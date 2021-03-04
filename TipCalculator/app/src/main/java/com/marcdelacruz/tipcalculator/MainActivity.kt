package com.marcdelacruz.tipcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import java.lang.Double.parseDouble
import java.lang.NumberFormatException
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance()
    private val percentFormat: NumberFormat = NumberFormat.getPercentInstance()

    private var billAmount: Double = 0.0
    private var percent: Double = 0.15

    private var amountTextView: TextView? = null
    private var percentTextView: TextView? = null
    private var tipTextView: TextView? = null
    private var totalTextView: TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        amountTextView = findViewById<TextView>(R.id.amountTextView)
        percentTextView = findViewById<TextView>(R.id.percentTextView)
        tipTextView = findViewById<TextView>(R.id.tipTextView)
        totalTextView = findViewById<TextView>(R.id.totalTextView)

        tipTextView?.text = currencyFormat.format(0)
        totalTextView?.text = currencyFormat.format(0)


        val amountEditText: EditText = findViewById<EditText>(R.id.amountEditText)
        amountEditText.addTextChangedListener(amountEditTextWatcher)

        val percentSeekBar: SeekBar = findViewById<SeekBar>(R.id.percentSeekBar)
        percentSeekBar.setOnSeekBarChangeListener(seekBarListener)


    }
    private val amountEditTextWatcher : TextWatcher = object:TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            try {
                billAmount = parseDouble(p0.toString()) / 100.0
                amountTextView?.text = currencyFormat.format(billAmount)
            } catch (e:NumberFormatException){
                amountTextView?.text = ""
                billAmount = 0.0
            }

            calculate()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    private val seekBarListener:SeekBar.OnSeekBarChangeListener = object:SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
           percent = p1 / 100.0
           calculate()
        }
        override fun onStartTrackingTouch(p0: SeekBar?) {
        }
        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    }


    private fun calculate(){
        percentTextView?.text = percentFormat.format(percent)
        val tip: Double = billAmount * percent

        val total: Double = billAmount + tip
        tipTextView?.text = currencyFormat.format(tip)
        totalTextView?.text = currencyFormat.format(total)
    }

}