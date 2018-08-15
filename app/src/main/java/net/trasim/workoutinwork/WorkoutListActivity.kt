package net.trasim.workoutinwork

import android.content.Intent
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
import net.trasim.workoutinwork.adapters.ExercisesRecycleAdapter
import net.trasim.workoutinwork.database.AppDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class WorkoutListActivity : AppCompatActivity() {
    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_selection)

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

        doAsync {
            val database = AppDatabase.getInstance(this@WorkoutListActivity)
            val exercises = database.exerciseModel().allExercises
            uiThread {
                var recyclerView = findViewById<RecyclerView>(R.id.exercisesList)
                var mAdapter = ExercisesRecycleAdapter(exercises)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                recyclerView!!.layoutManager = mLayoutManager
                recyclerView!!.itemAnimator = DefaultItemAnimator()
                recyclerView!!.adapter = mAdapter
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
