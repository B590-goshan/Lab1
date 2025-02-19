import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.example.lab1.QuizViewModel
import com.example.lab1.CURRENT_INDEX_KEY

class QuizViewModelTest {

    private lateinit var quizViewModel: QuizViewModel

    @Before
    fun setUp() {
        val savedStateHandle = SavedStateHandle(mapOf(CURRENT_INDEX_KEY to 0))
        quizViewModel = QuizViewModel(savedStateHandle)

        quizViewModel.moveToNext()
    }

    @Test
    fun testCurrentQuestionAnswer_IsCorrect() {
        assertTrue("Expected answer to be correct", quizViewModel.currentQuestionAnswer)
    }

    @Test
    fun testCurrentQuestionAnswer_IsIncorrect() {
        quizViewModel.moveToNext()
        println("Current Question Answer: ${quizViewModel.currentQuestionAnswer}")
        assertFalse("Expected answer to be incorrect", quizViewModel.currentQuestionAnswer)
    }

}