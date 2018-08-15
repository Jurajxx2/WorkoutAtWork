package net.trasim.workoutinwork

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import org.jetbrains.anko.toast
import java.util.*

class ReminderService : Service() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private var time: Int = 60
    private val mBinder = MyBinder()

    override fun onBind(intent: Intent): IBinder? {
        toast("Service binded.")
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        toast("Service rebinded.")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        toast("Service unbinded.")
        return true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Send a notification that service is started
        toast("Service started.")

        // Do a periodic task
        mHandler = Handler()
        mRunnable = Runnable { showRandomNumber() }
        mHandler.postDelayed(mRunnable, 1000)

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        toast("Service destroyed.")
        mHandler.removeCallbacks(mRunnable)
    }

    // Custom method to do a task
    private fun showRandomNumber() {
        if (time==0){
            onDestroy()
        }
        time--
        //val rand = Random()
        //val number = rand.nextInt(100)
        //toast("Random Number : $number")
        mHandler.postDelayed(mRunnable, 1000)
    }

    inner class MyBinder : Binder() {
        internal val service: ReminderService
            get() = this@ReminderService
    }
}