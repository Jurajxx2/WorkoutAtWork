package net.trasim.workoutinwork

import android.app.*
import android.content.Context.VIBRATOR_SERVICE
import android.os.Vibrator
import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.VibrationEffect
import android.os.Build
import android.provider.Settings.Global.getString
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.media.RingtoneManager
import android.media.Ringtone
import android.net.Uri
import android.preference.PreferenceManager
import java.util.*
import android.media.AudioAttributes




class MyBroadcastReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManager? = null
    private val channelID = "my_channel_id"
    private val channelName = "Workout Notification Channel"
    private val channelDescription = "Notification channel to remind workouts"
    private val notificationID = 2

    private var workoutNextReminder: Long = 0
    private lateinit var sharedPref: SharedPreferences
    private var reminder: Boolean = false
    private var workoutReminderInterval: Long = 0

    override fun onReceive(context: Context, intent: Intent) {

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent2 = Intent(context, MyBroadcastReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent2, 0)


        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        reminder = sharedPref.getBoolean("workout_reminder", false)
        workoutNextReminder = sharedPref.getLong("next_interval", 0)
        workoutReminderInterval = sharedPref.getString("reminder_interval", "7200000").toLong()


        workoutNextReminder = System.currentTimeMillis()
        saveSharedPref()

        if (!reminder){
            alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + workoutReminderInterval,
                    workoutReminderInterval,
                    alarmIntent
            )

            reminder = true
            saveSharedPref()
        }

        if (intent.getBooleanExtra("skip", false)){
            return
        }

        if (intent.getBooleanExtra("end", false)){

            alarmMgr.cancel(alarmIntent)
            reminder = false
            saveSharedPref()

            return
        }

        if (sharedPref.getBoolean("lunch_break", false)){
            val lunchStart = sharedPref.getString("lunch_start", "0")
            val lunchEnd = sharedPref.getString("lunch_end", "0")

            val start = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, lunchStart.substring(0, 2).toInt())
                set(Calendar.MINUTE, lunchStart.substring(3).toInt())
            }

            val end = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, lunchEnd.substring(0, 2).toInt())
                set(Calendar.MINUTE, lunchEnd.substring(3).toInt())
            }

            val now = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
            }

            if (now.time>start.time && now.time<end.time){
                return
            }
        }

        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creating an Audio Attribute
            val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = channelName
            val description = channelDescription
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("my_channel_id", name, importance)
            channel.description = description

            channel.setSound(Uri.parse("android.resource://"+ MyBroadcastReceiver::class.java.`package`.name + "/" + R.raw.siren), audioAttributes)
            // Register the channel with the system
            notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        } else {
            notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager?
        }




        val intent = Intent(context, WorkoutActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //intent.putExtra("X", "X")
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val startIntent = Intent(context, WorkoutActivity::class.java)
        val pendingStartIntent = PendingIntent.getActivity(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val skipIntent = Intent(context, MyBroadcastReceiver::class.java)
        skipIntent.putExtra("skip", true)
        val pendingSkipIntent = PendingIntent.getActivity(context, 0, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val endIntent = Intent(context, MyBroadcastReceiver::class.java)
        endIntent.putExtra("end", true)
        val pendingEndIntent = PendingIntent.getActivity(context, 0, endIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, channelID)
                //.setDefaults(Notification.DEFAULT_SOUND.inv())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker("Workout reminder")  // the status text
                .setContentTitle("Ready for workout?")  // the label of the entry
                .setContentText("Time for your next workout, click to start")  // the contents of the entry
                .setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(R.drawable.ic_info_black_24dp, "Start", pendingStartIntent)
                .addAction(R.drawable.ic_info_black_24dp, "Skip", pendingSkipIntent)
                .addAction(R.drawable.ic_info_black_24dp, "End for Today", pendingEndIntent)
                .setSound(Uri.parse("android.resource://"+ MyBroadcastReceiver::class.java.`package`.name + "/" + R.raw.siren))
                //.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        val notificationManagerC = NotificationManagerCompat.from(context)
        val finalNotification = notification.build()
        finalNotification.flags = Notification.FLAG_INSISTENT or Notification.FLAG_AUTO_CANCEL

        notificationManagerC.notify(notificationID, finalNotification)
    }

    private fun saveSharedPref(){
        with(sharedPref.edit()){
            putBoolean("workout_reminder", reminder)
            putLong("next_interval", workoutNextReminder)
            apply()
        }
    }

}