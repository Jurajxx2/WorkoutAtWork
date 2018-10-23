package net.trasim.workoutinwork

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import net.trasim.workoutinwork.database.AppDatabase
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.Workday
import net.trasim.workoutinwork.objects.Workout
import org.jetbrains.anko.*
import java.util.*


class WorkoutActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mWorkout: Workout
    private lateinit var lastWorkday: Workday

    private lateinit var buttonNext: Button
    private lateinit var buttonFinish: Button
    private lateinit var buttonEnd: Button
    private lateinit var countdownButton: Button

    private lateinit var countdownTimer: TextView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var intensity: TextView
    private lateinit var img: pl.droidsonroids.gif.GifImageView

    private var exercisesInWorkout: Int = 0
    private var exercisesDone: Int = 0

    private lateinit var sharedPref: SharedPreferences

    private var weight: Float = 0.toFloat()
    private var height: Float = 0.toFloat()
    private var isOK: Boolean = false
    private var workoutReminderInterval: Long = 0
    private var workoutNextReminder: Long = 0
    private var reminder: Boolean = false
    private var countdown: Long = 0

    private var doubleBackToExitPressedOnce = false

    private lateinit var database: AppDatabase

    private lateinit var timer: CountDownTimer2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        //Initiate variables
        database = AppDatabase.getInstance(this)

        title = findViewById(R.id.exerciseName)
        description = findViewById(R.id.exerciseDescription)
        img = findViewById(R.id.exerciseGif)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        countdownTimer = findViewById(R.id.countdownTimer)
        intensity = findViewById(R.id.intensity)

        buttonNext = findViewById(R.id.buttonNext)
        buttonFinish = findViewById(R.id.buttonFinish)
        buttonEnd = findViewById(R.id.buttonEnd)
        countdownButton = findViewById(R.id.countdownButton)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        weight = sharedPref.getString("weight", "0").toFloat()
        height = sharedPref.getString("height", "0").toFloat()
        isOK = sharedPref.getBoolean("isOK", false)
        workoutReminderInterval = sharedPref.getString("reminder_interval", "7200000").toLong()
        workoutNextReminder = sharedPref.getLong("next_interval", 0)
        reminder = sharedPref.getBoolean("workout_reminder", false)
        exercisesInWorkout = sharedPref.getString("noOfExercises", "4").toInt()

        //If reminder is on, refresh next time interval
        if (reminder){
            workoutNextReminder = System.currentTimeMillis()
            saveSharedPref()
        } else {
            buttonFinish.visibility = View.GONE
        }

        //Load exercises and last workday, update UI with next exercise
        doAsync {
            lastWorkday = database.workdayModel().getLastWorkday()
            uiThread {
                nextExercise()
            }
        }

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //Menu
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            when (menuItem.itemId){
                R.id.home_btn -> {
                    alert("Are you sure you want to end this workout?", "End workout") {
                        yesButton {
                            val intent = Intent(this@WorkoutActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        noButton {}
                    }.show()
                }
                R.id.workouts_history_btn -> {
                    alert("Are you sure you want to end this workout?", "End workout") {
                        yesButton {
                            val intent = Intent(this@WorkoutActivity, WorkoutHistoryActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        noButton {}
                    }.show()
                }
                R.id.settings_btn -> {
                    alert("Are you sure you want to end this workout?", "End workout") {
                        yesButton {
                            val intent = Intent(this@WorkoutActivity, SettingsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        noButton {}
                    }.show()
                }
                R.id.workout_list_btn -> {
                    alert("Are you sure you want to end this workout?", "End workout") {
                        yesButton {
                            val intent = Intent(this@WorkoutActivity, WorkoutListActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        noButton {}
                    }.show()
                }
                R.id.info -> {
                    alert("Are you sure you want to end this workout?", "End workout") {
                        yesButton {
                            val intent = Intent(this@WorkoutActivity, InfoActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        noButton {}
                    }.show()
                }
            }
            true
        }

        buttonNext.setOnClickListener{
            if (exercisesDone==exercisesInWorkout){
                finish()

            } else {
                if (countdownButton.visibility == View.VISIBLE || countdownTimer.visibility == View.VISIBLE){
                    if (timer.timeLeft()>0){
                        toast("Please, finish the exercise to proceed")
                        return@setOnClickListener
                    }
                    countdownButton.visibility = View.GONE
                    countdownTimer.visibility = View.GONE
                    timer.cancel()
                }
                nextExercise()
            }
        }

        //Button to finish workout and reminder if enabled
        buttonFinish.setOnClickListener {
            if (reminder) {
                val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, MyBroadcastReceiver::class.java)
                val alarmIntent = PendingIntent.getBroadcast(this.applicationContext, 0, intent, 0)

                alarmMgr.cancel(alarmIntent)
                reminder = false
                saveSharedPref()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Button to leave workout
        buttonEnd.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Working with countdown for duration exercises
        countdownButton.setOnClickListener {
            when {
                timer.timeLeft()>0 && !timer.isPaused -> {
                    timer.pause()
                    countdownButton.text = getString(R.string.start)
                }
                timer.isPaused -> {
                    timer.resume()
                    countdownButton.text = getString(R.string.pause)
                }
                else -> {
                    timer.create()
                    countdownButton.text = getString(R.string.pause)
                }
            }
        }
    }

    //Function to select next exercise by random number
    private fun nextExercise(){
        //Update number of done exercises
        exercisesDone++
        doAsync{
            val enabledExercises = database.exerciseModel().getEnabledExercises()

            if (enabledExercises.isEmpty()){
                toast("Please, enable at least one exercise in order to start workout")
                finish()
            }
            //Get random nr
            val randomNr = (0 until enabledExercises.size).random()

            //Update workday
            lastWorkday.workouts++
            database.workdayModel().updateWorkday(lastWorkday)
            mWorkout = Workout(lastWorkday.id)
            mWorkout.exerciseID = enabledExercises[randomNr].id
            uiThread {
                updateUI(enabledExercises[randomNr])
            }
        }
    }

    //Function updates UI with selected exercise
    private fun updateUI(exercise: Exercise){
        //Hide button if it is last workout
        if (exercisesDone==exercisesInWorkout){
            buttonNext.visibility = View.INVISIBLE
        }

        title.text = exercise.name
        when(exercise.img){
            "pushup" -> img.setImageResource(R.drawable.pushup)
            "jacks" -> img.setImageResource(R.drawable.jacks)
            "leftplank" -> img.setImageResource(R.drawable.leftplank)
            "lunge" -> img.setImageResource(R.drawable.lunge)
            "plank" -> img.setImageResource(R.drawable.plank)
            "rightplank" -> img.setImageResource(R.drawable.rightplank)
            "running" -> img.setImageResource(R.drawable.running)
            "squat" -> img.setImageResource(R.drawable.squat)
            "stepup" -> img.setImageResource(R.drawable.stepup)
            "triceps" -> img.setImageResource(R.drawable.triceps)
            "wallsit" -> img.setImageResource(R.drawable.wallsit)
            "abcrunch" -> img.setImageResource(R.drawable.abcrunch)
            else -> img.setImageResource(R.drawable.coffee)
        }

        var popis = ""
        if (exercise.duration>0){
            mWorkout.duration = exercise.duration
            popis = "Do it for " + exercise.duration.toString() + " seconds"
            countdownButton.visibility = View.VISIBLE
            countdownButton.text = getString(R.string.start)
            countdownButton.isEnabled = true
            countdownTimer.visibility = View.VISIBLE

            var exerciseDuration = exercise.duration.toString() + "s"
            countdownTimer.text = exerciseDuration

            countdown = (exercise.duration * 1000).toLong()

            timer = object : CountDownTimer2(countdown, 1000, true) {

                override fun onTick(millisUntilFinished: Long) {
                    exerciseDuration = (millisUntilFinished/1000).toString() + "s"
                    countdownTimer.text = exerciseDuration
                }

                override fun onFinish() {
                    countdownButton.isEnabled = false
                    countdownTimer.text = getString(R.string.finished)
                }
            }

        } else if (exercise.repetitions>0){
            mWorkout.repetitions = exercise.repetitions
            popis = "Do it " + exercise.repetitions.toString() + " times"
        }

        doAsync {
            database.workoutModel().insertWorkout(mWorkout)
        }

        description.text = exercise.description
        intensity.text = popis
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
            putBoolean("isOK", isOK)
            putBoolean("workout_reminder", reminder)
            putLong("remind_interval", workoutReminderInterval)
            putLong("next_interval", workoutNextReminder)
            apply()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to finish workout", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start

