package net.trasim.workoutinwork

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import com.prolificinteractive.materialcalendarview.CalendarDay
import net.trasim.workoutinwork.database.AppDatabase
import net.trasim.workoutinwork.database.DatabaseInit
import net.trasim.workoutinwork.objects.User
import net.trasim.workoutinwork.objects.Workday
import org.jetbrains.anko.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var startDay: Button
    private lateinit var startWorkout: Button
    private lateinit var setInterval: Button
    private lateinit var reminderInterval: TextView
    private lateinit var nextAlarm: TextView

    private lateinit var workdays: List<Workday>
    private var lastWorkday = Workday()

    private lateinit var users: List<User>
    private var user = User()
    private lateinit var database: AppDatabase

    //private var alarmMgr: AlarmManager? = null
    //private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(this.applicationContext, 0, intent, 0)

        startDay = findViewById(R.id.startDay)
        startWorkout = findViewById(R.id.startWorkout)
        setInterval = findViewById(R.id.setInterval)
        reminderInterval = findViewById(R.id.reminderInterval)
        nextAlarm = findViewById(R.id.nextAlarm)

        doAsync {
            database = AppDatabase.getInstance(this@MainActivity)
            users = database.userModel().allUsers
            workdays = database.workdayModel().allWorkdays

            uiThread {
                if (users.isEmpty()) {
                    doAsync { DatabaseInit(this@MainActivity) }
                    val intent = Intent(this@MainActivity, DialogActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    user = users[0]

                    if (user.reminder=="true"){
                        val myCalender = Calendar.getInstance()
                        myCalender.timeInMillis = user.nextReminder + user.remindInterval*60*1000
                        val hour = myCalender.get(Calendar.HOUR_OF_DAY)
                        val minute = myCalender.get(Calendar.MINUTE)
                        startDay.text = "Disable reminder"
                        user.reminder = "true"

                        myCalender.set(Calendar.HOUR_OF_DAY, hour)
                        myCalender.set(Calendar.MINUTE, minute)

                        if (user.remindInterval % 60 > 10) {
                            val intervalText = "0" + (user.remindInterval / 60).toString() + " : " + (user.remindInterval % 60).toString()
                            reminderInterval.text = intervalText
                        } else {
                            val intervalText = "0" + (user.remindInterval / 60).toString() + " : " + "0" + (user.remindInterval % 60).toString()
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

                    }
                }

                if (!workdays.isEmpty()) {
                    lastWorkday = workdays[workdays.size - 1]
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
            when (menuItem.itemId) {
                R.id.home_btn -> {
                    //Going to same screen
                }
                R.id.workouts_history_btn -> {
                    val intent = Intent(this, WorkoutHistoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.settings_btn -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.workout_list_btn -> {
                    val intent = Intent(this, WorkoutListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.info -> {
                    val intent = Intent(this, InfoActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            true
        }

        startDay.setOnClickListener {
            if (user.reminder!!.toBoolean()) {
                startDay.text = "Enable reminder"
                user.reminder = "false"
                alarmMgr?.cancel(alarmIntent)
                reminderInterval.text = "_ _ : _ _"
                nextAlarm.text = "_ _ : _ _"

            } else {
                val myCalender = Calendar.getInstance()
                val hour = myCalender.get(Calendar.HOUR_OF_DAY)
                val minute = myCalender.get(Calendar.MINUTE)
                startDay.text = "Disable reminder"
                user.reminder = "true"
                user.nextReminder = System.currentTimeMillis()

                myCalender.set(Calendar.HOUR_OF_DAY, hour + user.remindInterval/60)
                myCalender.set(Calendar.MINUTE, minute + user.remindInterval%60)

                if (user.remindInterval % 60 > 10) {
                    val intervalText = "0" + (user.remindInterval / 60).toString() + " : " + (user.remindInterval % 60).toString()
                    reminderInterval.text = intervalText
                } else {
                    val intervalText = "0" + (user.remindInterval / 60).toString() + " : " + "0" + (user.remindInterval % 60).toString()
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

//                alarmMgr?.setRepeating(
//                        AlarmManager.RTC_WAKEUP,
//                        calendar.timeInMillis,
//                        1000 * 60,
//                        alarmIntent
//                )

                alarmMgr.set(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (1000 * 60 * user.remindInterval),
                        alarmIntent
                )

            }
            doAsync {
                AppDatabase.getInstance(this@MainActivity).userModel().updateUser(user)
            }


        }

        startWorkout.setOnClickListener {
            if (!workdays.isEmpty()) {
                val c = Calendar.getInstance()
                c.timeInMillis = lastWorkday.date!!.toLong()
                CalendarDay.from(c)
                if (CalendarDay.from(c) == CalendarDay.today()) {
                    startWorkout()
                } else {
                    createWorkday()
                }
            } else {
                createWorkday()
            }
        }

        setInterval.setOnClickListener {
            val myCalender = Calendar.getInstance()
            val hour = myCalender.get(Calendar.HOUR_OF_DAY)
            val minute = myCalender.get(Calendar.MINUTE)


            val myTimeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minuteOfDay ->
                if (view.isShown) {
                    if (hourOfDay > 4 || (hourOfDay == 0 && minute < 15)) {
                        toast("This interval is not allowed. Please select less than 4 hours and more than 14 minutes for the interval.")
                    } else {
                        myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay + hour)
                        myCalender.set(Calendar.MINUTE, minuteOfDay + minute)

                        user.remindInterval = hourOfDay*60 + minuteOfDay

                        alarmMgr.set(
                                AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis() + (1000 * 60 * user.remindInterval),
                                alarmIntent
                        )

                        doAsync {
                            AppDatabase.getInstance(this@MainActivity).userModel().updateUser(user)
                            uiThread {
                                toast("Reminder interval was successfully updated")
                            }
                        }

                        if (user.remindInterval % 60 > 10) {
                            val intervalText = "0" + (user.remindInterval / 60).toString() + " : " + (user.remindInterval % 60).toString()
                            reminderInterval.text = intervalText
                        } else {
                            val intervalText = "0" + (user.remindInterval / 60).toString() + " : " + "0" + (user.remindInterval % 60).toString()
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
                    }
                }
            }
            val timePickerDialog = TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, 2, 15, true)
            timePickerDialog.setTitle("Choose interval:")
            timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }
    }

    private fun createWorkday() {
        val today = Calendar.getInstance().timeInMillis
        lastWorkday = Workday(today.toString(), System.currentTimeMillis(), System.currentTimeMillis(), 0)

        doAsync {
            AppDatabase.getInstance(this@MainActivity).workdayModel().insertWorkday(lastWorkday)
        }
        startWorkout()
    }

    private fun startWorkout() {
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
}
