package net.trasim.workoutinwork.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.trasim.workoutinwork.R
import net.trasim.workoutinwork.R.id.exercisesList
import net.trasim.workoutinwork.database.exercises
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.Workout

class WorkoutsRecycleAdapter(private val workoutsList: List<Workout>, private val exercises: List<Exercise>) : RecyclerView.Adapter<WorkoutsRecycleAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.workoutName)
        var done: TextView = view.findViewById(R.id.done)
        var repetitions: TextView = view.findViewById(R.id.repetitions)
        var calories: TextView = view.findViewById(R.id.calories)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.workout_list_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val workout = workoutsList[position]
        val exercise = exercises[workout.exerciseID]

        holder.title.text = exercise.name
        holder.calories.text = workout.calories.toString()
        holder.repetitions.text = workout.done.toString()
        if (exercise.duration>0){
            holder.done.text = "Duration: "
        } else if (exercise.repetitions>0){
            holder.done.text = "Repetitions: "
        }
    }

    override fun getItemCount(): Int {
        return workoutsList.size
    }
}