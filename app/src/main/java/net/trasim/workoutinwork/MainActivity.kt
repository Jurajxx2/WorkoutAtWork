package net.trasim.workoutinwork

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.CalendarDay
import net.trasim.workoutinwork.database.AppDatabase
import net.trasim.workoutinwork.database.DatabaseInit
import net.trasim.workoutinwork.objects.Workday
import org.jetbrains.anko.*
import java.util.*
import android.content.SharedPreferences
import android.os.Handler
import android.support.v7.preference.PreferenceManager
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import net.trasim.workoutinwork.database.Data4to5
import net.trasim.workoutinwork.objects.Tip

class MainActivity : AppCompatActivity() {

    //Layouts
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var enableReminder: Button
    private lateinit var startWorkout: Button
    private lateinit var setInterval: Button
    private lateinit var reminderInterval: TextView
    private lateinit var nextAlarm: TextView
    private lateinit var hintHeading: TextView
    private lateinit var hintText: TextView

    //Objects
    private lateinit var workdays: List<Workday>
    private lateinit var tips: List<Tip>
    private var lastWorkday = Workday()

    private lateinit var database: AppDatabase

    private lateinit var sharedPref: SharedPreferences

    //Basic data
    private var weight: Float = 0.toFloat()
    private var height: Float = 0.toFloat()
    private var isOK: Boolean = false
    private var workoutReminderInterval: Long = 0
    private var workoutNextReminder: Long = 0
    private var reminder: Boolean = false
    private var databaseInit: Boolean = false

    private var language: String = ""

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getInstance(this@MainActivity)

        //Preferences init
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        weight = sharedPref.getString("weight", "0").toFloat()
        height = sharedPref.getString("height", "0").toFloat()
        isOK = sharedPref.getBoolean("isOK", false)
        workoutReminderInterval = sharedPref.getString("reminder_interval", "7200000").toLong()
        workoutNextReminder = sharedPref.getLong("next_interval", 0)
        reminder = sharedPref.getBoolean("workout_reminder", false)
        databaseInit = sharedPref.getBoolean("database", false)

        val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(this.applicationContext, 0, intent, 0)

        //Views init
        enableReminder = findViewById(R.id.enableReminder)
        startWorkout = findViewById(R.id.startWorkout)
        setInterval = findViewById(R.id.setInterval)
        reminderInterval = findViewById(R.id.reminderInterval)
        nextAlarm = findViewById(R.id.nextAlarm)
        hintHeading = findViewById(R.id.hintHeading)
        hintText = findViewById(R.id.hintText)

        //Get language
        language = Locale.getDefault().isO3Language


