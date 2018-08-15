package net.trasim.workoutinwork

import android.app.Notification
import android.content.Context.VIBRATOR_SERVICE
import android.os.Vibrator
import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.VibrationEffect
import android.app.NotificationManager
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat


class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Time to workout!2", Toast.LENGTH_LONG).show()
    }

}