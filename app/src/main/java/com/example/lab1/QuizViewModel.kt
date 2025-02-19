package com.example.lab1

import Question
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"
const val ANSWERED_QUESTIONS_KEY = "ANSWERED_QUESTIONS_KEY"
const val CORRECT_ANSWERS_KEY = "CORRECT_ANSWERS_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    // Track if the user has cheated
    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    // Store the index of the current question
    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    // Store answered questions to prevent re-answering
    private var answeredQuestions: MutableSet<Int>
        get() = savedStateHandle.get<HashSet<Int>>(ANSWERED_QUESTIONS_KEY) ?: hashSetOf()
        set(value) = savedStateHandle.set(ANSWERED_QUESTIONS_KEY, value)

    // Track the number of correct answers
    var correctAnswers: Int
        get() = savedStateHandle.get(CORRECT_ANSWERS_KEY) ?: 0
        set(value) = savedStateHandle.set(CORRECT_ANSWERS_KEY, value)

    // Get the current question's correct answer
    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    // Get the current question's text
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    // Move to the next question while looping through the questionBank
    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    // Check if a question has already been answered
    fun isQuestionAnswered(): Boolean {
        return currentIndex in answeredQuestions
    }

    // Mark question as answered and update correct answers count
    fun submitAnswer(userAnswer: Boolean) {
        if (!isQuestionAnswered()) {
            answeredQuestions.add(currentIndex)
            savedStateHandle.set(ANSWERED_QUESTIONS_KEY, answeredQuestions)
            if (userAnswer == currentQuestionAnswer) {
                correctAnswers++
                savedStateHandle.set(CORRECT_ANSWERS_KEY, correctAnswers)
            }
        }
    }
}