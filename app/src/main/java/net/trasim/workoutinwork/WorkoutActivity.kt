package net.trasim.workoutinwork

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.transition.Visibility
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import net.trasim.workoutinwork.database.AppDatabase
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.User
import net.trasim.workoutinwork.objects.Workday
import net.trasim.workoutinwork.objects.Workout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*


class WorkoutActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mWorkout: Workout
    private lateinit var exercises: List<Exercise>
    private lateinit var lastWorkday: Workday

    private lateinit var buttonNext: Button
    private lateinit var buttonFinish: Button
    private lateinit var buttonEnd: Button
    private lateinit var countdownButton: Button

    private lateinit var countdownTimer: TextView
    private lateinit var title: TextView
    private lateinit var description: TextView
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

    private lateinit var timer: CountDownTimer2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        title = findViewById(R.id.exerciseName)
        description = findViewById(R.id.exerciseDescription)
        img = findViewById(R.id.exerciseGif)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        countdownTimer = findViewById(R.id.countdownTimer)

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

        if (reminder){
            workoutNextReminder = System.currentTimeMillis()
            saveSharedPref()
        }

        doAsync {
            exercises = AppDatabase.getInstance(this@WorkoutActivity).exerciseModel().allExercises
            lastWorkday = AppDatabase.getInstance(this@WorkoutActivity).workdayModel().getLastWorkday()
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
            if (exercisesDone==exercisesInWorkout){
                finish()

            } else {
                if (countdownButton.visibility == View.VISIBLE || countdownTimer.visibility == View.VISIBLE){
                    countdownButton.visibility = View.GONE
                    countdownTimer.visibility = View.GONE
                }
                nextExercise()
            }
        }

        buttonFinish.setOnClickListener {
            if (reminder) {
                val alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, MyBroadcastReceiver::class.java)
                val alarmIntent = PendingIntent.getBroadcast(this.applicationContext, 0, intent, 0)

                alarmMgr.cancel(alarmIntent)
                saveSharedPref()
            }
            finish()
        }

        buttonEnd.setOnClickListener {
            finish()
        }

        countdownButton.setOnClickListener {
            when {
                timer.timeLeft()>0 -> timer.pause()
                timer.isPaused -> timer.resume()
                else -> timer.create()
            }
        }
    }

    private fun nextExercise(){
        exercisesDone++
        doAsync{
            val enabledExercises = AppDatabase.getInstance(this@WorkoutActivity).exerciseModel().getEnabledExercises()

            if (enabledExercises.isEmpty()){
                toast("Please, enable at least one exercise in order to start workout")
                finish()
            }
            val randomNr = (0 until enabledExercises.size).random()

            lastWorkday.workouts ++
            AppDatabase.getInstance(this@WorkoutActivity).workdayModel().updateWorkday(lastWorkday)
            mWorkout = Workout(lastWorkday.id)
            mWorkout.exerciseID = enabledExercises[randomNr].id
            AppDatabase.getInstance(this@WorkoutActivity).workoutModel().insertWorkout(mWorkout)
            uiThread {
                updateUI(enabledExercises[randomNr])
            }
        }
    }

    private fun updateUI(exercise: Exercise){
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
            countdownButton.visibility = View.VISIBLE
            countdownTimer.visibility = View.VISIBLE

            countdown = (exercise.duration * 1000).toLong()

            timer = object : CountDownTimer2(countdown, 1000, true) {

                override fun onTick(millisUntilFinished: Long) {
                    countdownTimer.text = (millisUntilFinished/1000).toString() + "s"
                }

                override fun onFinish() {
                    countdownTimer.text = "0s"
                }
            }

        } else if (exercise.repetitions>0){
            popis = "\nDo it " + exercise.repetitions.toString() + " times"
        }
        description.text = exercise.heading + popis
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
}

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start

