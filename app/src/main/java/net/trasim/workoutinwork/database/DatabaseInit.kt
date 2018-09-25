package net.trasim.workoutinwork.database

import android.app.Activity
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.Tip
import net.trasim.workoutinwork.objects.Workday
import net.trasim.workoutinwork.objects.Workout

var exercises: ArrayList<Exercise> = ArrayList(20)
var tips: ArrayList<Tip> = ArrayList(10)
var initialWorkout: Workout = Workout(0)
var initialWorkday: Workday = Workday("0", 0, 0, 0)

class DatabaseInit(activity: Activity) {

    init {
        exercises.add(Exercise("Jumping jacks", "Do jumping jacks!", "Jump from a standing position to a position with legs spread and arms raised and then back to the original position.", 20, 0, "jacks", true))
        exercises.add(Exercise("Wall sit", "Do wall sitting!", "Form a right angle (90 degrees) at your hips and your knees, your back is flat against the wall, and your heels are on the ground.", 0, 60, "wallsit", true))
        exercises.add(Exercise("Push up", "Do push ups!", "Perform in a prone position by raising and lowering the body with the straightening and bending of the arms while keeping the back straight and supporting the body on the hands and toes.", 10, 0, "pushup", true))
        exercises.add(Exercise("Abdominal crunch", "Do abdominal crunches!", "", 15, 0, "abcrunch", true))
        exercises.add(Exercise("Step-up onto chair", "Do step-ups onto chair!", "", 5, 0, "stepup", true))
        exercises.add(Exercise("Squat", "Do squats!", "", 10, 0, "squat", true))
        exercises.add(Exercise("Triceps dip on chair", "Do triceps dips on chair!", "", 10, 0, "triceps", true))
        exercises.add(Exercise("Plank", "Do plank!", "", 0, 20, "plank", true))
        exercises.add(Exercise("High knees running in place", "Do high knees running in place!", "", 0, 20, "running", true))
        exercises.add(Exercise("Lunge", "Do lunges!", "", 10, 0, "lunge", true))
        exercises.add(Exercise("Side plank - right", "Do right side plank!", "", 0, 15, "rightplank", true))
        exercises.add(Exercise("Side plank - left", "Do left side plank!", "", 0, 15, "leftplank", true))
        for (exercise in exercises) {
            AppDatabase.getInstance(activity).exerciseModel().insertExercise(exercise = exercise)
        }

        tips.add(Tip("Drink enough water", "Studies show that even mild dehydration (1-3% of body weight) can impair many aspects of brain function."))
        tips.add(Tip("Don’t skip breakfast", "Studies show that eating a proper breakfast is one of the most positive things you can do if you are trying to lose weight. Breakfast skipp"))
        tips.add(Tip("Eat your stress away", "Prevent low blood sugar as it stresses you out. Eat regular and small healthy meals and keep fruit and veggies handy. Herbal teas will also soothe your frazzled nerves."))
        tips.add(Tip("Load up on vitamin C", "We need at least 90 mg of vitamin C per day and the best way to get this is by eating at least five servings of fresh fruit and vegetables every day. So hit the oranges and guavas!"))
        for (tip in tips) {
            AppDatabase.getInstance(activity).tipModel().insertTip(tip)
        }

        AppDatabase.getInstance(activity).workdayModel().insertWorkday(initialWorkday)
        AppDatabase.getInstance(activity).workoutModel().insertWorkout(initialWorkout)
    }
}