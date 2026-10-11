package com.arkanzi.udant.feature.feed.ui.components



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkanzi.udant.core.model.FeedCategory
import com.arkanzi.udant.core.ui.components.TextButtonItem
import com.arkanzi.udant.feature.feed.model.FeedTarget
import com.rometools.rome.feed.atom.Feed


@Composable
fun CategoryBar(
    categories: List<FeedCategory>,
    selectedTarget: FeedTarget?,
    onTargetSelected: (FeedTarget) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow {
            item {
                TextButtonItem(
                    itemName = "My Feed",
                    selected = selectedTarget is FeedTarget.All,
                    action = {
                        onTargetSelected(FeedTarget.All)
                    }
                )
            }
            items(categories) { category ->
                TextButtonItem(
                    itemName = category.name,
                    selected = selectedTarget == FeedTarget.Category(category.id),
                    action = {
                        onTargetSelected(
                            FeedTarget.Category(category.id)
                        )
                    }
                )
            }
        }
    }
}

