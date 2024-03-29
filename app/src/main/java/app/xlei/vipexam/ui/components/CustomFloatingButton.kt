package app.xlei.vipexam.ui.components

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.xlei.vipexam.R

/**
 * Custom floating action button
 *
 * @param expandable 是否能展开
 * @param onFabClick 按钮点击事件
 * @param iconUnExpanded 未展开图标
 * @param iconExpanded 展开图标
 * @param items 展开显示的内容，按从上到下排列
 * @param onItemClick 内容点击事件
 * @receiver
 * @receiver
 */
@Composable
fun CustomFloatingActionButton(
    expandable: Boolean,
    onFabClick: () -> Unit,
    iconUnExpanded: ImageVector,
    iconExpanded: ImageVector,
    items: List<Pair<String, String>>,
    onItemClick: (String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    if (!expandable) { // Close the expanded fab if you change to non expandable nav destination
        isExpanded = false
    }

    val fabSize = 56.dp
    val expandedFabWidth by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f), label = ""
    )
    val expandedFabHeight by animateDpAsState(
        targetValue = if (isExpanded) 56.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f), label = ""
    )

    Column {
        Column(
            modifier = Modifier
                .offset(y = (16).dp)
                .size(
                    width = expandedFabWidth,
                    height = (animateDpAsState(
                        if (isExpanded) 225.dp else 0.dp,
                        animationSpec = spring(dampingRatio = 4f), label = ""
                    )).value
                )
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp)
                )
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 16.dp)
            ) {
                items.forEach{

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(fabSize)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                onItemClick(it.first)
                                isExpanded = false
                            }
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = it.second,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                onFabClick()
                if (expandable) {
                    isExpanded = !isExpanded
                }
            },
            modifier = Modifier
                .width(expandedFabWidth)
                .height(expandedFabHeight),
            shape = RoundedCornerShape(16.dp)

        ) {
            Icon(
                imageVector = if(isExpanded){iconExpanded}else{iconUnExpanded},
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded) -70.dp else 0.dp,
                            animationSpec = spring(dampingRatio = 3f), label = ""
                        ).value
                    )
            )

            Text(
                text = stringResource(R.string.close_button),
                softWrap = false,
                modifier = Modifier
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded) 10.dp else 50.dp,
                            animationSpec = spring(dampingRatio = 3f), label = ""
                        ).value
                    )
                    .alpha(
                        animateFloatAsState(
                            targetValue = if (isExpanded) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = if (isExpanded) 350 else 100,
                                delayMillis = if (isExpanded) 100 else 0,
                                easing = EaseIn
                            ), label = ""
                        ).value
                    )
            )
        }
    }
}