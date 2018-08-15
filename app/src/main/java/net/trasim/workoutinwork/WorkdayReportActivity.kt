package net.trasim.workoutinwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import net.trasim.workoutinwork.adapters.WorkoutsRecycleAdapter
import net.trasim.workoutinwork.database.AppDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class WorkdayReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workday_report)

        val extras = intent.extras

        doAsync {
            val database = AppDatabase.getInstance(this@WorkdayReportActivity)
            val exercises = database.exerciseModel().allExercises
            val workouts = database.workoutModel().getWorkoutsByWorkdayID(extras.getInt("workdayID"))
            uiThread {
                var recyclerView = findViewById<RecyclerView>(R.id.workoutsList)
                var mAdapter = WorkoutsRecycleAdapter(workouts, exercises)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                recyclerView!!.layoutManager = mLayoutManager
                recyclerView!!.itemAnimator = DefaultItemAnimator()
                recyclerView!!.adapter = mAdapter
            }
        }
    }
}