        //If initial settings were saved
        if(isOK){

            //Get last workday - I need its date if it is today or not
            //Get tips and update UI
            doAsync {
                workdays = database.workdayModel().allWorkdays
                tips = database.tipModel().allTips

                if (tips.isEmpty()){
                    Data4to5(this@MainActivity)
                }

                uiThread {
                    val randomNr = (0 until tips.size).random()

                    lastWorkday = workdays[workdays.size - 1]

                    if (language == "SK") {
                        hintHeading.text = tips[randomNr].headingSK
                        hintText.text = tips[randomNr].textSK

                    } else {
                        hintHeading.text = tips[randomNr].headingEN
                        hintText.text = tips[randomNr].textEN
                    }

                    if (hintText.text.length > 120) {
                        hintText.movementMethod = ScrollingMovementMethod.getInstance()
                    }
                }
            }

            //If reminder is true, update UI with information
            if (reminder){
                setInterval.isEnabled = true
                skipWorkout.isEnabled = true

                val myCalender = Calendar.getInstance()
                myCalender.timeInMillis = workoutNextReminder + workoutReminderInterval
                val hour = myCalender.get(Calendar.HOUR_OF_DAY)
                val minute = myCalender.get(Calendar.MINUTE)

                //If last reminder was before actual time
                if (myCalender.timeInMillis<System.currentTimeMillis()){
                    reminder = false
                    saveSharedPref()
                } else {

                    enableReminder.text = getString(R.string.disable_workout_reminder)

                    myCalender.set(Calendar.HOUR_OF_DAY, hour)
                    myCalender.set(Calendar.MINUTE, minute)

                    val time = workoutReminderInterval / 1000 / 60

                    if (time % 60 > 9) {
                        val intervalText = "0" + (time / 60).toString() + " : " + (time % 60).toString()
                        reminderInterval.text = intervalText
                    } else {
                        val intervalText = "0" + (time / 60).toString() + " : " + "0" + (time % 60).toString()
                        reminderInterval.text = intervalText
                    }

                    var nextAlarmTime = if (myCalender.get(Calendar.HOUR_OF_DAY) < 10) {
                        "0" + myCalender.get(Calendar.HOUR_OF_DAY).toString() + " : "
                    } else {
                        myCalender.get(Calendar.HOUR_OF_DAY).toString() + " : "
                    }

                    nextAlarmTime += if (myCalender.get(Calendar.MINUTE) < 10) {
                        "0" + myCalender.get(Calendar.MINUTE).toString()
                    } else {
                        myCalender.get(Calendar.MINUTE).toString()
                    }

                    nextAlarm.text = nextAlarmTime
                }
            }

        } else {
            //Initialise database - need data about exercises and tips + initialise default values for settings
            PreferenceManager.setDefaultValues(this, R.xml.pref_general, false)
            if (!databaseInit) {
                doAsync { DatabaseInit(this@MainActivity) }
                databaseInit = true
                saveSharedPref()
            }
            //Launch Intro activity
            val intent2 = Intent(this@MainActivity, DialogActivity::class.java)
            startActivity(intent2)
            finish()
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //Menu buttons
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            mDrawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.home_btn -> {
                    val menuIntent = Intent(this, MainActivity::class.java)
                    startActivity(menuIntent)
                    finish()
                }
                R.id.workouts_history_btn -> {
                    val menuIntent = Intent(this, WorkoutHistoryActivity::class.java)
                    startActivity(menuIntent)
                    finish()
                }
                R.id.settings_btn -> {
                    val menuIntent = Intent(this, SettingsActivity::class.java)
                    startActivity(menuIntent)
                    finish()
                }
                R.id.workout_list_btn -> {
                    val menuIntent = Intent(this, WorkoutListActivity::class.java)
                    startActivity(menuIntent)
                    finish()
                }
                R.id.info -> {
                    val menuIntent = Intent(this, InfoActivity::class.java)
                    startActivity(menuIntent)
                    finish()
                }
            }

