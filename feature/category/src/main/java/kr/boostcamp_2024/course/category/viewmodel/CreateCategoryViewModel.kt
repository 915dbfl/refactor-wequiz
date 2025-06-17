package kr.boostcamp_2024.course.category.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.boostcamp_2024.course.category.R
import kr.boostcamp_2024.course.category.navigation.CreateCategoryRoute
import kr.boostcamp_2024.course.designsystem.ui.base.BaseViewModel
import kr.boostcamp_2024.course.domain.repository.CategoryRepository
import kr.boostcamp_2024.course.domain.repository.StorageRepository
import kr.boostcamp_2024.course.domain.repository.StudyGroupRepository
import javax.inject.Inject

data class CreateCategoryUiState(
    val isLoading: Boolean = false,
    val categoryName: String = "",
    val categoryDescription: String = "",
    val creationSuccess: Boolean = false,
    val currentImage: ByteArray? = null,
    val defaultImageUri: String? = null,
) {
    val isCategoryCreationValid: Boolean = categoryName.length in 1..20 && categoryDescription.length in 0..100 && isLoading.not()
}

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val studyGroupRepository: StudyGroupRepository,
    private val storageRepository: StorageRepository,
) : BaseViewModel() {
    private val studyGroupId = savedStateHandle.toRoute<CreateCategoryRoute>().studyGroupId
    val categoryId = savedStateHandle.toRoute<CreateCategoryRoute>().categoryId

    private val _createCategoryUiState: MutableStateFlow<CreateCategoryUiState> = MutableStateFlow(CreateCategoryUiState())
    val createCategoryUiState: StateFlow<CreateCategoryUiState> = _createCategoryUiState.asStateFlow()

    fun uploadCategory() {
        setLoading(true)
        var messageId: Int? = null
        viewModelScope.launch {
            try {
                when (categoryId) {
                    null -> {
                        messageId = R.string.error_create_category
                        createCategory()
                    }

                    else -> {
                        messageId = R.string.error_update_category
                        updateCategory(categoryId)
                    }
                }
            } catch (e: Exception) {
                Log.e("CreateCategoryViewModel", "uploadCategory: ${e.message}", e)
                handleError(messageId, e)
                setLoading(false)
            }
        }
    }

    fun fetchCategoryInfo() {
        viewModelScope.launch {
            try {
                categoryId?.let {
                    setLoading(true)
                    val category = categoryRepository.getCategory(categoryId)
                    _createCategoryUiState.update {
                        it.copy(
                            isLoading = false,
                            categoryName = category.name,
                            categoryDescription = category.description ?: "",
                            defaultImageUri = category.categoryImageUrl,
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("CreateCategoryViewModel", "fetchCategoryInfo: ${e.message}", e)
                handleError(null, e)
                setLoading(false)
            }
        }
    }

    private suspend fun createCategory() {
        val imageUrl = _createCategoryUiState.value.currentImage?.let { image ->
            storageRepository.uploadImage(image)
        }

        val categoryId = categoryRepository.createCategory(
            _createCategoryUiState.value.categoryName,
            _createCategoryUiState.value.categoryDescription.takeIf { it.isNotBlank() },
            imageUrl,
        )
        saveCategoryToStudyGroup(categoryId)
    }

    private suspend fun updateCategory(categoryId: String) {
        val imageUrl = _createCategoryUiState.value.currentImage?.let { image ->
            storageRepository.uploadImage(image)
        } ?: createCategoryUiState.value.defaultImageUri

        categoryRepository.updateCategory(
            categoryId,
            _createCategoryUiState.value.categoryName,
            _createCategoryUiState.value.categoryDescription.takeIf { it.isNotBlank() },
            imageUrl,
        )
        saveCategoryToStudyGroup(categoryId)
    }

    private suspend fun saveCategoryToStudyGroup(categoryId: String) {
        studyGroupId?.let {
            studyGroupRepository.addCategoryToStudyGroup(studyGroupId, categoryId)
        }

        _createCategoryUiState.update { currentState ->
            currentState.copy(
                isLoading = false,
                creationSuccess = true,
            )
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _createCategoryUiState.update { currentState ->
            currentState.copy(isLoading = isLoading)
        }
    }

    fun onNameChanged(name: String) {
        _createCategoryUiState.update { currentState ->
            currentState.copy(categoryName = name)
        }
    }

    fun onDescriptionChanged(description: String) {
        _createCategoryUiState.update { currentState ->
            currentState.copy(categoryDescription = description)
        }
    }

    fun onImageByteArrayChanged(imageByteArray: ByteArray) {
        _createCategoryUiState.update { it.copy(currentImage = imageByteArray) }
    }
}
