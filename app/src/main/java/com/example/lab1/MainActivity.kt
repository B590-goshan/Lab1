package com.example.lab1

import android.os.Bundle
import android.util.Log
import Question
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var answeredQuestions = mutableSetOf<Int>()  // Track answered questions
    private var correctAnswers = 0  // Count correct answers
    private var currentIndex = 0
    private val quizViewModel: QuizViewModel by viewModels()

    // Register for result from CheatActivity
    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    // Define the question bank
    private val questionBank = listOf(
        Question(R.string.question_text, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        // Restore answered questions and cheat status if available
        answeredQuestions = savedInstanceState?.getIntegerArrayList(KEY_ANSWERED_QUESTIONS)?.toMutableSet() ?: mutableSetOf()
        correctAnswers = savedInstanceState?.getInt(KEY_CORRECT_ANSWERS, 0) ?: 0
        quizViewModel.isCheater = savedInstanceState?.getBoolean(KEY_IS_CHEATER, false) ?: false

        // Set up True button listener
        binding.trueButton.setOnClickListener {
            checkAnswer(true)
        }

        // Set up False button listener
        binding.falseButton.setOnClickListener {
            checkAnswer(false)
        }

        // Set up Next button listener
        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        }

        // Set up Cheat button listener
        binding.cheatButton?.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        // Initialize the first question
        updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    // Save state to persist cheat status and answered questions
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList(KEY_ANSWERED_QUESTIONS, ArrayList(answeredQuestions))
        outState.putInt(KEY_CORRECT_ANSWERS, correctAnswers)
        outState.putBoolean(KEY_IS_CHEATER, quizViewModel.isCheater)
    }

    // Update the displayed question and re-enable buttons if necessary
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)

        // Enable or disable buttons based on whether the question has been answered
        val alreadyAnswered = currentIndex in answeredQuestions
        binding.trueButton.isEnabled = !alreadyAnswered
        binding.falseButton.isEnabled = !alreadyAnswered
    }

    // Check user's answer and disable buttons to prevent multiple submissions
    private fun checkAnswer(userAnswer: Boolean) {
        // Prevent re-answering the same question
        if (currentIndex in answeredQuestions) return
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> {
                correctAnswers++
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }

        // Show toast message
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        // Mark question as answered
        answeredQuestions.add(currentIndex)

        // Disable buttons after answering
        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false

        // Check if all questions have been answered
        if (answeredQuestions.size == questionBank.size) {
            showScore()
        }
    }

    // Show final quiz score
    private fun showScore() {
        val scorePercentage = (correctAnswers.toDouble() / questionBank.size) * 100
        Toast.makeText(this, "Quiz Completed! Your score: $scorePercentage%", Toast.LENGTH_LONG).show()
    }

    // Companion object for saving instance state keys
    companion object {
        private const val KEY_ANSWERED_QUESTIONS = "answered_questions"
        private const val KEY_CORRECT_ANSWERS = "correct_answers"
        private const val KEY_IS_CHEATER = "is_cheater"
    }
}