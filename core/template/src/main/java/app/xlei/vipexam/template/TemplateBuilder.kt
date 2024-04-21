package app.xlei.vipexam.template

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.core.ui.VipexamArticleContainer
import app.xlei.vipexam.preference.LocalShowAnswer
import coil.compose.AsyncImage

/**
 * Template builder
 * 问题模板DSL
 * @constructor Create empty Template builder
 * @sample [TemplateBuilderSample]
 */
class TemplateBuilder {
    inner class ArticleBuilder {
        lateinit var title: String
        lateinit var content: String
        lateinit var contentPic: String

        fun Title(title: String) = this.apply { this.title = title }
        fun Content(content: String) = this.apply { this.content = content }
        fun ContentPic(contentPic: String) = this.apply { this.contentPic = contentPic }

        @Composable
        fun Render(index: Int? = null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    if (::title.isInitialized) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (::content.isInitialized) {
                    Text(
                        text = (if (index != null && !::title.isInitialized) "$index. " else "") + content
                    )
                }

                if (::contentPic.isInitialized) {
                    contentPic.split(",").forEach {
                        Row {
                            Spacer(Modifier.weight(2f))
                            AsyncImage(
                                model = "https://rang.vipexam.org/images/$it.jpg",
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .align(Alignment.CenterVertically)
                                    .weight(6f)
                                    .fillMaxWidth()
                            )
                            Spacer(Modifier.weight(2f))
                        }
                    }
                }
            }
        }
    }

    val article by lazy { ArticleBuilder() }
    val question by lazy { QuestionBuilder() }
    var questions = emptyList<QuestionBuilder>()

    fun Article(block: ArticleBuilder.() -> Unit) = this.article.apply(block)
    fun Question(block: QuestionBuilder.() -> Unit) = this.question.apply(block)
    fun Questions(count: Int, block: QuestionBuilder.(index: Int) -> Unit) =
        repeat(count) {
            this.questions += QuestionBuilder()
        }.also {
            this.questions.forEachIndexed { index, questionBuilder ->
                it.apply { block(questionBuilder, index) }
            }
        }

    inner class QuestionBuilder {
        private lateinit var question: String
        private lateinit var optionA: String
        private lateinit var optionB: String
        private lateinit var optionC: String
        private lateinit var optionD: String
        private lateinit var options: List<String>
        private lateinit var answer: String
        private lateinit var description: String

        private lateinit var questionPic: String
        private lateinit var answerPic: String
        private lateinit var descriptionPic: String

        var choice = mutableStateOf("")
        fun Question(question: String) =
            this.apply { this@QuestionBuilder.question = question.removeSuffix("[*]") }

        fun QuestionPic(pic: String) = this.apply { this.questionPic = pic }

        fun OptionA(option: String) = this.apply { optionA = option }
        fun OptionB(option: String) = this.apply { optionB = option }
        fun OptionC(option: String) = this.apply { optionC = option }
        fun OptionD(option: String) = this.apply { optionD = option }
        fun Options(options: List<String>) = this.apply { this@QuestionBuilder.options = options }
        fun Answer(answer: String) =
            this.apply { this@QuestionBuilder.answer = answer.removeSuffix("[*]") }

        fun AnswerPic(pic: String) = this.apply { this.answerPic = pic }
        fun Description(desc: String) = this.apply { this.description = desc.removeSuffix("[*]") }
        fun DescriptionPic(descPic: String) = this.apply { this.descriptionPic = descPic }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun Render(index: Int? = null) {
            var showOptions by remember {
                mutableStateOf(false)
            }
            val showAnswer = LocalShowAnswer.current.isShowAnswer()

            Column {
                VipexamArticleContainer(
                    onDragContent = (if (::question.isInitialized) question else "" + "\n\n") +
                            (if (::answer.isInitialized && showAnswer) run {
                                answer + "\n" + if (::description.isInitialized) description else ""
                            } else "")
                ) {
                    Column {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            if (::question.isInitialized)
                                Text(
                                    text = (if (index != null) "$index " else "") + question,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(start = 4.dp, end = 4.dp)
                                )

                            if (::questionPic.isInitialized) {
                                questionPic.split(",").forEach {
                                    Row {
                                        Spacer(Modifier.weight(2f))
                                        AsyncImage(
                                            model = "https://rang.vipexam.org/images/$it.jpg",
                                            contentDescription = null,
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier
                                                .padding(top = 12.dp)
                                                .align(Alignment.CenterVertically)
                                                .weight(6f)
                                                .fillMaxWidth()
                                        )
                                        Spacer(Modifier.weight(2f))
                                    }
                                }
                            }

                            choice.takeIf { it.value != "" }?.let {
                                SuggestionChip(onClick = { }, label = { Text(choice.value) })
                            }
                            if (::optionA.isInitialized && optionA != "") Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)
                                    .clickable { choice.value = "A" }
                            ) { Text(text = "A. $optionA") }

                            if (::optionB.isInitialized && optionB != "") Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)
                                    .clickable { choice.value = "B" }
                            ) { Text("B. $optionB") }

                            if (::optionC.isInitialized && optionC != "") Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)
                                    .clickable { choice.value = "C" }
                            ) { Text("C. $optionC") }

                            if (::optionD.isInitialized && optionD != "") Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)
                                    .clickable { choice.value = "D" }
                            ) { Text("D. $optionD") }

                            if (::options.isInitialized)
                                options.forEach { option ->
                                    Card(
                                        modifier = Modifier
                                            .clickable { choice.value = option }
                                    ) { Text(option) }
                                }
                        }
                        if (showAnswer && ::answer.isInitialized) {
                            Text(text = answer, modifier = Modifier.padding(start = 24.dp))
                            if (::description.isInitialized) {
                                Text(text = description, modifier = Modifier.padding(start = 24.dp))
                                if (::descriptionPic.isInitialized) {
                                    val pics = descriptionPic.split(',')
                                    pics.forEach {
                                        Row {
                                            Spacer(Modifier.weight(2f))
                                            AsyncImage(
                                                model = "https://rang.vipexam.org/images/$it.jpg",
                                                contentDescription = null,
                                                contentScale = ContentScale.FillWidth,
                                                modifier = Modifier
                                                    .padding(top = 24.dp)
                                                    .align(Alignment.CenterVertically)
                                                    .weight(6f)
                                                    .fillMaxWidth()
                                            )
                                            Spacer(Modifier.weight(2f))
                                        }
                                    }
                                }
                            }

                            if (::answerPic.isInitialized) {
                                val pics = answerPic.split(',')
                                pics.forEach {
                                    Row {
                                        Spacer(Modifier.weight(2f))
                                        AsyncImage(
                                            model = "https://rang.vipexam.org/images/$it.jpg",
                                            contentDescription = null,
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier
                                                .padding(top = 24.dp)
                                                .align(Alignment.CenterVertically)
                                                .weight(6f)
                                                .fillMaxWidth()
                                        )
                                        Spacer(Modifier.weight(2f))
                                    }
                                }
                            }
                        }
                    }
                }

                if (showOptions) ModalBottomSheet(onDismissRequest = { showOptions = false }) {
                    if (::optionA.isInitialized && optionA != "") Button(onClick = {
                        choice.value = optionA
                        showOptions = false
                    }) {
                        Text("A. $optionA")
                    }
                    if (::optionB.isInitialized && optionB != "") Button(onClick = {
                        choice.value = optionB
                    }) {
                        Text("B. $optionB")
                    }
                    if (::optionC.isInitialized && optionC != "") Button(onClick = {
                        choice.value = optionC
                    }) {
                        Text("C. $optionC")
                    }
                    if (::optionD.isInitialized && optionD != "") Button(onClick = {
                        choice.value = optionD
                    }) {
                        Text("D. $optionD")
                    }
                }
            }

        }
    }

    @Composable
    fun Render(
        modifier: Modifier,
        isSubQuestion: Boolean,
        index: Int? = null
    ) {
        Column(
            modifier = modifier
        ) {
            if (isSubQuestion) {
                Column {
                    article.Render(index)
                    question.Render()
                    questions.forEachIndexed { index, question ->
                        question.Render(index + 1)
                    }
                }
            } else {
                LazyColumn {
                    item { article.Render(index) }
                    item { question.Render() }
                    questions.forEachIndexed { index, question ->
                        item { question.Render(index + 1) }
                    }
                }
            }
        }
    }
}

@Composable
fun Template(
    modifier: Modifier = Modifier,
    isSubQuestion: Boolean = false,
    index: Int? = null,
    block: TemplateBuilder.() -> Unit
) =
    TemplateBuilder().apply(block).Render(modifier, isSubQuestion, index)

@Composable
private fun TemplateBuilderSample() {
    Template {
        Article {
            Title("title")
            Content("content")
        }
        Questions(5) { index ->
            Question(index.toString())
            OptionA("A")
            OptionB("B")
            OptionC("C")
            OptionD("D")
            Answer("answer")
        }
    }
}