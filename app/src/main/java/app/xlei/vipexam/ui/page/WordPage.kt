package app.xlei.vipexam.ui.page

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.*
import app.xlei.vipexam.R
import app.xlei.vipexam.data.TranslationResponse
import app.xlei.vipexam.logic.DB
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clipboard
import compose.icons.feathericons.Loader
import compose.icons.feathericons.Menu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@Entity(
    tableName = "words",
    indices = [Index(value = ["word"], unique = true)]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "word") val word: String,
    val created: Long = Calendar.getInstance().timeInMillis,
)

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)

    @Delete
    suspend fun delete(word: Word)

}

@Database(
    entities = [Word::class],
    version = 1
)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        var INSTANCE: WordDatabase? = null
        fun getDatabase(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context,
                    WordDatabase::class.java,
                    "word_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

class WordListViewModel : ViewModel() {
    private val _wordList = MutableStateFlow(emptyList<Word>())

    val wordList = _wordList.asStateFlow()

    init {
        getWordList()
    }

    fun getWordList() {
        viewModelScope.launch {
            DB.repository.getAllWords().flowOn(Dispatchers.IO).collect { wordList: List<Word> ->
                _wordList.update {
                    wordList
                }
            }
        }
    }

    fun addWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            DB.repository.addWord(word)
        }
    }

    fun removeWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            DB.repository.removeWord(word)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WordListPage(
    viewModel: WordListViewModel = viewModel(),
    openDrawer: () -> Unit,
) {
    val wordListState by viewModel.wordList.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var textToTranslate by remember { mutableStateOf("") }
    var showTranslationSheet by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.words)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = openDrawer
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Menu,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {

            LazyColumn {
                items(wordListState.size) { index ->
                    ListItem(
                        headlineContent = { Text(wordListState[index].word) },
                        supportingContent = { Text(Date(wordListState[index].created).toString()) },
                        trailingContent = { copyToClipboardButton(wordListState[index].word) },
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    textToTranslate = wordListState[index].word
                                    showTranslationSheet = true
                                },
                                onLongClick = { viewModel.removeWord(wordListState[index]) }
                            )
                    )
                }
                item {
                    if (wordListState.isEmpty())
                        Text(
                            text = stringResource(R.string.empty_wordlist_tips),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                }
            }

            if (showTranslationSheet)
                translationSheet(
                    textToTranslate
                ) {
                    showTranslationSheet = !showTranslationSheet
                }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun translationSheet(
    text: String,
    toggleBottomSheet: () -> Unit,
) {

    var translation by remember {
        mutableStateOf(
            TranslationResponse(
                code = 200,
                id = "",
                data = "",
                emptyList()
            )
        )
    }
    val coroutine = rememberCoroutineScope()
    DisposableEffect(Unit) {
        coroutine.launch {
            val res = app.xlei.vipexam.data.network.Repository.translateToZH(text)
            if (res != null) {
                translation = res
            }
        }
        onDispose { }
    }
    ModalBottomSheet(
        onDismissRequest = toggleBottomSheet,
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 120.dp)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
            )
            HorizontalDivider()
            Text(
                text = translation.data,
                fontSize = 24.sp,
            )
            LazyRow {
                if (translation.alternatives.isEmpty() && translation.data == "") {
                    item {
                        Icon(
                            imageVector = FeatherIcons.Loader,
                            contentDescription = null,
                        )
                    }
                } else {
                    items(translation.alternatives.size) {
                        Text(
                            text = translation.alternatives[it],
                            modifier = Modifier
                                .padding(end = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun copyToClipboardButton(
    text: String,
) {
    val context = LocalContext.current
    val string = stringResource(R.string.clipToClipboardSuccess)

    fun copyToClipboard(word: String) {
        context.copyToClipboard(word)
    }

    IconButton(
        onClick = {
            copyToClipboard(text)
            Toast.makeText(context, "$text $string", Toast.LENGTH_LONG).show()
        }
    ) {
        Icon(
            imageVector = FeatherIcons.Clipboard,
            contentDescription = null,
        )
    }
}

@SuppressLint("ServiceCast")
fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}