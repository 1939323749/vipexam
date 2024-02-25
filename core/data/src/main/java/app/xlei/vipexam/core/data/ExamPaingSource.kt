package app.xlei.vipexam.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.xlei.vipexam.core.data.repository.ExamHistoryRepository
import app.xlei.vipexam.core.network.module.NetWorkRepository
import app.xlei.vipexam.core.network.module.getExamList.Exam
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface ExamListRepository {
    suspend fun getExamList(): Flow<PagingData<ExamListItem>>
    suspend fun search(query: String): Flow<PagingData<ExamListItem>>
}

interface ExamListRemoteDataSource {

    suspend fun getExamList(
        pageNumber: Int
    ): List<Exam>

    suspend fun search(
        pageNumber: Int,
        query: String
    ): List<Exam>

}

object ExamListApi {
    private var type = "4"

    fun setType(type: String) {
        ExamListApi.type = type
    }

    suspend fun getExamList(pageNumber: Int): List<Exam>{
        var examList = listOf<Exam>()
        NetWorkRepository.getExamList(
            page = pageNumber.toString(),
            examStyle = type,
            examTypeEName = "ve01002"
        )
            .onSuccess {
                examList = it.list
            }
            .onFailure {
                examList = emptyList()
            }
        return examList
    }

    suspend fun searchExam(
        pageNumber: Int,
        searchContent: String,
    ): List<Exam>{
        var examList = listOf<Exam>()
        NetWorkRepository.searchExam(
            page = pageNumber.toString(),
            searchContent = searchContent
        )
            .onSuccess {
                examList = it.list
            }
            .onFailure {
                examList = emptyList()
            }
        return examList
    }
}

class ExamListRemoteDataSourceImpl : ExamListRemoteDataSource {
    override suspend fun getExamList(
        pageNumber: Int
    ): List<Exam> {
        return ExamListApi.getExamList(pageNumber = pageNumber)
    }

    override suspend fun search(pageNumber: Int, query: String): List<Exam> {
        return ExamListApi.searchExam(
            pageNumber = pageNumber,
            searchContent = query,
        )
    }
}
class ExamListRepositoryImpl @Inject constructor(
    private val remoteDataSource: ExamListRemoteDataSource,
    private val examHistoryRepository: ExamHistoryRepository,
    private val coroutineScope: CoroutineScope,
) : ExamListRepository {
    override suspend fun getExamList(): Flow<PagingData<ExamListItem>> {
        return Pager(
            config = PagingConfig(pageSize = 9999, prefetchDistance = 2),
            pagingSourceFactory = {
                ExamPagingSource(remoteDataSource,examHistoryRepository,coroutineScope)
            }
        ).flow
    }

    override suspend fun search(query: String): Flow<PagingData<ExamListItem>> {
        return Pager(
            config = PagingConfig(pageSize = 9999, prefetchDistance = 10),
            pagingSourceFactory = {
                SearchExamPagingSource(remoteDataSource,examHistoryRepository,coroutineScope,query)
            }
        ).flow
    }
}

data class ExamListItem(
    val exam: Exam,
    val lastOpen: Long?,
)

class SearchExamPagingSource @Inject constructor(
    private val remoteDataSource: ExamListRemoteDataSource,
    private val examHistoryRepository: ExamHistoryRepository,
    private val coroutineScope: CoroutineScope,
    private val query: String,
): PagingSource<Int, ExamListItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExamListItem> {
        return try {
            val currentPage = params.key ?: 1
            val examList = remoteDataSource.search(
                pageNumber = currentPage,
                query = query,
            )
            val examItemList = examList.map { exam ->
                coroutineScope.async {
                    val lastOpen = withContext(Dispatchers.IO) {
                        examHistoryRepository.getExamHistoryByExamId(exam.examid)?.lastOpen
                    }
                    ExamListItem(
                        exam = exam,
                        lastOpen = lastOpen
                    )
                }
            }.awaitAll()

            LoadResult.Page(
                data = examItemList,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (examList.isEmpty()) null else currentPage + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ExamListItem>): Int? {
        return state.anchorPosition
    }

}

class ExamPagingSource @Inject constructor(
    private val remoteDataSource: ExamListRemoteDataSource,
    private val examHistoryRepository: ExamHistoryRepository,
    private val coroutineScope: CoroutineScope,
): PagingSource<Int, ExamListItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExamListItem> {
        return try {
            val currentPage = params.key ?: 1
            val examList = remoteDataSource.getExamList(
                pageNumber = currentPage,
            )
            val examItemList = examList.map { exam ->
                    coroutineScope.async {
                        val lastOpen = withContext(Dispatchers.IO) {
                            examHistoryRepository.getExamHistoryByExamId(exam.examid)?.lastOpen
                        }
                        ExamListItem(
                            exam = exam,
                            lastOpen = lastOpen
                        )
                    }
                }.awaitAll()

            LoadResult.Page(
                data = examItemList,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (examList.isEmpty()) null else currentPage + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ExamListItem>): Int? {
        return state.anchorPosition
    }

}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesExamListRemoteDataSource(): ExamListRemoteDataSource {
        return ExamListRemoteDataSourceImpl()
    }
    @Singleton
    @Provides
    fun providesExamListRepository(
        examListRemoteDataSource: ExamListRemoteDataSource,
        examHistoryRepository: ExamHistoryRepository,
        coroutineScope: CoroutineScope,
    ): ExamListRepository {
        return ExamListRepositoryImpl(examListRemoteDataSource,examHistoryRepository,coroutineScope)
    }

    @Singleton
    @Provides
    fun providesGetMoviesUseCase(
        examListRepository: ExamListRepository
    ): GetExamListUseCase {
        return GetExamListUseCase(examListRepository)
    }
}

interface BaseUseCase<In, Out>{
    suspend fun execute(input: In): Out
}

class GetExamListUseCase @Inject constructor(
    private val repository: ExamListRepository
) : BaseUseCase<Unit, Flow<PagingData<ExamListItem>>> {
    override suspend fun execute(input: Unit): Flow<PagingData<ExamListItem>> {
        return repository.getExamList()
    }
}