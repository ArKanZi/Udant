package com.arkanzi.udant.feature.feed.ui
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.pager.VerticalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import coil3.compose.AsyncImage
//import com.arkanzi.udant.core.model.Article
//import com.arkanzi.udant.feature.feed.viewmodel.FeedViewModel
//
//@Composable
//fun FeedScreen(
//    viewModel: FeedViewModel = hiltViewModel()
//) {
//
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    val pagerState = rememberPagerState(
//        pageCount = { uiState.articles.size }
//    )
//
//    VerticalPager(
//        state = pagerState,
//        modifier = Modifier.fillMaxSize()
//    ) { page ->
//
//        val article = uiState.articles[page]
//
//        FeedPage(article = article)
//    }
//}
//
//@Composable
//private fun FeedPage(
//    article: Article
//) {
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//    ) {
//
//        article.imageUrl?.let { imageUrl ->
//
//            AsyncImage(
//                model = imageUrl,
//                contentDescription = article.title,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    Color.Black.copy(alpha = 0.45f)
//                )
//        )
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .statusBarsPadding()
//                .padding(20.dp),
//            verticalArrangement = Arrangement.Bottom
//        ) {
//
//            Text(
//                text = article.sourceName,
//                style = MaterialTheme.typography.labelLarge,
//                color = Color.White
//            )
//
//            Text(
//                text = article.title,
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//                color = Color.White,
//                maxLines = 3,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//
//            Text(
//                text = article.summary,
//                style = MaterialTheme.typography.bodyLarge,
//                color = Color.White,
//                maxLines = 6,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier.padding(top = 12.dp)
//            )
//
//            Text(
//                text = article.author ?: "Unknown",
//                style = MaterialTheme.typography.labelMedium,
//                color = Color.LightGray,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//
//            Text(
//                text = "Swipe for next",
//                style = MaterialTheme.typography.labelSmall,
//                color = Color.LightGray,
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(top = 24.dp, bottom = 12.dp)
//            )
//        }
//    }
//}