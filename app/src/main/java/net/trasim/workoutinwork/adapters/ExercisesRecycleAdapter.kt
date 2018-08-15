package net.trasim.workoutinwork.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.R

class ExercisesRecycleAdapter(private val exercisesList: List<Exercise>) : RecyclerView.Adapter<ExercisesRecycleAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.exerciseName)
        var description: TextView = view.findViewById(R.id.exerciseDescription)
        var img: pl.droidsonroids.gif.GifImageView = view.findViewById(R.id.exerciseGif)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.exercise_list_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val exercises = exercisesList[position]
        holder.title.text = exercises.name

        when(exercises.img){
            "pushup" -> holder.img.setImageResource(R.drawable.pushup)
            "jacks" -> holder.img.setImageResource(R.drawable.jacks)
            "leftplank" -> holder.img.setImageResource(R.drawable.leftplank)
            "lunge" -> holder.img.setImageResource(R.drawable.lunge)
            "plank" -> holder.img.setImageResource(R.drawable.plank)
            "rightplank" -> holder.img.setImageResource(R.drawable.rightplank)
            "rotation" -> holder.img.setImageResource(R.drawable.rotation)
            "running" -> holder.img.setImageResource(R.drawable.runnung)
            "squat" -> holder.img.setImageResource(R.drawable.squat)
            "stepup" -> holder.img.setImageResource(R.drawable.stepup)
            "triceps" -> holder.img.setImageResource(R.drawable.triceps)
            "wallsit" -> holder.img.setImageResource(R.drawable.wallsit)
            "abcrunch" -> holder.img.setImageResource(R.drawable.abcrunch)
            else -> holder.img.setImageResource(R.drawable.coffee)
        }

        var description = ""
        if (exercises.duration>0){
            description = "\nDo it for " + exercises.duration.toString() + " seconds"
        } else if (exercises.repetitions>0){
            description = "\nDo it " + exercises.repetitions.toString() + " times"
        }
        holder.description.text = exercises.heading + description
    }

    override fun getItemCount(): Int {
        return exercisesList.size
    }
}