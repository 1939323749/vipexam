package app.xlei.vipexam

//@HiltViewModel
//class MainActivityViewModel @Inject constructor(
//    userDataRepository: UserDataRepository,
//) : ViewModel() {
//    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
//        MainActivityUiState.Success(it)
//    }.stateIn(
//        scope = viewModelScope,
//        initialValue = MainActivityUiState.Loading,
//        started = SharingStarted.WhileSubscribed(5_000),
//    )
//}
//
//sealed interface MainActivityUiState {
//    data object Loading : MainActivityUiState
//    data class Success(val userData: UserData) : MainActivityUiState
//}
