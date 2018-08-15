package net.trasim.workoutinwork.database

import android.app.Activity
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.Workday
import net.trasim.workoutinwork.objects.Workout

var exercises: ArrayList<Exercise> = ArrayList(20)
var initialWorkout: Workout = Workout(0)
var initialWorkday: Workday = Workday("0", 0, 0, 0)

class DatabaseInit {

    constructor(activity: Activity) {
        exercises.add(Exercise("Jumping jacks", "Do jumping jacks!", "", 20, 0, "jacks"))
        exercises.add(Exercise("Wall sit", "Do wall sitting!", "", 0, 30, "wallsit"))
        exercises.add(Exercise("Push up", "Do push ups!", "", 10, 0, "pushup"))
        exercises.add(Exercise("Abdominal crunch", "Do abdominal crunches!", "", 15, 0, "abcrunch"))
        exercises.add(Exercise("Step-up onto chair", "Do step-ups onto chair!", "", 5, 0, "stepup"))
        exercises.add(Exercise("Squat", "Do squats!", "", 10, 0, "squat"))
        exercises.add(Exercise("Triceps dip on chair", "Do triceps dips on chair!", "", 10, 0, "triceps"))
        exercises.add(Exercise("Plank", "Do plank!", "", 0, 20, "plank"))
        exercises.add(Exercise("High knees running in place", "Do high knees running in place!", "", 0, 20, "running"))
        exercises.add(Exercise("Lunge", "Do lunges!", "", 10, 0, "lunge"))
        exercises.add(Exercise("Push-up and rotation", "Do push-up and rotations!", "", 8, 0, "rotation"))
        exercises.add(Exercise("Side plank - right", "Do right side plank!", "", 0, 15, "rightplank"))
        exercises.add(Exercise("Side plank - left", "Do left side plank!", "", 0, 15, "leftplank"))

        for (exercise in exercises) {
            AppDatabase.getInstance(activity).exerciseModel().insertExercise(exercise = exercise)
        }

        AppDatabase.getInstance(activity).workdayModel().insertWorkday(initialWorkday)
        AppDatabase.getInstance(activity).workoutModel().insertWorkout(initialWorkout)
    }
}