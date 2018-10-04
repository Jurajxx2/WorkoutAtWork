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
        exercises.add(Exercise("Abdominal crunch", "Do abdominal crunches!", "Perform from a supine position by raising the torso to a sitting position and returning to the original position without using the arms or lifting the feet", 15, 0, "abcrunch", true))
        exercises.add(Exercise("Step-up onto chair", "Do step-ups onto chair!", "Take your right foot, step up and continue looking all the straight ahead and then stay back down. Similarly switch legs, step back up and come down.", 5, 0, "stepup", true))
        exercises.add(Exercise("Squat", "Do squats!", "Stand tall with your feet hip distance apart. Your hips, knees, and toes should all be facing forward. Bend your knees and extend your buttocks backward as if you are going to sit back into a chair. Make sure that you keep your knees behind your toes and your weight in your heels. Rise back up and repeat.", 10, 0, "squat", true))
        exercises.add(Exercise("Triceps dip on chair", "Do triceps dips on chair!", "Your hands should be shoulder-width apart on the surface you are dipping from, with your arms straight. From there you dip down until your arms are at a 90-degree angle, then straighten them out again, raising your body. Repeat.", 10, 0, "triceps", true))
        exercises.add(Exercise("Plank", "Do plank!", "Balance on the toes and forearms while holding the rest of the body straight.", 0, 20, "plank", true))
        exercises.add(Exercise("High knees running in place", "Do high knees running in place!", "Focus on lifting the knees up (at least 90° angle from the ground) and down with power while keeping your back straight.", 0, 20, "running", true))
        exercises.add(Exercise("Lunge", "Do lunges!", "Step forward with one leg, lowering your hips until both knees are bent at about a 90-degree angle. Make sure your front knee is directly above your ankle, not pushed out too far, and make sure your other knee doesn't touch the floor. Keep the weight in your heels as you push back up to the starting position.", 10, 0, "lunge", true))
        exercises.add(Exercise("Side plank - right", "Do right side plank!", "Start on your right side with your feet together and one forearm directly below your shoulder. Contract your core and raise your hips until your body is in a straight line from head to feet. Hold the position without letting your hips drop for the allotted time for each set, then repeat on the other side.", 0, 15, "rightplank", true))
        exercises.add(Exercise("Side plank - left", "Do left side plank!", "Start on your left side with your feet together and one forearm directly below your shoulder. Contract your core and raise your hips until your body is in a straight line from head to feet. Hold the position without letting your hips drop for the allotted time for each set, then repeat on the other side.", 0, 15, "leftplank", true))
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