package com.example.lab1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1.databinding.ActivityCheatBinding
import android.content.*
import android.app.Activity

class CheatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheatBinding

    private var answerIsTrue = false
    private var questionIndex = 0
    private var isAnswerShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        questionIndex = intent.getIntExtra(EXTRA_QUESTION_INDEX, 0)
        isAnswerShown = savedInstanceState?.getBoolean(KEY_ANSWER_SHOWN, false) ?: false

        if (isAnswerShown) {
            showAnswer()
            setAnswerShownResult(questionIndex)
        }

        binding.showAnswerButton.setOnClickListener {
            showAnswer()
            setAnswerShownResult(questionIndex)
        }
    }

    private fun showAnswer() {
        val answerText = if (answerIsTrue) R.string.true_button else R.string.false_button
        binding.answerTextView.setText(answerText)
        isAnswerShown = true
    }

    private fun setAnswerShownResult(questionIndex: Int) {
        val data = Intent().apply {
            putExtra(EXTRA_QUESTION_INDEX, questionIndex)
        }
        setResult(Activity.RESULT_OK, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ANSWER_SHOWN, isAnswerShown)
    }

    companion object {
        private const val EXTRA_ANSWER_IS_TRUE = "com.example.lab1.answer_is_true"
        const val EXTRA_QUESTION_INDEX = "com.example.lab1.question_index"
        private const val KEY_ANSWER_SHOWN = "KEY_ANSWER_SHOWN"

        fun newIntent(packageContext: Context, answerIsTrue: Boolean, questionIndex: Int): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
                putExtra(EXTRA_QUESTION_INDEX, questionIndex)
            }
        }
    }
}