            true
        }

        //Enable reminder button - onclick updates times of next reminder and interval
        enableReminder.setOnClickListener {
            if (reminder) {
                enableReminder.text = getString(R.string.enable_workout_reminder)
                reminder = false
                alarmMgr.cancel(alarmIntent)
                reminderInterval.text = "_ _ : _ _"
                nextAlarm.text = "_ _ : _ _"

            } else {
                val myCalender = Calendar.getInstance()
                val hour = myCalender.get(Calendar.HOUR_OF_DAY)
                val minute = myCalender.get(Calendar.MINUTE)
                enableReminder.text = getString(R.string.disable_workout_reminder)
                reminder = true
                workoutNextReminder = System.currentTimeMillis()

                val time = (workoutReminderInterval/1000/60).toInt()

                myCalender.set(Calendar.HOUR_OF_DAY, hour + time/60)
                myCalender.set(Calendar.MINUTE, minute + time%60)

                if (time % 60 > 9) {
                    val intervalText = "0" + (time / 60).toString() + " : " + (time % 60).toString()
                    reminderInterval.text = intervalText
                } else {
                    val intervalText = "0" + (time / 60).toString() + " : " + "0" + (time % 60).toString()
                    reminderInterval.text = intervalText
                }

                var nextAlarmTime = if (myCalender.get(Calendar.HOUR_OF_DAY)<10){
                    "0" + myCalender.get(Calendar.HOUR_OF_DAY).toString() + " : "
                } else {
                    myCalender.get(Calendar.HOUR_OF_DAY).toString() + " : "
                }

                nextAlarmTime += if (myCalender.get(Calendar.MINUTE)<10){
                    "0" + myCalender.get(Calendar.MINUTE).toString()
                } else {
                    myCalender.get(Calendar.MINUTE).toString()
                }

                nextAlarm.text = nextAlarmTime

                alarmMgr.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + workoutReminderInterval,
                        workoutReminderInterval,
                        alarmIntent
                )
            }
            //Save changed data
            saveSharedPref()
        }

        //Start workout button - launch workout activity, check if it should be added to last workday
        startWorkout.setOnClickListener {
            val c = Calendar.getInstance()
            c.timeInMillis = lastWorkday.date!!.toLong()
            CalendarDay.from(c)
            if (CalendarDay.from(c) == CalendarDay.today()) {
                startWorkout()
            } else {
                createWorkday()
            }
        }

        //Set interval for workout reminder
        setInterval.setOnClickListener {
            if(!reminder){
                toast("Enable reminder to set interval")
                return@setOnClickListener
            }
            val myCalender = Calendar.getInstance()
            val hour = myCalender.get(Calendar.HOUR_OF_DAY)
            val minute = myCalender.get(Calendar.MINUTE)

            val myTimeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
                if (view.isShown) {
                    //if (hourOfDay > 4 || (hourOfDay == 0 && minuteOfDay < 15)) {
                    //    toast("This interval is not allowed. Please select less than 4 hours and more than 14 minutes for the interval.")
                    //} else {
                        myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay + hour)
                        myCalender.set(Calendar.MINUTE, minuteOfDay + minute)

                        workoutReminderInterval = (hourOfDay*3600 + minuteOfDay*60).toLong()*1000
                        val time = (workoutReminderInterval/1000/60).toInt()

                        alarmMgr.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis() + workoutReminderInterval,
                                workoutReminderInterval,
                                alarmIntent
                        )

                        //alarmMgr.setAlarmClock(AlarmManager.AlarmClockInfo(60000, PendingIntent.getActivity(this, 123, Intent(this, Exercise::class.java), 0)), alarmIntent)

                        //Update settings here
                        saveSharedPref()

                        if (time % 60 > 9) {
                            val intervalText = "0" + (time / 60).toString() + " : " + (time % 60).toString()
                            reminderInterval.text = intervalText
                        } else {
                            val intervalText = "0" + (time / 60).toString() + " : " + "0" + (time % 60).toString()
                            reminderInterval.text = intervalText
                        }

                        var nextAlarmTime = if (myCalender.get(Calendar.HOUR_OF_DAY)<10){
                            "0" + myCalender.get(Calendar.HOUR_OF_DAY).toString() + " : "
                        } else {
                            myCalender.get(Calendar.HOUR_OF_DAY).toString() + " : "
                        }

                        nextAlarmTime += if (myCalender.get(Calendar.MINUTE)<10){
                            "0" + myCalender.get(Calendar.MINUTE).toString()
                        } else {
                            myCalender.get(Calendar.MINUTE).toString()
                        }

                        nextAlarm.text = nextAlarmTime
                    //}
                }
            }
            val timePickerDialog = TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, 2, 15, true)
            timePickerDialog.setTitle("Choose interval:")
            timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }

        //Skip workout button - adds one reminder interval to reminder interval
        skipWorkout.setOnClickListener {
            if (reminder) {
                alarmMgr.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        workoutNextReminder + 2 * workoutReminderInterval,
                        workoutReminderInterval,
                        alarmIntent
                )
                workoutNextReminder += workoutReminderInterval

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = workoutNextReminder + workoutReminderInterval
                }

                var nextAlarmTime = if (calendar.get(Calendar.HOUR_OF_DAY)<10){
                    "0" + calendar.get(Calendar.HOUR_OF_DAY).toString() + " : "
                } else {
                    calendar.get(Calendar.HOUR_OF_DAY).toString() + " : "
                }

                nextAlarmTime += if (calendar.get(Calendar.MINUTE)<10){
                    "0" + calendar.get(Calendar.MINUTE).toString()
                } else {
                    calendar.get(Calendar.MINUTE).toString()
                }

                nextAlarm.text = nextAlarmTime
                saveSharedPref()
            } else {
                toast("Enable reminder to skip interval")
            }
        }
    }

    //Save shared pref
    private fun saveSharedPref(){
        with(sharedPref.edit()){
            putBoolean("workout_reminder", reminder)
            putBoolean("database", databaseInit)
            putString("reminder_interval", workoutReminderInterval.toString())
            putLong("next_interval", workoutNextReminder)
            apply()
        }
    }

    //If today was not created any workday, it will create new workday
    private fun createWorkday() {
        val today = Calendar.getInstance().timeInMillis
        lastWorkday = Workday(today.toString(), System.currentTimeMillis(), System.currentTimeMillis(), 0)

        doAsync {
            database.workdayModel().insertWorkday(lastWorkday)
        }
        startWorkout()
    }

    //Start workout function that will add actual time to workday end and update database and launch workout
    private fun startWorkout() {
        lastWorkday.end = System.currentTimeMillis()
        doAsync {
            database.workdayModel().updateWorkday(lastWorkday)
        }
        val intent = Intent(this, WorkoutActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Double backpressed safety function - on backpressed toast will be displayed and after next backpressed will finish
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}
