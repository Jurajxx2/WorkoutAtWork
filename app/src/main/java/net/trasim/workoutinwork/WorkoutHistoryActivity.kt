package net.trasim.workoutinwork

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.view.MenuItem
import android.widget.DatePicker
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.activity_workout_history.*
import net.trasim.workoutinwork.Database.AppDatabase
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList


class WorkoutHistoryActivity : AppCompatActivity() {
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mCalendarView: MaterialCalendarView
    private var oneDayDecorator = OneDayDecorator()
    private lateinit var workdays: List<Workday>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_history)

        mDrawerLayout = findViewById(R.id.drawer_layout)
        mCalendarView = findViewById(R.id.calendar)

        mCalendarView.currentDate = CalendarDay.today()

        doAsync {
            workdays = AppDatabase.getInstance(this@WorkoutHistoryActivity).workdayModel().allWorkdays
            val dates = ArrayList<CalendarDay>()
            for (workday in workdays){
                val c = Calendar.getInstance()
                c.timeInMillis = workday.date!!.toLong()
                dates.add(CalendarDay.from(c))
            }
            uiThread {
                var decorator = EventDecorator(Color.RED, dates)
                mCalendarView.addDecorators(decorator, oneDayDecorator)
            }
        }

        mCalendarView.setOnDateChangedListener { materialCalendarView, calendarDay, b ->
            for (workday in workdays){
                val c = Calendar.getInstance()
                c.timeInMillis = workday.date!!.toLong()
                if (CalendarDay.from(c) == calendarDay){
                    alert {
                        title = workday.end.toString()
                        message = System.currentTimeMillis().toString()
                    }.show()
                    return@setOnDateChangedListener
                }
            }
        }


        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val navigationView: NavigationView = findViewById((R.id.nav_view))
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            when (menuItem.itemId){
                R.id.home_btn -> {
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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
