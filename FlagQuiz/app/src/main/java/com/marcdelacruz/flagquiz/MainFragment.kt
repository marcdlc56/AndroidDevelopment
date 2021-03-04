package com.marcdelacruz.flagquiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.IOException
import java.security.SecureRandom

private const val TAG = "FlagQuiz Activity"
private const val FLAGS_IN_QUIZ = 10

class MainFragment : Fragment() {

    private var fileNamelist: MutableList<String>? = null// flag file names
    private var quizCountriesList: MutableList<String>? = null// countries in current quiz
    private var regionsSet: Set<String>? = null// world regions in current quiz
    private var correctAnswer: String?= null// correct country for the current flag
    private var totalGuesses = 0
    private var correctAnswers = 0
    private var guessRows = 0
    private var handler: Handler? = null
    private var random: SecureRandom? = null
    private var shakeAnimation: Animation? = null
    private var quizLinearLayout: LinearLayout? = null// Layout that contains the quiz
    private var questionNumberTextView: TextView? = null //
    private var flagImageView: ImageView? = null// displays a flag
    private var guesslinearlayouts: Array<LinearLayout>? = null
    private var answerTextView: TextView? = null // displays correct answer



    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        fileNamelist = ArrayList()
        quizCountriesList = ArrayList()
        random = SecureRandom()
        handler = Handler()
        shakeAnimation = AnimationUtils.loadAnimation(activity, R.anim.incorrect_shake)
        shakeAnimation?.repeatCount = 3

