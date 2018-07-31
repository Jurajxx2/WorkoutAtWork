package net.trasim.workoutinwork

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
import android.widget.Toast
import net.trasim.workoutinwork.Database.AppDatabase
import net.trasim.workoutinwork.Database.exercises
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class WorkoutActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mWorkout: Workout
    private lateinit var exercises: List<Exercise>
    private lateinit var lastWorkday: Workday

    private lateinit var buttonNext: Button
    private lateinit var buttonFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        doAsync {
            exercises = AppDatabase.getInstance(this@WorkoutActivity).exerciseModel().allExercises
            lastWorkday = AppDatabase.getInstance(this@WorkoutActivity).workdayModel().getLastWorkday()
        }
        nextExercise()

        mDrawerLayout = findViewById(R.id.drawer_layout)
        buttonNext = findViewById(R.id.buttonNext)
        buttonFinish = findViewById(R.id.buttonFinish)

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

        buttonNext.setOnClickListener{
            nextExercise()
        }

        buttonFinish.setOnClickListener {
            finish()
        }
    }

    private fun nextExercise(){
        doAsync{
            lastWorkday.workouts ++
            AppDatabase.getInstance(this@WorkoutActivity).workdayModel().updateWorkday(lastWorkday)
            mWorkout = Workout(lastWorkday.id)
            mWorkout.exerciseID = (1..13).random()
            AppDatabase.getInstance(this@WorkoutActivity).workoutModel().insertWorkout(mWorkout)
            uiThread {
                updateUI()
            }
        }
    }

    private fun updateUI(){
        val title: TextView = findViewById(R.id.exerciseName)
        val description: TextView = findViewById(R.id.exerciseDescription)
        val img: pl.droidsonroids.gif.GifImageView = findViewById(R.id.exerciseGif)

        doAsync {
        val database = AppDatabase.getInstance(this@WorkoutActivity)
        val exercise = database.exerciseModel().getExerciseByID(mWorkout.exerciseID)
            uiThread {
                title.text = exercise.name

                when(exercise.img){
                    "pushup" -> img.setImageResource(R.drawable.pushup)
                    "jacks" -> img.setImageResource(R.drawable.jacks)
                    "leftplank" -> img.setImageResource(R.drawable.leftplank)
                    "lunge" -> img.setImageResource(R.drawable.lunge)
                    "plank" -> img.setImageResource(R.drawable.plank)
                    "rightplank" -> img.setImageResource(R.drawable.rightplank)
                    "rotation" -> img.setImageResource(R.drawable.rotation)
                    "running" -> img.setImageResource(R.drawable.runnung)
                    "squat" -> img.setImageResource(R.drawable.squat)
                    "stepup" -> img.setImageResource(R.drawable.stepup)
                    "triceps" -> img.setImageResource(R.drawable.triceps)
                    "wallsit" -> img.setImageResource(R.drawable.wallsit)
                    "abcrunch" -> img.setImageResource(R.drawable.abcrunch)
                    else -> img.setImageResource(R.drawable.coffee)
                }

                var popis = ""
                if (exercise.duration>0){
                    popis = "\nDo it for " + exercise.duration.toString() + " seconds"
                } else if (exercise.repetitions>0){
                    popis = "\nDo it " + exercise.repetitions.toString() + " times"
                }
                description.text = exercise.heading + popis
            }
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

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start

