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
    private var answeredQuestions = mutableSetOf<Int>()
    private var correctAnswers = 0
    private var currentIndex = 0
    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val cheatedQuestionIndex = result.data?.getIntExtra(CheatActivity.EXTRA_QUESTION_INDEX, -1)
            if (cheatedQuestionIndex != null && cheatedQuestionIndex >= 0) {
                quizViewModel.markCheated()
            }
        }
    }

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

        answeredQuestions = savedInstanceState?.getIntegerArrayList(KEY_ANSWERED_QUESTIONS)?.toMutableSet() ?: mutableSetOf()
        correctAnswers = savedInstanceState?.getInt(KEY_CORRECT_ANSWERS, 0) ?: 0

        binding.trueButton.setOnClickListener {
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener {
            checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        }

        binding.cheatButton?.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue, currentIndex)
            cheatLauncher.launch(intent)
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList(KEY_ANSWERED_QUESTIONS, ArrayList(answeredQuestions))
        outState.putInt(KEY_CORRECT_ANSWERS, correctAnswers)
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)

        val alreadyAnswered = currentIndex in answeredQuestions
        binding.trueButton.isEnabled = !alreadyAnswered
        binding.falseButton.isEnabled = !alreadyAnswered
    }

    private fun checkAnswer(userAnswer: Boolean) {
        if (currentIndex in answeredQuestions) return
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheaterOnCurrentQuestion() -> R.string.judgment_toast
            userAnswer == correctAnswer -> {
                quizViewModel.submitAnswer(userAnswer)
                R.string.correct_toast
            }
            else -> {
                quizViewModel.submitAnswer(userAnswer)
                R.string.incorrect_toast
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        answeredQuestions.add(currentIndex)
        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false

        if (answeredQuestions.size == questionBank.size) {
            showScore()
        }
    }

    private fun showScore() {
        val scorePercentage = (correctAnswers.toDouble() / questionBank.size) * 100
        Toast.makeText(this, "Quiz Completed! Your score: $scorePercentage%", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val KEY_ANSWERED_QUESTIONS = "answered_questions"
        private const val KEY_CORRECT_ANSWERS = "correct_answers"
    }
}