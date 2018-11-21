package net.trasim.workoutinwork.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import net.trasim.workoutinwork.R
import org.jetbrains.anko.toast

class DialogActivity : Activity() {

    private var next: Button? = null
    private var later: Button? = null

    private lateinit var age: Spinner
    private lateinit var gender: Spinner

    private lateinit var weight: EditText
    private lateinit var height: EditText

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        next = findViewById(R.id.nextBtn)
        later = findViewById(R.id.laterBtn)

        age = findViewById(R.id.selectAge)
        gender = findViewById(R.id.selectGender)

        weight = findViewById(R.id.setWeight)
        height = findViewById(R.id.setHeight)

        //Button to next page
        next!!.setOnClickListener {
            if (weight.text.isEmpty() || height.text.isEmpty() || age.selectedItemPosition==0 || gender.selectedItemPosition==-1){
                toast("Please, fill all fields")
                return@setOnClickListener
            }
            if (Integer.valueOf(weight.text.toString())<10){
                toast("Please, enter your weight")
                return@setOnClickListener
            }

            if (Integer.valueOf(height.text.toString())<10){
                toast("Please, enter your weight")
                return@setOnClickListener
            }

            //Update shared preferences
            with(sharedPref.edit()){
                putBoolean("isOK", true)
                putString("gender", gender.selectedItem.toString())
                putString("weight", weight.text.toString())
                putString("height", height.text.toString())
                putString("age", age.selectedItem.toString())
                putBoolean("reminder", false)
                putString("reminder_interval", "7200000")
                apply()
            }

            //Start intent
            val intent = Intent(this@DialogActivity, DialogActivity2::class.java)
            startActivity(intent)
            finish()
        }

        //Finish button
        later!!.setOnClickListener {
            finish()
        }
    }
}
