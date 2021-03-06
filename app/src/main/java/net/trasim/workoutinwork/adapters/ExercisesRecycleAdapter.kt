package net.trasim.workoutinwork.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.exercise_list_row.view.*
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.R
import net.trasim.workoutinwork.database.AppDatabase
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class ExercisesRecycleAdapter(private val exercisesList: List<Exercise>, private val context: Context) : RecyclerView.Adapter<ExercisesRecycleAdapter.MyViewHolder>() {

    //ViewHolder with views in layout
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.exerciseName)
        var description: TextView = view.findViewById(R.id.exerciseDescription)
        var img: pl.droidsonroids.gif.GifImageView = view.findViewById(R.id.exerciseGif)
        var enableExercise: Switch = view.findViewById(R.id.switch1)
        var plus: Button = view.findViewById(R.id.plus)
        var minus: Button = view.findViewById(R.id.minus)
        var background: ImageView = view.findViewById(R.id.background)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.exercise_list_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //Get exercise
        val exercises = exercisesList[position]

        //Get language code
        val language = Locale.getDefault().isO3Language

        if (language == "slk") {
            holder.title.text = exercises.nameSK
        } else {
            holder.title.text = exercises.nameEN
        }

        when(exercises.img){
            "pushup" -> holder.img.setImageResource(R.drawable.pushup)
            "jacks" -> holder.img.setImageResource(R.drawable.jacks)
            "leftplank" -> holder.img.setImageResource(R.drawable.leftplank)
            "lunge" -> holder.img.setImageResource(R.drawable.lunge)
            "plank" -> holder.img.setImageResource(R.drawable.plank)
            "rightplank" -> holder.img.setImageResource(R.drawable.rightplank)
            "running" -> holder.img.setImageResource(R.drawable.running)
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
                description = if (language == "slk") {
                    "\nCvičte " + exercises.duration.toString() + "s"
                } else {
                    "\nDo it for " + exercises.duration.toString() + "s"
                }
            } else if (exercises.repetitions>0){
                exercises.repetitions += 1
                description = if (language == "slk") {
                    "\nZopakujte " + exercises.repetitions.toString() + "x"
                } else {
                    "\nDo it " + exercises.repetitions.toString() + "x"
                }
            }
            doAsync {
                val database = AppDatabase.getInstance(context)
                database.exerciseModel().updateExercise(exercises)
            }

            val popis = if (language == "slk"){
                exercises.descriptionSK!! + description
            } else {
                exercises.descriptionEN!! + description
            }

            holder.description.text = popis
        }

        holder.minus.setOnClickListener {
            if (exercises.duration>0){
                if(exercises.duration != 1) {
                    exercises.duration -= 1
                    description = if (language == "slk") {
                        "\nCvičte " + exercises.duration.toString() + "s"
                    } else {
                        "\nDo it for " + exercises.duration.toString() + "s"
                    }
                } else {
                    Toast.makeText(context, "Cannot change duration to 0s", Toast.LENGTH_SHORT).show()
                }
            } else if (exercises.repetitions>0){
                if(exercises.repetitions != 1) {
                    exercises.repetitions -= 1
                    description = if (language == "slk") {
                        "\nZopakujte " + exercises.repetitions.toString() + "x"
                    } else {
                        "\nDo it " + exercises.repetitions.toString() + "x"
                    }
                } else {
                    Toast.makeText(context, "Cannot change repetitions to 0x", Toast.LENGTH_SHORT).show()
                }
            }
            doAsync {
                val database = AppDatabase.getInstance(context)
                database.exerciseModel().updateExercise(exercises)
            }
            val popis: String = if (language == "slk"){
                exercises.descriptionSK!! + description
            } else {
                exercises.descriptionEN!! + description
            }

            holder.description.text = popis
        }

        if (exercises.duration>0){
            description = if (language == "slk") {
                "\nCvičte " + exercises.duration.toString() + "s"
            } else {
                "\nDo it for " + exercises.duration.toString() + "s"
            }
        } else if (exercises.repetitions>0){
            description = if (language == "slk") {
                "\nZopakujte " + exercises.repetitions.toString() + "x"
            } else {
                "\nDo it " + exercises.repetitions.toString() + "x"
            }
        }

        val popis = if (language == "slk"){
            exercises.descriptionSK!! + description
        } else {
            exercises.descriptionEN!! + description
        }

        holder.description.text = popis
    }

    override fun getItemCount(): Int {
        return exercisesList.size
    }
}