package com.example.devfeed.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val savedRepos by viewModel.savedRepos.collectAsState()
    val savedRepoIds by viewModel.savedRepoIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val shimmerBrush = rememberShimmerBrush()

    var selectedTab by remember { mutableIntStateOf(0) }
    val languages = listOf("All", "Kotlin", "Java", "Python", "Rust", "TypeScript", "Go", "C++")
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val splashGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF98CE9F),
            Color(0xFFA5DCD1)
        )
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                        clip = true
                    )
                    .background(splashGradient)
                    .padding(bottom = 12.dp)
            ) {
                LargeTopAppBar(
                    title = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = when (selectedTab) {
                                        0 -> "DevFeed"
                                        1 -> "Explore"
                                        else -> "Saved Repos"
                                    },
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp,
                                    color = Color(0xFF0F291E)
                                )
                                if (selectedTab == 0) {
                                    LivePulseIndicator()
                                }
                            }
                            Text(
                                text = when (selectedTab) {
                                    0 -> "Trending Repositories • Global Stream"
                                    1 -> "Live Search & Tech Stacks"
                                    else -> "Offline Cached Repositories (${savedRepos.size})"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2D5A46)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color(0xFF98CE9F)
                    ),
                    scrollBehavior = scrollBehavior
                )

                // ─── OFFLINE WARNING BANNER ───
                AnimatedVisibility(
                    visible = !isOnline,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        color = Color(0xFFD32F2F).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Offline",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "No Connection • Showing Cached Repos",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (selectedTab == 0) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(languages) { lang ->
                            val isSelected = lang == selectedLanguage
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onLanguageSelected(lang) },
                                label = {
                                    Text(
                                        text = lang,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.White.copy(alpha = 0.5f),
                                    selectedContainerColor = Color(0xFF184E34),
                                    selectedLabelColor = Color.White,
                                    labelColor = Color(0xFF184E34)
                                )
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        clip = true
                    ),
                containerColor = Color(0xFFE2EFE8),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = "Trending"
                        )
                    },
                    label = { Text("Trending") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF184E34),
                        indicatorColor = Color(0xFFB7DEC6)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF184E34),
                        indicatorColor = Color(0xFFB7DEC6)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = "Saved") },
                    label = { Text("Saved") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF184E34),
                        indicatorColor = Color(0xFFB7DEC6)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(splashGradient)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(200))
                },
                label = "ScreenSwitch"
            ) { tab ->
                when (tab) {
                    0 -> {
                        AnimatedContent(
                            targetState = state,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(400)) togetherWith
                                        fadeOut(animationSpec = tween(300))
                            },
                            label = "FeedState"
                        ) { targetState ->
                            when (targetState) {
                                is HomeUiState.Loading -> {
                                    LazyColumn(
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(6) {
                                            ShimmerRepoItem(brush = shimmerBrush)
                                        }
                                    }
                                }

                                is HomeUiState.Error -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = targetState.message,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(onClick = { viewModel.fetchTrendingRepos() }) {
                                            Text(text = "Retry Connection")
                                        }
                                    }
                                }

                                is HomeUiState.Success -> {
                                    PullToRefreshBox(
                                        isRefreshing = isRefreshing,
                                        onRefresh = { viewModel.refresh() },
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        LazyColumn(
                                            contentPadding = PaddingValues(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            items(
                                                items = targetState.repos,
                                                key = { repo -> repo.id }
                                            ) { repo ->
                                                AnimatedRepoCard(
                                                    repo = repo,
                                                    isBookmarked = savedRepoIds.contains(repo.id),
                                                    onBookmarkClick = {
                                                        viewModel.toggleBookmark(repo)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        ExploreScreen(
                            query = searchQuery,
                            onQueryChange = { viewModel.onSearchQueryChanged(it) },
                            isSearching = isSearching,
                            searchResults = searchResults,
                            savedRepoIds = savedRepoIds,
                            onBookmarkClick = { repo -> viewModel.toggleBookmark(repo) },
                            onTopicSelect = { topic ->
                                viewModel.onLanguageSelected(topic)
                                selectedTab = 0
                            },
                            shimmerBrush = shimmerBrush
                        )
                    }

                    2 -> {
                        SavedScreen(
                            savedRepos = savedRepos,
                            onRemoveClick = { entity -> viewModel.removeBookmark(entity) }
                        )
                    }
                }
            }
        }
    }
}