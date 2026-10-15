package com.example.devfeed.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfeed.data.local.RepoEntity
import com.example.devfeed.data.local.dao.RepoDao
import com.example.devfeed.data.remote.GitHubApi
import com.example.devfeed.data.remote.dto.GithubRepoDto
import com.example.devfeed.util.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val repos: List<GithubRepoDto>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: GitHubApi,
    private val repoDao: RepoDao,
    private val networkObserver: NetworkObserver
) : ViewModel() {

    // ─── NETWORK CONNECTION OBSERVER ───
    val isOnline: StateFlow<Boolean> = networkObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("All")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // ─── SEARCH STATES ───
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GithubRepoDto>>(emptyList())
    val searchResults: StateFlow<List<GithubRepoDto>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // ─── ROOM DATABASE OBSERVERS ───
    val savedRepos: StateFlow<List<RepoEntity>> = repoDao.getAllSavedRepos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedRepoIds: StateFlow<Set<Long>> = savedRepos
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    init {
        fetchTrendingRepos()
        observeSearchQuery()
    }

    private fun buildQuery(language: String): String {
        return if (language.equals("All", ignoreCase = true)) {
            "stars:>1000"
        } else {
            "language:${language.lowercase()} stars:>1000"
        }
    }

    fun fetchTrendingRepos() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val query = buildQuery(_selectedLanguage.value)
                val response = api.getRepositories(query = query)
                _uiState.value = HomeUiState.Success(repos = response.items)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    message = e.localizedMessage ?: "Failed to load feed"
                )
            }
        }
    }

    fun onLanguageSelected(language: String) {
        if (_selectedLanguage.value == language) return
        _selectedLanguage.value = language
        fetchTrendingRepos()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val query = buildQuery(_selectedLanguage.value)
                val response = api.getRepositories(query = query)
                _uiState.value = HomeUiState.Success(repos = response.items)
            } catch (e: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    val trimmed = query.trim()
                    if (trimmed.length >= 2) {
                        performSearch(trimmed)
                    } else {
                        _searchResults.value = emptyList()
                        _isSearching.value = false
                    }
                }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.trim().length >= 2) {
            _isSearching.value = true
        } else {
            _isSearching.value = false
            _searchResults.value = emptyList()
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                val response = api.getRepositories(query = query)
                _searchResults.value = response.items
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun toggleBookmark(repo: GithubRepoDto) {
        viewModelScope.launch {
            val isAlreadySaved = savedRepoIds.value.contains(repo.id)
            val entity = RepoEntity(
                id = repo.id,
                name = repo.name,
                fullName = repo.fullName,
                description = repo.description,
                language = repo.language,
                stars = repo.stars,
                htmlUrl = repo.htmlUrl,
                ownerUsername = repo.owner.login,
                ownerAvatarUrl = repo.owner.avatarUrl
            )
            if (isAlreadySaved) {
                repoDao.unbookmarkRepo(entity)
            } else {
                repoDao.bookmarkRepo(entity)
            }
        }
    }

    fun removeBookmark(entity: RepoEntity) {
        viewModelScope.launch {
            repoDao.unbookmarkRepo(entity)
        }
    }
}