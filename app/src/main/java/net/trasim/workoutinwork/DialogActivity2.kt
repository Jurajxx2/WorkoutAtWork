package net.trasim.workoutinwork

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import net.trasim.workoutinwork.Database.AppDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class DialogActivity2 : AppCompatActivity() {

    private var finish: Button? = null
    private var settings: Button? = null

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog2)

        doAsync {
            user = AppDatabase.getInstance(this@DialogActivity2).userModel().getUserByID(1)

            uiThread {
                toast(user!!.height.toString() + " test")
            }
        }


        finish = findViewById(R.id.finishBtn)
        settings = findViewById(R.id.settingsBtn)

        finish!!.setOnClickListener {
            finish()
        }

        settings!!.setOnClickListener {
            val intent = Intent(this@DialogActivity2, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
