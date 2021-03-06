package net.trasim.workoutinwork.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import net.trasim.workoutinwork.R

class DialogActivity2 : AppCompatActivity() {

    private var finish: Button? = null
    private var settings: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog2)

        finish = findViewById(R.id.finishBtn)
        settings = findViewById(R.id.settingsBtn)

        //Exit screen to main activity
        finish!!.setOnClickListener {
            val intent = Intent(this@DialogActivity2, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Go to settings
        settings!!.setOnClickListener {
            val intent = Intent(this@DialogActivity2, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
