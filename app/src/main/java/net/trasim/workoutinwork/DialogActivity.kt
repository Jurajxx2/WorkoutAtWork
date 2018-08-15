package net.trasim.workoutinwork

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import net.trasim.workoutinwork.database.AppDatabase
import net.trasim.workoutinwork.objects.User
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.util.*

class DialogActivity : Activity() {

    private var next: Button? = null
    private var later: Button? = null

    private lateinit var age: Spinner
    private lateinit var gender: Spinner

    private lateinit var weight: EditText
    private lateinit var height: EditText

    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        next = findViewById(R.id.nextBtn)
        later = findViewById(R.id.laterBtn)

        age = findViewById(R.id.selectAge)
        gender = findViewById(R.id.selectGender)

        weight = findViewById(R.id.setWeight)
        height = findViewById(R.id.setHeight)

        next!!.setOnClickListener {
            if (weight.text.isEmpty() || height.text.isEmpty() || age.selectedItemPosition==0 || gender.selectedItemPosition==0){
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

            doAsync {
                val user = User(Integer.valueOf(weight.text.toString()), Integer.valueOf(height.text.toString()), Calendar.YEAR - age.selectedItemPosition, gender.selectedItem.toString(), 3, "en", "", "", "false", 120, 0)
                AppDatabase.getInstance(this@DialogActivity).userModel().insertUser(user)
            }

            val intent = Intent(this@DialogActivity, DialogActivity2::class.java)
            startActivity(intent)
            finish()
        }

        later!!.setOnClickListener {
            val intent = Intent(this@DialogActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
