package kr.boostcamp_2024.course.domain.repository

interface AuthRepository {
    suspend fun storeUserKey(userKey: String)

    suspend fun getUserKey(): String

    suspend fun removeUserKey()
}
