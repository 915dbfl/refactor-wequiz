package kr.boostcamp_2024.course.login.viewmodel

import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.boostcamp_2024.course.designsystem.ui.base.BaseViewModel
import kr.boostcamp_2024.course.domain.repository.AuthRepository
import kr.boostcamp_2024.course.domain.repository.UserRepository
import kr.boostcamp_2024.course.login.R
import kr.boostcamp_2024.course.login.model.UserUiModel
import javax.inject.Inject

data class LoginUiState(
    val isLoginSuccess: Boolean = false,
    val userInfo: UserUiModel? = null,
    val isNewUser: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val _loginUiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState
        .onStart {
            checkExistedUser()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            LoginUiState(),
        )

    fun loginForExperience() {
        viewModelScope.launch {
            authRepository.loginExperience()
            _loginUiState.update { currentState ->
                currentState.copy(isLoginSuccess = true)
            }
        }
    }

    private fun checkExistedUser() {
        viewModelScope.launch {
            try {
                authRepository.getUserKey()
                _loginUiState.update { currentState ->
                    currentState.copy(isLoginSuccess = true)
                }
            } catch (e: Exception) {
                // no-op
            }
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        val messageId = R.string.error_login
        viewModelScope.launch {
            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            checkUser(googleIdTokenCredential)
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.e("LoginScreen", "handleSignIn: ${e.message}", e)
                            handleError(messageId, e)
                        }
                    } else {
                        Log.e("LoginScreen", "Unexpected type of credential: ${credential.type}")
                        handleError(messageId, null)
                    }
                }

                else -> {
                    Log.e("LoginScreen", "credential is not CustomCredential: $credential")
                    handleError(messageId, null)
                }
            }
        }
    }

    private fun checkUser(googleIdTokenCredential: GoogleIdTokenCredential) {
        viewModelScope.launch {
            try {
                userRepository.findUserByEmail(googleIdTokenCredential.id)
                // 이미 회원가입된 유저
                authRepository.login(googleIdTokenCredential.idToken)
                _loginUiState.update { currentState ->
                    currentState.copy(isLoginSuccess = true)
                }
            } catch (e: Exception) {
                // 회원 가입 필요
                _loginUiState.update { currentState ->
                    currentState.copy(
                        userInfo = UserUiModel(
                            id = googleIdTokenCredential.idToken,
                            email = googleIdTokenCredential.id,
                            name = googleIdTokenCredential.familyName + googleIdTokenCredential.givenName,
                            profileImageUrl = googleIdTokenCredential.profilePictureUri.toString(),
                        ),
                        isNewUser = true,
                    )
                }
            }
        }
    }

    fun resetUserState() {
        _loginUiState.update { currentState ->
            currentState.copy(userInfo = null, isNewUser = false)
        }
    }
}
