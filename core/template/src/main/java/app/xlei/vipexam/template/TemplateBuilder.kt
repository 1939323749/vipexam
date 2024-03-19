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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.core.ui.VipexamArticleContainer
import app.xlei.vipexam.preference.LocalShowAnswer

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

        fun Title(title: String) = this.apply { this.title = title }
        fun Content(content: String) = this.apply { this.content = content }

        @Composable
        fun Render() {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    if (::title.isInitialized) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (::content.isInitialized) Text(content)
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
        var choice = mutableStateOf("")
        fun Question(question: String) = this.apply { this@QuestionBuilder.question = question }

        fun OptionA(option: String) = this.apply { optionA = option }
        fun OptionB(option: String) = this.apply { optionB = option }
        fun OptionC(option: String) = this.apply { optionC = option }
        fun OptionD(option: String) = this.apply { optionD = option }
        fun Options(options: List<String>) = this.apply { this@QuestionBuilder.options = options }
        fun Answer(answer: String) = this.apply { this@QuestionBuilder.answer = answer }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun Render() {
            var showOptions by remember {
                mutableStateOf(false)
            }
            val showAnswer = LocalShowAnswer.current.isShowAnswer()

            Column {
                VipexamArticleContainer(
                    onDragContent = if (::question.isInitialized) question + "\n\n" + answer else null
                ) {
                    Column {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            if (::question.isInitialized)
                                Text(
                                    text = question,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(start = 4.dp, end = 4.dp)
                                )
                            choice.takeIf { it.value != "" }?.let {
                                SuggestionChip(onClick = { }, label = { Text(choice.value) })
                            }
                            if (::optionA.isInitialized) Card(
                                modifier = Modifier.clickable { choice.value = optionA }
                            ) { Text(text = "A. $optionA") }

                            if (::optionB.isInitialized) Card(
                                modifier = Modifier.clickable { choice.value = optionB }
                            ) { Text("B. $optionB") }

                            if (::optionC.isInitialized) Card(
                                modifier = Modifier.clickable { choice.value = optionC }
                            ) { Text("C. $optionC") }

                            if (::optionD.isInitialized) Card(
                                modifier = Modifier.clickable { choice.value = optionD }
                            ) { Text("D. $optionD") }

                            if (::options.isInitialized)
                                options.forEach { option ->
                                    Card(
                                        modifier = Modifier
                                            .clickable { choice.value = option }
                                    ) { Text(option) }
                                }
                        }
                        if (showAnswer && ::answer.isInitialized)
                            Text(text = answer, modifier = Modifier.padding(24.dp))
                    }

                }
                if (showOptions) ModalBottomSheet(onDismissRequest = { showOptions = false }) {
                    if (::optionA.isInitialized) Button(onClick = {
                        choice.value = optionA
                        showOptions = false
                    }) {
                        Text("A. $optionA")
                    }
                    if (::optionB.isInitialized) Button(onClick = { choice.value = optionB }) {
                        Text("B. $optionB")
                    }
                    if (::optionC.isInitialized) Button(onClick = { choice.value = optionC }) {
                        Text("C. $optionC")
                    }
                    if (::optionD.isInitialized) Button(onClick = { choice.value = optionD }) {
                        Text("D. $optionD")
                    }
                }
            }

        }
    }

    @Composable
    fun Render(modifier: Modifier) {
        Column(
            modifier = modifier
        ) {
            LazyColumn {
                item { article.Render() }
                item { question.Render() }
                questions.forEach { item { it.Render() } }
            }
        }

    }

}

@Composable
fun Template(modifier: Modifier = Modifier, block: TemplateBuilder.() -> Unit) =
    TemplateBuilder().apply(block).Render(modifier)

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