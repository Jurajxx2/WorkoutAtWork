package net.trasim.workoutinwork

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import net.trasim.workoutinwork.Database.AppDatabase
import net.trasim.workoutinwork.Database.DatabaseInit
import net.trasim.workoutinwork.R.id.startDay
import org.jetbrains.anko.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var startDay : Button
    private lateinit var startWorkout : Button

    private var lastWorkday = Workday()

    private var user = User()
    private lateinit var database : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startDay = findViewById(R.id.startDay)
        startWorkout = findViewById(R.id.startWorkout)

        doAsync{
            val database = AppDatabase.getInstance(this@MainActivity)
            user = database.userModel().getUserByID(1)
            lastWorkday = database.workdayModel().getLastWorkday()

            uiThread {
                if (user?.id != 1){
                    doAsync { DatabaseInit(this@MainActivity) }
                    var intent = Intent(this@MainActivity, DialogActivity::class.java)
                    startActivity(intent)
                }

                if (lastWorkday != null) {
                    if (lastWorkday.end > System.currentTimeMillis()) {
                        startDay.text = "FINISH workday session"
                    }
                }
            }
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            //menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            when (menuItem.itemId){
                R.id.home_btn -> {
                    //Going to same screen
                }
                R.id.workouts_history_btn -> {
                    var intent = Intent(this, WorkoutHistoryActivity::class.java)
                    startActivity(intent)
                }
                R.id.settings_btn -> {
                    var intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.workout_list_btn -> {
                    var intent = Intent(this, WorkoutListActivity::class.java)
                    startActivity(intent)
                }
                R.id.info -> {
                    var intent = Intent(this, InfoActivity::class.java)
                    startActivity(intent)
                }
            }

            true
        }


        startDay.setOnClickListener{
            //doAsync { AppDatabase.getInstance(this@MainActivity).userModel().deleteAllUsers() }
            if (lastWorkday != null) {
                if (lastWorkday.end > System.currentTimeMillis()) {
                    lastWorkday.end = System.currentTimeMillis()
                    doAsync {
                        AppDatabase.getInstance(this@MainActivity).workdayModel().updateWorkday(lastWorkday)
                    }
                    startDay.text = "Start workday session"
                }
            } else {
                createWorkday(false)
            }

        }

        startWorkout.setOnClickListener{
            if (lastWorkday != null) {
                if (!lastWorkday.finished && lastWorkday.end > System.currentTimeMillis()) {
                    startWorkout()
                }
            } else {
                alert {
                    title = "Start workday session"
                    message = "To do a workout, you have to start a workday session. Do you want to start it now?"
                    positiveButton(android.R.string.yes, onClicked = {
                        createWorkday(true)
                    })
                }
            }
        }
    }


    private fun createWorkday(startWorkout: Boolean){
        val today = Calendar.getInstance().timeInMillis
        lastWorkday = Workday(today.toString(), System.currentTimeMillis(), System.currentTimeMillis() + 28800000, 28800000)

        doAsync {
            AppDatabase.getInstance(this@MainActivity).workdayModel().insertWorkday(lastWorkday)
        }

        if (startWorkout) {
            startWorkout()
        }

        startDay.text = "FINISH workday session"
    }

    private fun startWorkout(){
        val intent = Intent(this, WorkoutActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
