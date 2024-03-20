package app.xlei.vipexam.feature.wordlist

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.database.module.Word
import app.xlei.vipexam.core.network.module.EudicRemoteDatasource
import app.xlei.vipexam.feature.wordlist.constant.SortMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.PrintWriter
import javax.inject.Inject

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val repository: Repository<Word>,
) : ViewModel() {

    private val _wordList = MutableStateFlow(emptyList<Word>())
    private val _syncState = MutableStateFlow<SyncState<Nothing>>(SyncState.Default)
    val wordList
        get() = _wordList.asStateFlow()
    val syncState
        get() = _syncState.asStateFlow()

    var sortMethod = MutableStateFlow(SortMethod.OLD_TO_NEW)

    init {
        getWordList()
    }

    private fun getWordList() {
        viewModelScope.launch {
            repository.getAll()
                .flowOn(Dispatchers.IO)
                .collect { wordList: List<Word> ->
                    _wordList.update {
                        wordList
                    }
                }
        }
    }

    fun addWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.add(word)
        }
    }

    fun removeWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.remove(word)
        }
    }

    fun setSortMethod(sortMethod: SortMethod) {
        this.sortMethod.value = sortMethod
        viewModelScope.launch {
            repository.getAll()
                .flowOn(Dispatchers.IO)
                .map { words ->
                    when (sortMethod) {
                        SortMethod.OLD_TO_NEW -> words.sortedBy { it.created }
                        SortMethod.NEW_TO_OLD -> words.sortedByDescending { it.created }
                        SortMethod.A_TO_Z -> words.sortedBy { it.word }
                        SortMethod.Z_TO_A -> words.sortedByDescending { it.word }
                    }
                }
                .collect { sortedWordList: List<Word> ->
                    _wordList.update {
                        sortedWordList
                    }
                }
        }
    }

    fun exportWordsToCSV(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val words = repository.getAll().first()
            val csvFile = File(context.cacheDir, "words-${System.currentTimeMillis()}.csv")
            CSVPrinter(
                PrintWriter(csvFile),
                CSVFormat.DEFAULT.withHeader("id", "word", "created")
            ).use { printer ->
                words.forEach { word ->
                    printer.printRecord(word.id, word.word, word.created)
                }
            }
            withContext(Dispatchers.Main) {
                shareCSVFile(context, csvFile)
            }
        }
    }

    private fun shareCSVFile(context: Context, file: File) {
        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "text/csv"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Save file..."))
    }

    fun syncToEudic(apiKey: String) {
        apiKey.takeIf { it != "" }?.let {
            _syncState.update {
                SyncState.Syncing
            }
            viewModelScope.launch {
                EudicRemoteDatasource.api = it
                if (EudicRemoteDatasource.sync(_wordList.value.map { it.word }))
                    _syncState.update { SyncState.Success }
                else _syncState.update { SyncState.Error }
            }
        }
    }

    fun resetSyncState() {
        _syncState.update {
            SyncState.Default
        }
    }
}

sealed class SyncState<out T> {
    data object Default : SyncState<Nothing>()
    data object Success : SyncState<Nothing>()
    data object Syncing : SyncState<Nothing>()
    data object Error : SyncState<Nothing>()
}