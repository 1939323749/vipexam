package app.xlei.vipexam.core.data.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.xlei.vipexam.core.network.module.NetWorkRepository
import app.xlei.vipexam.core.network.module.momoLookUp.Phrase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface MomoLookUpRepository {
    suspend fun search(): Flow<PagingData<Phrase>>
}

interface MomoLookUpRemoteDataSource {
    suspend fun search(
        offset: Int,
    ): List<Phrase>
}

class MomoLookUpRepositoryImpl @Inject constructor(
    private val remoteDataSource: MomoLookUpRemoteDataSource
) : MomoLookUpRepository {
    override suspend fun search(): Flow<PagingData<Phrase>> {
        return Pager(
            config = PagingConfig(pageSize = 9999, prefetchDistance = 2),
            pagingSourceFactory = {
                MomoLookUpPagingSource(remoteDataSource)
            }
        ).flow
    }
}

enum class Source(val value: String, val displayName: String) {
    ALL("ALL", "全部"),
    CET4("CET4", "四级"),
    CET6("CET6", "六级"),
    KAOYAN("POSTGRADUATE", "考研")
}


object MomoLookUpApi {
    var keyword = ""
    var source = Source.ALL

    suspend fun search(offset: Int) =
        NetWorkRepository.momoLookUp(offset = offset, keyword = keyword, paperType = source.value)
}

class MomoLookUpRemoteDataSourceImpl : MomoLookUpRemoteDataSource {
    override suspend fun search(offset: Int): List<Phrase> {
        var phrases = listOf<Phrase>()

        MomoLookUpApi
            .search(offset = offset)
            .onSuccess {
                phrases = it.data.phrases
            }
            .onFailure {
                phrases = emptyList()
            }
        return phrases
    }
}

class MomoLookUpPagingSource @Inject constructor(
    private val remoteDataSource: MomoLookUpRemoteDataSource
) : PagingSource<Int, Phrase>() {
    override fun getRefreshKey(state: PagingState<Int, Phrase>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Phrase> {
        return try {
            val offset = params.key ?: 0
            val searchResult = remoteDataSource.search(
                offset = offset
            )
            println(offset)
            LoadResult.Page(
                data = searchResult,
                prevKey = if (offset == 0) null else offset - 10,
                nextKey = if (searchResult.isEmpty()) null else offset + 10
            )
        } catch (e: Exception) {
            println(e)
            LoadResult.Error(e)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object MomoLookModule {
    @Singleton
    @Provides
    fun providesMomoLookUpRemoteDataSource(): MomoLookUpRemoteDataSource {
        return MomoLookUpRemoteDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providesMomoLookUpRepository(
        remoteDataSource: MomoLookUpRemoteDataSource
    ): MomoLookUpRepository {
        return MomoLookUpRepositoryImpl(remoteDataSource)
    }
}

