package net.trasim.workoutinwork

import android.app.AlarmManager
import android.app.Fragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.preference.PreferenceScreen
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.TimePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.defaultSharedPreferences
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    private var weight: Float = 0.toFloat()
    private var height: Float = 0.toFloat()
    private var isOK: Boolean = false
    private var workoutReminderInterval: Long = 0
    private var workoutNextReminder: Long = 0
    private var reminder: Boolean = false
    private var dayReminder: Boolean = false

    private var dailyReminderString: String = "08:00"
    private var lunchStartString: String = "12:00"
    private var lunchEndString: String = "12:30"

    private var dailyReminder: Long = 0
    private var lunchStart: Long = 0
    private var lunchEnd: Long = 0

    private lateinit var mDrawerLayout: DrawerLayout

    private lateinit var prefs: SharedPreferences
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(this.applicationContext, 0, intent, 0)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        weight = sharedPref.getString("weight", "0").toFloat()
        height = sharedPref.getString("height", "0").toFloat()
        isOK = sharedPref.getBoolean("isOK", false)
        workoutReminderInterval = sharedPref.getString("reminder_interval", "7200000").toLong()
        workoutNextReminder = sharedPref.getLong("next_interval", 0)
        reminder = sharedPref.getBoolean("workout_reminder", false)
        dayReminder = sharedPref.getBoolean("day_reminder", false)

        dailyReminderString = sharedPref.getString("select_days_time", "08:00")
        lunchStartString = sharedPref.getString("lunch_start", "11:30")
        lunchEndString = sharedPref.getString("lunch_end", "12:00")

        dailyReminder = conversion(dailyReminderString)
        lunchStart = conversion(lunchStartString)
        lunchEnd = conversion(lunchEndString)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key=="workout_reminder"){
                reminder = prefs.getBoolean("workout_reminder", false)
                if(reminder){
                    alarmMgr.set(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + workoutReminderInterval,
                            alarmIntent
                    )
                    workoutNextReminder = System.currentTimeMillis()
                    saveSharedPref()
                } else {
                    alarmMgr?.cancel(alarmIntent)
                }
            }
            if (key=="reminder_interval"){
                alarmMgr?.cancel(alarmIntent)
                workoutReminderInterval = prefs.getString("reminder_interval", "7200000").toLong()
                alarmMgr.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + workoutReminderInterval,
                        workoutReminderInterval,
                        alarmIntent
                )
                workoutNextReminder = System.currentTimeMillis()
                saveSharedPref()
            }
            if (key=="day_reminder"){
                if (dayReminder){
                    setDailyAlarm()
                } else {
                    for (i in 1..7){
                        val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                        val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, i, intent2, 0)

                        alarmMgr2.cancel(alarmIntent2)
                    }
                }
            }
            if (key=="select_days"){
                setDailyAlarm()
            }

            if (key=="select_days_time"){
                dailyReminder = conversion(prefs.getString("select_days_time", "0"))
                setDailyAlarm()
            }

            if (key =="lunch_start"){
                lunchStart = conversion(prefs.getString("lunch_start", "0"))
                if (lunchStart>lunchEnd){
                    sharedPref.edit().putString("lunch_start", lunchStartString).apply()
                    toast("Please note, that lunch break start cannot be later than lunch break end")
                } else {
                    lunchStartString = sharedPref.getString("lunch_start", "0")
                }
            }

            if (key =="lunch_end"){
                lunchEnd = conversion(prefs.getString("lunch_end", "0"))
                if (lunchStart>lunchEnd){
                    sharedPref.edit().putString("lunch_end", lunchEndString).apply()
                    toast("Please note, that lunch break end cannot be earlier than lunch break start")
                } else {
                    lunchEndString = sharedPref.getString("lunch_end", "0")
                }
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)

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
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
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

    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setDailyAlarm(){
        prefs.getStringSet("select_days", setOf()).forEach {
            toast(it)
            when(it){
                "1" -> {
                    val calendar: Calendar = Calendar.getInstance().apply {
                        timeInMillis = dailyReminder
                        set(Calendar.DAY_OF_WEEK, 0)
                    }

                    val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, 1, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            604800000,
                            alarmIntent2
                    )
                }
                "2" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, 1)
                }

                    val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, 2, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            604800000,
                            alarmIntent2
                    ) }
                "3" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, 2)
                }

                    val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, 3, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            604800000,
                            alarmIntent2
                    ) }
                "4" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, 3)
                }

                    val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, 4, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            604800000,
                            alarmIntent2
                    ) }
                "5" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, 4)
                }

                    val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, 5, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            604800000,
                            alarmIntent2
                    ) }
                "6" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, 5)
                }

                    val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, 6, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            604800000,
                            alarmIntent2
                    ) }
                "7" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, 6)
                }

                    val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, 7, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            604800000,
                            alarmIntent2
                    ) }
                else -> {
                    val alarmMgr2 = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(this, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(this.applicationContext, it.toInt(), intent2, 0)

                    alarmMgr2.cancel(alarmIntent2)
                }
            }
        }
    }

    private fun conversion(time: String): Long {
        val calendar = Calendar.getInstance().apply{
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, time.substring(0, 2).toInt())
            set(Calendar.MINUTE, time.substring(3).toInt())
        }
        return calendar.timeInMillis
    }

    override fun onStop() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
        super.onStop()
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


    private fun saveSharedPref(){
        with(sharedPref.edit()){
            putBoolean("workout_reminder", reminder)
            putLong("remind_interval", workoutReminderInterval)
            putLong("next_interval", workoutNextReminder)
            apply()
        }
    }

}
