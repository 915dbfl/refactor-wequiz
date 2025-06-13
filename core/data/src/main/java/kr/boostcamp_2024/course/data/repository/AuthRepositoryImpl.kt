package kr.boostcamp_2024.course.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kr.boostcamp_2024.course.domain.WeQuizException
import kr.boostcamp_2024.course.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override suspend fun login(idToken: String): Unit = runCatchingWeQuiz {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }

    override suspend fun loginExperience(): Unit = runCatchingWeQuiz {
        firebaseAuth.signInWithEmailAndPassword("wequiz1206@gmail.com", "wequiz1206@gmail.com").await()
    }

    override fun getUserKey() = firebaseAuth.uid
        ?: throw WeQuizException.UnknownException(null, Exception("User key not found"))

    override fun logout() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            throw WeQuizException.UnknownException(null, e)
        }
    }
}
