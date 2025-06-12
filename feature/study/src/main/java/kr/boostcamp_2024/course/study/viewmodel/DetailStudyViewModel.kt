package kr.boostcamp_2024.course.study.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.boostcamp_2024.course.designsystem.ui.base.BaseViewModel
import kr.boostcamp_2024.course.domain.model.Category
import kr.boostcamp_2024.course.domain.model.StudyGroup
import kr.boostcamp_2024.course.domain.model.User
import kr.boostcamp_2024.course.domain.repository.AuthRepository
import kr.boostcamp_2024.course.domain.repository.CategoryRepository
import kr.boostcamp_2024.course.domain.repository.NotificationRepository
import kr.boostcamp_2024.course.domain.repository.QuestionRepository
import kr.boostcamp_2024.course.domain.repository.QuizRepository
import kr.boostcamp_2024.course.domain.repository.StorageRepository
import kr.boostcamp_2024.course.domain.repository.StudyGroupRepository
import kr.boostcamp_2024.course.domain.repository.UserOmrRepository
import kr.boostcamp_2024.course.domain.repository.UserRepository
import kr.boostcamp_2024.course.study.R
import kr.boostcamp_2024.course.study.navigation.StudyRoute
import javax.inject.Inject

data class DetailStudyUiState(
    val isLoading: Boolean = false,
    val currentGroup: StudyGroup? = null,
    val errorMessageId: Int? = null,
    val categories: List<Category> = emptyList(),
    val users: List<User> = emptyList(),
    val owner: User? = null,
    val userId: String? = null,
    val isDeleteStudyGroupSuccess: Boolean = false,
    val isLeaveStudyGroupSuccess: Boolean = false,
    val email: String? = null,
    val isEmailValid: Boolean = false,
)

@HiltViewModel
class DetailStudyViewModel @Inject constructor(
    private val studyGroupRepository: StudyGroupRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val userOmrRepository: UserOmrRepository,
    private val storageRepository: StorageRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    private val studyGroupId: String = savedStateHandle.toRoute<StudyRoute>().studyGroupId

    private val _uiState: MutableStateFlow<DetailStudyUiState> = MutableStateFlow(DetailStudyUiState())
    val uiState: StateFlow<DetailStudyUiState> = _uiState.asStateFlow()

    fun initViewmodel() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val userId = authRepository.getUserKey()
                val currentGroup = studyGroupRepository.getStudyGroup(studyGroupId)
                val categories = categoryRepository.getCategories(currentGroup.categories)
                val users = userRepository.getUsers(currentGroup.users)
                val owner = userRepository.getUser(currentGroup.ownerId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentGroup = currentGroup,
                        categories = categories,
                        users = users,
                        owner = owner,
                        userId = userId,
                    )
                }
            } catch (e: Exception) {
                Log.e("DetailStudyViewModel", "initViewmodel: ${e.message}", e)
                handleError(null, e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addNotification(groupId: String, email: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val user = userRepository.findUserByEmail(email)
                _uiState.value.currentGroup?.let { currentGroup ->
                    if (currentGroup.users.contains(user.id).not()) {
                        notificationRepository.addNotification(groupId, user.id)
                    }
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e("DetailStudyViewModel", "addNotification: ${e.message}", e)
                handleError(null, e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteStudyGroupMember(userId: String, studyGroupId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                studyGroupRepository.deleteUser(studyGroupId, userId)
                userRepository.deleteStudyGroupUser(userId, studyGroupId)

                val currentGroup = studyGroupRepository.getStudyGroup(studyGroupId)
                val categories = categoryRepository.getCategories(currentGroup.categories)
                val users = userRepository.getUsers(currentGroup.users)
                val owner = userRepository.getUser(currentGroup.ownerId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentGroup = currentGroup,
                        categories = categories,
                        users = users,
                        owner = owner,
                    )
                }
            } catch (e: Exception) {
                Log.e("DetailStudyViewModel", "deleteStudyGroupMember: ${e.message}", e)
                val messageId = R.string.error_message_delete_study_group_member
                handleError(messageId, e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteStudyGroup() {
        uiState.value.currentGroup?.let { studyGroup ->
            viewModelScope.launch {
                try {
                    _uiState.update { it.copy(isLoading = true) }

                    val categories = categoryRepository.getCategories(studyGroup.categories)
                    val quizzes = quizRepository.getQuizList(categories.flatMap { it.quizzes })

                    questionRepository.deleteQuestions(quizzes.flatMap { it.questions })
                    userOmrRepository.deleteUserOmrs(quizzes.flatMap { it.userOmrs })
                    quizRepository.deleteQuizzes(quizzes.map { it.id })
                    storageRepository.deleteImages(categories.mapNotNull { it.categoryImageUrl })
                    categoryRepository.deleteCategories(categories.map { it.id })
                    notificationRepository.deleteNotification(notificationId = studyGroup.id)
                    studyGroup.studyGroupImageUrl?.let { storageRepository.deleteImage(it) }
                    userRepository.deleteStudyGroupUsers(studyGroup.users, studyGroup.id)
                    studyGroupRepository.deleteStudyGroup(studyGroup.id)

                    _uiState.update { it.copy(isLoading = false, isDeleteStudyGroupSuccess = true) }
                } catch (e: Exception) {
                    Log.e("DetailStudyViewModel", "deleteStudyGroup: ${e.message}", e)
                    val messageId = R.string.error_message_delete_study_group
                    handleError(messageId, e)
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun deleteUserFromStudyGroup() {
        val userId = uiState.value.userId ?: return
        val studyGroupId = uiState.value.currentGroup?.id ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                userRepository.deleteStudyGroupUser(userId, studyGroupId)
                studyGroupRepository.deleteUser(studyGroupId, userId)

                _uiState.update { it.copy(isLoading = false, isLeaveStudyGroupSuccess = true) }
            } catch (e: Exception) {
                Log.e("DetailStudyViewModel", "deleteUserFromStudyGroup: ${e.message}", e)
                val messageId = R.string.error_message_leave_group
                handleError(messageId, e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()) }
    }

    fun resetEmail() {
        _uiState.update { it.copy(email = null, isEmailValid = false) }
    }
}
