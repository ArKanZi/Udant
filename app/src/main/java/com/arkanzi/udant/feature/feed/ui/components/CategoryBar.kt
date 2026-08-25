package com.arkanzi.udant.feature.feed.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun CategoryBar(
    categories: List<String> = listOf("My Feed","Top Stories","Most Recent","India","World","Business","Sports","Cricket","Tech","Science","Environment","Entertainment","Life & Style","Education","US","Most Read","Most Shared","Most Commented",)
) {
    var selectedCategory by rememberSaveable {
        mutableStateOf("My Feed")
    }
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center){
        LazyRow {
            items(categories){ category ->
                CategoryItem(
                    category = category,
                    selected = category == selectedCategory,
                    action = { selectedCategory = category }
                )
            }
        }
    }


}

@Preview(showBackground= true)
@Composable
fun CategoryBarPreview() {
    CategoryBar()
}
