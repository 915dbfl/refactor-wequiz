package kr.boostcamp_2024.course.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.boostcamp_2024.course.designsystem.ui.base.BaseViewModel
import kr.boostcamp_2024.course.domain.model.Notification
import kr.boostcamp_2024.course.domain.model.NotificationWithGroupInfo
import kr.boostcamp_2024.course.domain.repository.AuthRepository
import kr.boostcamp_2024.course.domain.repository.NotificationRepository
import kr.boostcamp_2024.course.domain.repository.StudyGroupRepository
import kr.boostcamp_2024.course.domain.repository.UserRepository
import kr.boostcamp_2024.course.main.R
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notificationWithGroupInfoList: List<NotificationWithGroupInfo> = emptyList(),
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val studyGroupRepository: StudyGroupRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : BaseViewModel() {
    private val _uiState: MutableStateFlow<NotificationUiState> = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState
        .onStart {
            loadNotifications()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            NotificationUiState(),
        )

    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val userKey = authRepository.getUserKey()
                val notifications = notificationRepository.getNotifications(userKey)
                val notificationWithStudyGroupNameList = notifications.map { notification ->
                    val studyGroup = studyGroupRepository.getStudyGroup(notification.groupId)
                    NotificationWithGroupInfo(
                        notification = notification,
                        studyGroupName = studyGroup.name,
                        studyGroupImgUrl = studyGroup.studyGroupImageUrl,
                    )
                }
                _uiState.update { it.copy(isLoading = false, notificationWithGroupInfoList = notificationWithStudyGroupNameList) }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "loadNotification: ${e.message}", e)
                val messageId = R.string.error_load_notification
                handleError(messageId, e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun acceptInvitation(notification: Notification) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                userRepository.addStudyGroupToUser(notification.userId, notification.groupId)
                deleteInvitation(notification.id, null)
                studyGroupRepository.addUser(notification.groupId, notification.userId)

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "acceptInvitation: ${e.message}", e)
                val messageId = R.string.error_accept_invitation
                handleError(messageId, e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteInvitation(studyId: String, onErrorAction: (suspend () -> Unit)? = this::onRejectInvitationError) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                notificationRepository.deleteNotification(studyId)
                _uiState.update { currentState ->
                    val updatedList = currentState.notificationWithGroupInfoList.filterNot { it.notification.id == studyId }
                    currentState.copy(isLoading = false, notificationWithGroupInfoList = updatedList)
                }
            } catch (e: Exception) {
                onErrorAction?.invoke()
            }
        }
    }

    suspend fun onRejectInvitationError() {
        Log.e("NotificationViewModel", "deleteInvitation")
        val messageId = R.string.error_reject_invitation
        handleError(messageId, null)
        _uiState.update { it.copy(isLoading = false) }
    }
}
