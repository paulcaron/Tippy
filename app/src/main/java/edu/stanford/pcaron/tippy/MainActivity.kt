package edu.stanford.pcaron.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_NUM_PEOPLE = 1

var currency = ""

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        tvNumPeople.text = "$INITIAL_NUM_PEOPLE person"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChangedTip $progress")
                tvTipPercent.text = "$progress%"
                updateTipDescription(progress)
                computeTipAndTotal()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBarNumPeople.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChangedNumPeople $progress")
                val numPersons = progress+1
                if (progress==0){
                    tvNumPeople.text = "$numPersons person"
                }
                else{
                    tvNumPeople.text = "$numPersons persons"
                }
                computeTipAndTotal()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        etBase.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        buttonEuro.setOnClickListener {
            currency = "€"
            buttonEuro.background.setTint(ContextCompat.getColor(this, R.color.colorPrimary))
            buttonDollar.background.setTint(ContextCompat.getColor(this, R.color.colorDefaultButton))
            buttonPound.background.setTint(ContextCompat.getColor(this, R.color.colorDefaultButton))
            computeTipAndTotal()
        }

        buttonDollar.setOnClickListener {
            currency = "$"
            buttonEuro.background.setTint(ContextCompat.getColor(this, R.color.colorDefaultButton))
            buttonDollar.background.setTint(ContextCompat.getColor(this, R.color.colorPrimary))
            buttonPound.background.setTint(ContextCompat.getColor(this, R.color.colorDefaultButton))
            computeTipAndTotal()
        }

        buttonPound.setOnClickListener {
            currency = "£"
            buttonEuro.background.setTint(ContextCompat.getColor(this, R.color.colorDefaultButton))
            buttonDollar.background.setTint(ContextCompat.getColor(this, R.color.colorDefaultButton))
            buttonPound.background.setTint(ContextCompat.getColor(this, R.color.colorPrimary))
            computeTipAndTotal()
        }
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription : String
        when(tipPercent){
            in 0..9 -> tipDescription = "Poor \uD83D\uDE31"
            in 10..14 -> tipDescription = "Acceptable \uD83D\uDE10"
            in 15..19 -> tipDescription = "Good \uD83D\uDE42"
            in 20..24 -> tipDescription = "Great \uD83D\uDE0A"
            else -> tipDescription = "Amazing \uD83E\uDD29"
        }
        tvTipDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal(){
        // Get the value of the base and tip percent
        if (etBase.text.isEmpty()){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvTotalAmountPerPerson.text = ""
        }
        else {
            val baseAmout = etBase.text.toString().toDouble()
            val tipPercent = seekBarTip.progress
            val tipAmount = baseAmout * tipPercent / 100
            val totalAmount = baseAmout + tipAmount
            val totalAmountPerPerson = totalAmount / (seekBarNumPeople.progress+1)
            tvTipAmount.text = "%.2f".format(tipAmount) + " " + currency
            tvTotalAmount.text = "%.2f".format(totalAmount) + " " + currency
            tvTotalAmountPerPerson.text = "%.2f".format(totalAmountPerPerson) + " " + currency
        }
    }
}