        quizLinearLayout = view.findViewById(R.id.quizLinearLayout)
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView)
        flagImageView = view.findViewById(R.id.flagImageView)
        guesslinearlayouts = arrayOf(
            view.findViewById<LinearLayout>(R.id.row1LinearLayout),
            view.findViewById<LinearLayout>(R.id.row2LinearLayout),
            view.findViewById<LinearLayout>(R.id.row3LinearLayout),
            view.findViewById<LinearLayout>(R.id.row4LinearLayout)
        )

        answerTextView = view.findViewById(R.id.answerTextView)
        for (row in guesslinearlayouts!!) {
            for (column in 0 until row.childCount) {
                val button = row.getChildAt(column) as Button
                button.setOnClickListener(guessButtonListener)
            }
        }

        updateGuessRows(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        updateRegions(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        resetQuiz()
        questionNumberTextView?.text = getString(R.string.question, 1, FLAGS_IN_QUIZ)

        return view

    }



    private val guessButtonListener = View.OnClickListener { v ->
        val guessButton = v as Button
        val guess = guessButton.text.toString()
        val answer: String? = getCountryName(correctAnswer)
        ++totalGuesses
        if(guess == answer){
            ++correctAnswers
            answerTextView!!.text = "$answer!"
            answerTextView!!.setTextColor(
                resources.getColor(R.color.correct_answer, requireContext().theme))

            disableButtons()

            if(correctAnswers == FLAGS_IN_QUIZ) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.results,totalGuesses,1000/totalGuesses.toDouble()))
                builder.setCancelable(false)
                builder.setPositiveButton("Reset Quiz") { _, _ -> resetQuiz()}
                builder.setNegativeButton("No"){ dialog, _ -> dialog.cancel()}
                val alert = builder.create()
                alert.show()
            } else {
                handler!!.postDelayed(
                    {animate(true)}, 1000
                )
            }
        } else {
            flagImageView!!.startAnimation(shakeAnimation)
            answerTextView!!.setText(R.string.incorrect_answer)
            answerTextView!!.setTextColor(resources.getColor(R.color.incorrect_answer, requireContext().theme))
        }
        guessButton.isEnabled = false
    }

    private fun loadNextFlag() {
        val nextImage: String? = quizCountriesList?.removeAt(0)
        correctAnswer = nextImage
        answerTextView!!.text = ""
        questionNumberTextView!!.text = getString(R.string.question,correctAnswers + 1,FLAGS_IN_QUIZ)
        val region = nextImage?.substring(0, nextImage.indexOf('-'))
        val assets = requireActivity().assets

        try {
            assets.open("$region/$nextImage.png").use {stream ->
                val flag = Drawable.createFromStream(stream, nextImage)
                flagImageView!!.setImageDrawable(flag)
                animate(false)
            }
        } catch (exception: IOException){
            Log.e(TAG, "Error loading $nextImage", exception)

        }
        fileNamelist?.shuffle()

        val correct = fileNamelist!!.indexOf(correctAnswer!!)
        fileNamelist?.add(fileNamelist!!.removeAt(correct))


        for(row in 0 until guessRows) {
            for (column in 0 until guesslinearlayouts!![row].childCount){
                val newGuessButton = guesslinearlayouts!![row].getChildAt(column) as Button
                newGuessButton.isEnabled = true

                val filename = fileNamelist!![row * 2 + column]
                newGuessButton.text = getCountryName(filename)
            }
        }

        val row = random!!.nextInt(guessRows)
        val column = random!!.nextInt(2)
        val randRow = guesslinearlayouts!![row]
        val countryName = getCountryName(correctAnswer!!)
        (randRow.getChildAt(column) as Button).text = countryName

    }

    private fun resetQuiz(){
        val assets = requireActivity().assets
        fileNamelist?.clear()
        try {
            for (region in regionsSet!!){
                val paths = assets.list(region)
                for (path in paths!!){
                    fileNamelist?.add(path.replace(".png", ""))
                }
            }

        } catch (exception: IOException){
            Log.e(TAG, "Error loading image file names", exception)
        }

        correctAnswers = 0
        totalGuesses = 0
        quizCountriesList?.clear()
        var flagCounter = 1
        var numberOfFlags = fileNamelist!!.size

        while(flagCounter <= FLAGS_IN_QUIZ) {
            val randomIndex = random!!.nextInt(numberOfFlags)
            val filename = fileNamelist!![randomIndex]

            if(!quizCountriesList!!.contains(filename)) {
                quizCountriesList?.add(filename)
                ++flagCounter
            }

        }

        loadNextFlag()

    }
    private fun getCountryName(name: String?): String? {
        return name?.substring(name?.indexOf('-')+1)?.replace('_',' ')
    }

    private fun animate(animateOut: Boolean) {
        if (correctAnswers == 0) {
            return
        }
        val centerX = (quizLinearLayout!!.left + quizLinearLayout!!.right) / 2
        val centerY = (quizLinearLayout!!.top + quizLinearLayout!!.bottom) / 2

        val radius = quizLinearLayout!!.width.coerceAtLeast(quizLinearLayout!!.height)
        val animator: Animator

        if (animateOut) {
            animator = ViewAnimationUtils.createCircularReveal(
                quizLinearLayout, centerX, centerY, radius.toFloat(), 0f
            )
            animator.addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        loadNextFlag()
                    }
                }
            )
        } else {
            animator = ViewAnimationUtils.createCircularReveal(
                quizLinearLayout,
                centerX,
                centerX,
                0f,
                radius.toFloat()
            )
        }
        animator.duration = 500
        animator.start()

    }

    private fun disableButtons() {
        for (row in 0 until guessRows) {
            val guessRow = guesslinearlayouts!![row]
            for (i in 0 until guessRow.childCount) {
                guessRow.getChildAt(i).isEnabled = false
            }
        }
    }


    private fun updateGuessRows(sharedPreferences: SharedPreferences){
        val choices = sharedPreferences.getString(CHOICES, null)
        guessRows = choices!!.toInt() /2
        for (layout in guesslinearlayouts!!){
            layout.visibility = View.GONE

        }

        for (row in 0 until guessRows){
            guesslinearlayouts!![row].visibility = View.VISIBLE
        }
    }

    private fun updateRegions(sharedPreferences: SharedPreferences){
        regionsSet = sharedPreferences.getStringSet(REGIONS, null)
    }



        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}