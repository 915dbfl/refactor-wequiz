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
import kr.boostcamp_2024.course.category.navigation.CategoryRoute
import kr.boostcamp_2024.course.designsystem.ui.base.BaseViewModel
import kr.boostcamp_2024.course.domain.model.BaseQuiz
import kr.boostcamp_2024.course.domain.model.Category
import kr.boostcamp_2024.course.domain.repository.CategoryRepository
import kr.boostcamp_2024.course.domain.repository.QuizRepository
import kr.boostcamp_2024.course.domain.repository.StudyGroupRepository
import javax.inject.Inject

data class CategoryUiState(
    val category: Category? = null,
    val quizList: List<BaseQuiz>? = null,
    val isDeleteCategorySuccess: Boolean = false,
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val quizRepository: QuizRepository,
    private val studyGroupRepository: StudyGroupRepository,
) : BaseViewModel() {
    private val studyGroupId: String = savedStateHandle.toRoute<CategoryRoute>().studyGroupId
    private val categoryId: String = savedStateHandle.toRoute<CategoryRoute>().categoryId

    private val _categoryUiState: MutableStateFlow<CategoryUiState> = MutableStateFlow(CategoryUiState())
    val categoryUiState: StateFlow<CategoryUiState> = _categoryUiState.asStateFlow()

    fun initViewmodel() {
        loadCategory(categoryId)
    }

    private fun loadCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                val category = categoryRepository.getCategory(categoryId)
                _categoryUiState.update { it.copy(category = category) }
                loadQuizList(category.quizzes)
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "loadCategory: ${e.message}", e)
                handleError(null, e)
            }
        }
    }

    suspend private fun loadQuizList(quizIdList: List<String>) {
        val quizList = quizRepository.getQuizList(quizIdList)
        _categoryUiState.update { it.copy(quizList = quizList) }
    }

    fun onCategoryDeleteClick() {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(categoryId)
                studyGroupRepository.deleteCategory(studyGroupId, categoryId)
                _categoryUiState.update { it.copy(isDeleteCategorySuccess = true) }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "onCategoryDeleteClick: ${e.message}", e)
                val messageId = R.string.error_delete_category
                handleError(messageId, e)
            }
        }
    }
}
