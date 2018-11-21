package net.trasim.workoutinwork.recievers

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.Toast
import net.trasim.workoutinwork.R
import net.trasim.workoutinwork.activities.WorkoutActivity
import java.util.*

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

    private var dailyReminder: Long = 0
    private lateinit var days: Set<String>

    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "Alarm", Toast.LENGTH_LONG).show()

        //Define alarmmanager
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent2 = Intent(context, MyBroadcastReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent2, 0)


        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        reminder = sharedPref.getBoolean("workout_reminder", false)
        //vibrations = sharedPref.getBoolean("vibrations", false)
        //sounds = sharedPref.getBoolean("sounds", false)
        workoutNextReminder = sharedPref.getLong("next_interval", 0)
        workoutReminderInterval = sharedPref.getString("reminder_interval", "7200000").toLong()
        dailyReminder = conversion(sharedPref.getString("select_days_time", "08:00"))
        days = sharedPref.getStringSet("select_days", setOf())


        //If was launched after boot completed
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            setDailyAlarm(context)

            if (reminder) {
                if (workoutNextReminder + workoutReminderInterval > System.currentTimeMillis()) {
                    alarmMgr.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            workoutNextReminder + workoutReminderInterval,
                            workoutReminderInterval,
                            alarmIntent
                    )
                    return
                }
            }
            return
        }

        //Otherwise set last reminder launch time to actual time
        workoutNextReminder = System.currentTimeMillis()
        reminder = true
        saveSharedPref()


        //If lunch break option is true, check if this time is not during lunch break, otherwise return
        if (sharedPref.getBoolean("lunch_break", false)) {
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

            if (now.time > start.time && now.time < end.time) {
                return
            }
        }

        //Sound playing
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Notification channel in oreo and higher
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

            //Added sound to notification channel
            channel.setSound(Uri.parse("android.resource://" + MyBroadcastReceiver::class.java.`package`.name + "/" + R.raw.siren), audioAttributes)

            // Register the channel with the system
            notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        } else {
            notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager?
        }

        //Setting pending intent for notification
        val workoutIntent = Intent(context, WorkoutActivity::class.java)
        workoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //intent.putExtra("X", "X")
        val pendingIntent = PendingIntent.getActivity(context, 0, workoutIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, channelID)
                //.setDefaults(Notification.DEFAULT_SOUND.inv())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker("Workout reminder")  // the status text
                .setContentTitle("Ready for workout?")  // the label of the entry
                .setContentText("Time for your next workout, click to start")  // the contents of the entry
                .setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(Uri.parse("android.resource://" + MyBroadcastReceiver::class.java.`package`.name + "/" + R.raw.siren))

        val notificationManagerC = NotificationManagerCompat.from(context)
        val finalNotification = notification.build()

        finalNotification.flags = Notification.FLAG_INSISTENT or Notification.FLAG_AUTO_CANCEL

        notificationManagerC.notify(notificationID, finalNotification)
    }

    private fun saveSharedPref() {
        with(sharedPref.edit()) {
            putBoolean("workout_reminder", reminder)
            putLong("next_interval", workoutNextReminder)
            apply()
        }
    }

    //Set daily alarm function for days in list form settings
    //Function to set daily alarm based on list of days from settings
    private fun setDailyAlarm(context: Context){
        days.forEach {
            when(it){
                "1" -> {
                    val calendar: Calendar = Calendar.getInstance().apply {
                        timeInMillis = dailyReminder
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    }

                    val timeFromNow = if(calendar.timeInMillis>System.currentTimeMillis()){
                        calendar.timeInMillis-System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis-System.currentTimeMillis() + 604800000
                    }

                    val alarmMgr2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(context, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(context.applicationContext, 1, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + timeFromNow,
                            604800000,
                            alarmIntent2
                    )
                }
                "2" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                }

                    val timeFromNow = if(calendar.timeInMillis>System.currentTimeMillis()){
                        calendar.timeInMillis-System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis-System.currentTimeMillis() + 604800000
                    }

                    val alarmMgr2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(context, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(context.applicationContext, 2, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + timeFromNow,
                            604800000,
                            alarmIntent2
                    ) }
                "3" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                }

                    val timeFromNow = if(calendar.timeInMillis>System.currentTimeMillis()){
                        calendar.timeInMillis-System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis-System.currentTimeMillis() + 604800000
                    }

                    val alarmMgr2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(context, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(context.applicationContext, 3, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + timeFromNow,
                            604800000,
                            alarmIntent2
                    ) }
                "4" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                }

                    val timeFromNow = if(calendar.timeInMillis>System.currentTimeMillis()){
                        calendar.timeInMillis-System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis-System.currentTimeMillis() + 604800000
                    }

                    val alarmMgr2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(context, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(context.applicationContext, 4, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + timeFromNow,
                            604800000,
                            alarmIntent2
                    ) }
                "5" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                }

                    val timeFromNow = if(calendar.timeInMillis>System.currentTimeMillis()){
                        calendar.timeInMillis-System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis-System.currentTimeMillis() + 604800000
                    }

                    val alarmMgr2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(context, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(context.applicationContext, 5, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + timeFromNow,
                            604800000,
                            alarmIntent2
                    ) }
                "6" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                }

                    val timeFromNow = if(calendar.timeInMillis>System.currentTimeMillis()){
                        calendar.timeInMillis-System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis-System.currentTimeMillis() + 604800000
                    }

                    val alarmMgr2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(context, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(context.applicationContext, 6, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + timeFromNow,
                            604800000,
                            alarmIntent2
                    ) }
                "7" -> { val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = dailyReminder
                    set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                }

                    val timeFromNow = if(calendar.timeInMillis>System.currentTimeMillis()){
                        calendar.timeInMillis-System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis-System.currentTimeMillis() + 604800000
                    }

                    val alarmMgr2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(context, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(context.applicationContext, 7, intent2, 0)

                    alarmMgr2.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + timeFromNow,
                            604800000,
                            alarmIntent2
                    ) }
                else -> {
                    val alarmMgr2 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent2 = Intent(context, MyBroadcastReceiver::class.java)
                    val alarmIntent2 = PendingIntent.getBroadcast(context.applicationContext, it.toInt(), intent2, 0)

                    alarmMgr2.cancel(alarmIntent2)
                }
            }
        }
    }


    private fun conversion(time: String): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, time.substring(0, 2).toInt())
            set(Calendar.MINUTE, time.substring(3).toInt())
        }
        return calendar.timeInMillis
    }

}