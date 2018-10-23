package net.trasim.workoutinwork

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.preference.PreferenceManager
import android.view.MenuItem
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import net.trasim.workoutinwork.database.AppDatabase
import net.trasim.workoutinwork.decorators.EventDecorator
import net.trasim.workoutinwork.decorators.OneDayDecorator
import net.trasim.workoutinwork.objects.Workday
import net.trasim.workoutinwork.objects.WorkdayMin
import net.trasim.workoutinwork.objects.Workout
import org.jetbrains.anko.*
import java.util.*
import kotlin.collections.ArrayList


class WorkoutHistoryActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mCalendarView: MaterialCalendarView
    private var oneDayDecorator = OneDayDecorator()
    private lateinit var workdays: List<WorkdayMin>
    private var workouts: Int = 0
    private lateinit var database: AppDatabase

    private lateinit var bmi: TextView
    private lateinit var workoutsNo: TextView
    private lateinit var workdaysNo: TextView

    private lateinit var sharedPref: SharedPreferences

    private var weight: Float = 0.toFloat()
    private var height: Float = 0.toFloat()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_history)

        mDrawerLayout = findViewById(R.id.drawer_layout)
        mCalendarView = findViewById(R.id.calendar)
        database = AppDatabase.getInstance(this)

        bmi = findViewById(R.id.bmiNo)
        workoutsNo = findViewById(R.id.workoutsNo)
        workdaysNo = findViewById(R.id.workDaysNo)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        weight = sharedPref.getString("weight", "0").toFloat()
        height = sharedPref.getString("height", "0").toFloat()

        mCalendarView.currentDate = CalendarDay.today()

        //Get workouts and workdays
        doAsync {
            workdays = database.workdayModel().getMinWorkdays
            workouts = database.workoutModel().countWorkouts
            val dates = ArrayList<CalendarDay>()
            for (workday in workdays){
                val c = Calendar.getInstance()
                c.timeInMillis = workday.date.toLong()
                dates.add(CalendarDay.from(c))
            }
            uiThread {
                //Update UI
                val decorator = EventDecorator(Color.RED, dates)
                mCalendarView.addDecorators(decorator, oneDayDecorator)

                bmi.text = (weight/((height/100)*(height/100))).toString()
                workoutsNo.text = (workouts - 1).toString()
                workdaysNo.text = (workdays.size - 1).toString()
            }
        }

        //On click listener to some date in calendar
        mCalendarView.setOnDateChangedListener { _ , calendarDay, b ->
            for (workday in workdays){
                val c = Calendar.getInstance()
                c.timeInMillis = workday.date.toLong()
                if (CalendarDay.from(c) == calendarDay){

                    //Launch dialog activity with info about selected date
                    val intent = Intent(this, WorkdayReportActivity::class.java)
                    intent.putExtra("workdayID", workday.id)
                    startActivity(intent)

                    return@setOnDateChangedListener
                }
            }
        }

        //Menu btn
        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //Menu
        val navigationView: NavigationView = findViewById((R.id.nav_view))
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
