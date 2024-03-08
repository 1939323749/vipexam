package app.xlei.vipexam.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import app.xlei.vipexam.core.ui.ErrorMessage
import app.xlei.vipexam.core.ui.LoadingNextPageItem
import app.xlei.vipexam.core.ui.PageLoader
import app.xlei.vipexam.ui.components.vm.SearchViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Search
import compose.icons.feathericons.X

/**
 * Exam search bar
 *
 * @param modifier
 * @param viewModel 搜索vm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSearchBar(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
){
    var queryString by remember {
        mutableStateOf("")
    }

    var isActive by remember {
        mutableStateOf(false)
    }
    SearchBar(
        query = queryString,
        onQueryChange = { queryString = it },
        onSearch = { viewModel.search(queryString) },
        active = isActive,
        onActiveChange = { isActive = it },
        leadingIcon = { IconButton(onClick = { isActive = true }) {
            Icon(imageVector = FeatherIcons.Search, contentDescription = null)
        } },
        trailingIcon = {
            if (isActive)
                IconButton(onClick = {
                    isActive = false
                    queryString = ""
                }) {
                    Icon(imageVector = FeatherIcons.X, contentDescription = null) } },
        modifier = modifier,
    ) {
        val results = viewModel.examListState.collectAsLazyPagingItems()
        LazyColumn {
            items(results.itemCount) {
                ListItem(
                    headlineContent = { results[it]?.exam?.let { exam->
                        Text(text = exam.examname) } },
                    modifier = Modifier
                        .clickable {
                            results[it]?.exam?.let { exam ->
                                viewModel.download(
                                    fileName = exam.examname,
                                    examId = exam.examid,
                                )
                            }
                        }
                )
            }
            results.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
                    }

                    loadState.refresh is LoadState.Error -> {
                        val error = results.loadState.refresh as LoadState.Error
                        item {
                            ErrorMessage(
                                modifier = Modifier.fillParentMaxSize(),
                                message = error.error.localizedMessage!!,
                                onClickRetry = { retry() })
                        }
                    }

                    loadState.append is LoadState.Loading -> {
                        item { LoadingNextPageItem(modifier = Modifier) }
                    }

                    loadState.append is LoadState.Error -> {
                        val error = results.loadState.append as LoadState.Error
                        item {
                            ErrorMessage(
                                modifier = Modifier,
                                message = error.error.localizedMessage!!,
                                onClickRetry = { retry() })
                        }
                    }
                }
            }
        }
    }
}

