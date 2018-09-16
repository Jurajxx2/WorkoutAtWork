package net.trasim.workoutinwork.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.R
import net.trasim.workoutinwork.database.AppDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ExercisesRecycleAdapter(private val exercisesList: List<Exercise>, private val context: Context) : RecyclerView.Adapter<ExercisesRecycleAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.exerciseName)
        var description: TextView = view.findViewById(R.id.exerciseDescription)
        var img: pl.droidsonroids.gif.GifImageView = view.findViewById(R.id.exerciseGif)
        var enableExercise: Switch = view.findViewById(R.id.switch1)
        var plus: Button = view.findViewById(R.id.plus)
        var minus: Button = view.findViewById(R.id.minus)

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

        holder.enableExercise.isChecked = exercises.isEnabled

        holder.enableExercise.setOnClickListener {
            if (exercises.isEnabled){
                exercises.isEnabled = false
                doAsync {
                    val database = AppDatabase.getInstance(context)
                    database.exerciseModel().updateExercise(exercises)
                }
            } else {
                exercises.isEnabled = true
                doAsync {
                    val database = AppDatabase.getInstance(context)
                    database.exerciseModel().updateExercise(exercises)
                }
            }
        }
        var description = ""

        holder.plus.setOnClickListener {
            if (exercises.duration>0){
                exercises.duration += 1
                description = "\nDo it for " + exercises.duration.toString() + " seconds"
            } else if (exercises.repetitions>0){
                exercises.repetitions += 1
                description = "\nDo it " + exercises.repetitions.toString() + " times"
            }
            doAsync {
                val database = AppDatabase.getInstance(context)
                database.exerciseModel().updateExercise(exercises)
            }
            holder.description.text = exercises.heading + description
        }

        holder.minus.setOnClickListener {
            if (exercises.duration>0){
                exercises.duration -= 1
                description = "\nDo it for " + exercises.duration.toString() + " seconds"
            } else if (exercises.repetitions>0){
                exercises.repetitions -= 1
                description = "\nDo it " + exercises.repetitions.toString() + " times"
            }
            doAsync {
                val database = AppDatabase.getInstance(context)
                database.exerciseModel().updateExercise(exercises)
            }
            holder.description.text = exercises.heading + description
        }

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