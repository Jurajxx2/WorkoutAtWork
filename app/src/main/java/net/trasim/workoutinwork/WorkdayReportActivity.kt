package net.trasim.workoutinwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import net.trasim.workoutinwork.adapters.WorkoutsRecycleAdapter
import net.trasim.workoutinwork.database.AppDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class WorkdayReportActivity : AppCompatActivity() {

    private lateinit var date: TextView
    private lateinit var workoutsNo: TextView
    private lateinit var start: TextView
    private lateinit var end: TextView
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workday_report)

        database = AppDatabase.getInstance(this@WorkdayReportActivity)

        val extras = intent.extras

        date = findViewById(R.id.workdayDate)
        start = findViewById(R.id.workdayStart)
        end = findViewById(R.id.workdayEnd)
        workoutsNo = findViewById(R.id.workdayWorkouts)

        //Get data from database and update UI
        doAsync {
            val exercises = database.exerciseModel().allExercises
            val workouts = database.workoutModel().getWorkoutsByWorkdayID(extras.getInt("workdayID"))
            val workday = database.workdayModel().getWorkdayByID(extras.getInt("workdayID"))
            uiThread {
                val myCalender = Calendar.getInstance().apply {
                    timeInMillis = workday.date!!.toLong()
                }

                //Date of the workday
                date.text = myCalender.get(Calendar.DAY_OF_MONTH).toString() + "." + myCalender.get(Calendar.MONTH) + "."

                val myCalender2 = Calendar.getInstance().apply {
                    timeInMillis = workday.start
                }

                val myCalender3 = Calendar.getInstance().apply {
                    timeInMillis = workday.end
                }

                //Set start and end time + number of workouts during that workday
                start.text = myCalender2.get(Calendar.HOUR_OF_DAY).toString() + ":" + myCalender2.get(Calendar.MINUTE).toString()
                end.text = myCalender3.get(Calendar.HOUR_OF_DAY).toString() + ":" + myCalender3.get(Calendar.MINUTE).toString()
                workoutsNo.text = workouts.size.toString()

                //Add workdays to recycler view
                val recyclerView = findViewById<RecyclerView>(R.id.workoutsList)
                val mAdapter = WorkoutsRecycleAdapter(workouts, exercises)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                recyclerView!!.layoutManager = mLayoutManager
                recyclerView!!.itemAnimator = DefaultItemAnimator()
                recyclerView!!.adapter = mAdapter
            }
        }
    }
}
