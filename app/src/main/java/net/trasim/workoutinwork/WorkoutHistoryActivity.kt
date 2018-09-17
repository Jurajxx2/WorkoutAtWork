package net.trasim.workoutinwork

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import net.trasim.workoutinwork.adapters.ExercisesRecycleAdapter
import net.trasim.workoutinwork.adapters.WorkoutsRecycleAdapter
import net.trasim.workoutinwork.database.AppDatabase
import net.trasim.workoutinwork.decorators.EventDecorator
import net.trasim.workoutinwork.decorators.OneDayDecorator
import net.trasim.workoutinwork.objects.Workday
import org.jetbrains.anko.*
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

                    val intent = Intent(this, WorkdayReportActivity::class.java)
                    intent.putExtra("workdayID", workday.id)
                    startActivity(intent)

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
