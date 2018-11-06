package net.trasim.workoutinwork.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.trasim.workoutinwork.R
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.Workout
import java.util.*

class WorkoutsRecycleAdapter(private val workoutsList: List<Workout>, private val exercises: List<Exercise>, private val context: Context) : RecyclerView.Adapter<WorkoutsRecycleAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.workoutName)
        var done: TextView = view.findViewById(R.id.done)
        var repetitions: TextView = view.findViewById(R.id.repetitions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.workout_list_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //Get workouts
        val workout = workoutsList[position]
        val exercise = exercises[workout.exerciseID - 1]

        //get language code
        val language = Locale.getDefault().isO3Language

        if (language == "SK"){
            holder.title.text = exercise.nameSK
        } else {
            holder.title.text = exercise.nameEN
        }

        var repetitions = ""
        if (workout.repetitions>0){
            holder.done.text = context.getString(R.string.repetitions)
            repetitions = workout.repetitions.toString() + "x"
        } else if (workout.duration>0){
            holder.done.text = context.getString(R.string.duration)
            repetitions = workout.duration.toString() + "s"
        }
        holder.repetitions.text = repetitions
    }

    override fun getItemCount(): Int {
        return workoutsList.size
    }
}