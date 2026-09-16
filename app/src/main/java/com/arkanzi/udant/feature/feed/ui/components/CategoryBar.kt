package com.arkanzi.udant.feature.feed.ui.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun CategoryBar(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
){

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        LazyRow {
            items(categories){ category ->
                CategoryItem(
                    category = category,
                    selected = category == selectedCategory,
                    action = {
                        onCategorySelected(category) }
                )
            }
        }
    }


}

@Preview(showBackground= true)
@Composable
fun CategoryBarPreview() {
    CategoryBar(
        listOf(
            "My Feed",
            "Top Stories",
            "Most Recent",
            "India",
            "World",
            "Business",
            "Sports",
            "Cricket",
            "Tech",
            "Science",
            "Environment",
            "Entertainment",
            "Life & Style",
            "Education",
            "US",
            "Most Read",
            "Most Shared",
            "Most Commented",
        ),
        selectedCategory = "My Feed",
        onCategorySelected = {}
    )
}
