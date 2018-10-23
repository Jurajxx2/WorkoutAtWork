package net.trasim.workoutinwork

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SettingsFragment : PreferenceFragmentCompat(){

    private lateinit var startListener: Preference.OnPreferenceChangeListener
    private lateinit var endListener: Preference.OnPreferenceChangeListener
    private lateinit var lunchStart: TimePreference
    private lateinit var lunchEnd: TimePreference
    private lateinit var sharedPref: SharedPreferences

    private var lunchStartString: String = "12:00"
    private var lunchEndString: String = "12:30"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context)
        lunchStartString = sharedPref.getString("lunch_start", "12:00")
        lunchEndString = sharedPref.getString("lunch_end", "13:00")

        lunchStart = findPreference("lunch_start") as TimePreference
        lunchEnd = findPreference("lunch_end") as TimePreference

        //Listeners that listens to start, end lunch time should not be before start time and start should not be after end
        startListener = Preference.OnPreferenceChangeListener{ preference: Preference, any: Any ->
            val calendar1 = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, any.toString().substring(0,2).toInt())
                set(Calendar.MINUTE, any.toString().substring(3).toInt())
            }
            val calendar2 = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, lunchEndString.substring(0,2).toInt())
                set(Calendar.MINUTE, lunchEndString.substring(3).toInt())
            }
            if (calendar1.timeInMillis>calendar2.timeInMillis) {
                lunchStart.hour = lunchStartString.substring(0,2).toInt()
                lunchStart.minute = lunchStartString.substring(3).toInt()
            } else {
                lunchStartString = any.toString()
            }
            true
        }

        //Listeners that listens to end, end lunch time should not be before start time and start should not be after end
        endListener = Preference.OnPreferenceChangeListener{ preference: Preference, any: Any ->
            val calendar1 = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, any.toString().substring(0,2).toInt())
                set(Calendar.MINUTE, any.toString().substring(3).toInt())
            }
            val calendar2 = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, lunchStartString.substring(0,2).toInt())
                set(Calendar.MINUTE, lunchStartString.substring(3).toInt())
            }
            if (calendar1.timeInMillis<calendar2.timeInMillis) {
                lunchEnd.hour = lunchEndString.substring(0,2).toInt()
                lunchEnd.minute = lunchEndString.substring(3).toInt()
            } else {
                lunchEndString = any.toString()
            }
            true
        }
    }

    override fun onResume() {
        lunchStart.onPreferenceChangeListener = startListener
        super.onResume()
    }

    override fun onStop() {
        lunchStart.onPreferenceChangeListener = null
        super.onStop()
    }

    //Help function when displaying custom time preference dialog because of support v7
    override fun onDisplayPreferenceDialog(preference: Preference) {
        var dialogFragment: DialogFragment? = null
        if (preference is TimePreference) {
            dialogFragment = TimePreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString("key", preference.getKey())
            dialogFragment.arguments = bundle
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(this.fragmentManager, "android.support.v7.preference.PreferenceFragment.DIALOG")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}
