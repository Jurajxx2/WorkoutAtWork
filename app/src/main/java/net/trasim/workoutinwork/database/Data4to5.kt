package net.trasim.workoutinwork.database

import android.app.Activity
import net.trasim.workoutinwork.objects.Exercise
import net.trasim.workoutinwork.objects.Tip
import net.trasim.workoutinwork.objects.Workday
import net.trasim.workoutinwork.objects.Workout

private var exercises: ArrayList<Exercise> = ArrayList(20)
private var tips: ArrayList<Tip> = ArrayList(10)

class Data4to5(activity: Activity) {

    init {
        if (AppDatabase.getInstance(activity).exerciseModel().allExercises.isEmpty()) {

            exercises.add(Exercise("Jumping jacks", "Poskoky s odrazom", "Do jumping jacks!", "Robte poskoky s odrazom!", "Jump from a standing position to a position with legs spread and arms raised and then back to the original position.", "Skočte zo stoja do polohy s roztiahnutými nohami a zdvihnutými rukami a potom späť do pôvodnej polohy.", 20, 0, "jacks", true))
            exercises.add(Exercise("Wall sit", "Sed pri stene", "Do wall sitting!", "Robte sed pri stene!", "Form a right angle (90 degrees) at your hips and your knees, your back is flat against the wall, and your heels are on the ground.", "Vytvorte pravý uhol (90 stupňov) na bokoch a kolenách, chrbát je rovný a opretý o stenu v celej dĺžke a vaše päty sú na zemi.", 0, 60, "wallsit", true))
            exercises.add(Exercise("Push up", "Klik", "Do push ups!", "Klikujte!", "Perform in a prone position by raising and lowering the body with the straightening and bending of the arms while keeping the back straight and supporting the body on the hands and toes.", "Z pozície s vystretým telom a s rukami na zemi ohnite lakte, kým sa váš hrudník nedostane takmer k podložke, tesne nad zemou chvíľu vydržte a potom sa zdvihnite do pôvodnej polohy.", 10, 0, "pushup", true))
            exercises.add(Exercise("Abdominal crunch", "Brušák", "Do abdominal crunches!", "Robte brušáky!", "Perform from a supine position by raising the torso to a sitting position and returning to the original position without using the arms or lifting the feet", "Vykonajte z pozície na chrbte s pokrčenými nohami zdvihnutím trupu do sediacej polohy a návratom do pôvodnej polohy bez použitia ramien alebo zdvíhania chodidiel", 15, 0, "abcrunch", true))
            exercises.add(Exercise("Step-up onto chair", "Výstup na stoličku", "Do step-ups onto chair!", "Robte výstupy na stoličku!", "Take your right foot, step up and continue looking all the straight ahead and then stay back down. Similarly switch legs, step back up and come down.", "Pravou nohou vstúpte na stoličku, pričom sa pozeráte sa priamo pred seba, a potom zídite späť, vymeňte nohy, krok nahor a nadol.", 5, 0, "stepup", true))
            exercises.add(Exercise("Squat", "Drep", "Do squats!", "Drepujte!", "Stand tall with your feet hip distance apart. Your hips, knees, and toes should all be facing forward. Bend your knees and extend your buttocks backward as if you are going to sit back into a chair. Make sure that you keep your knees behind your toes and your weight in your heels. Rise back up and repeat.", "Stoj na šírku ramien, päty sú na zemi, drž chrbát vystretý, drž kolenný kĺb tak, aby smeroval dopredu, pomaly ohni kolená. Panvu tlačíme smerom vzad a nadol. Vráť sa do východiskovej pozície.", 10, 0, "squat", true))
            exercises.add(Exercise("Triceps dip on chair", "Kliky vzadu", "Do triceps dips on chair!", "Robte kliky vzadu!", "Your hands should be shoulder-width apart on the surface you are dipping from, with your arms straight. From there you dip down until your arms are at a 90-degree angle, then straighten them out again, raising your body. Repeat.", "Vaše ruky by mali byť od seba vzdialené na šírku ramien a vystreté na vyvýšenom povrchu(napr. stolička). Z tejto pozície pokrčte ruky, až sú v 90-stupňovom uhle, potom ich narovnajte a zdvihnite svoje telo. Zopakujte.", 10, 0, "triceps", true))
            exercises.add(Exercise("Plank", "Plank", "Do plank!", "Robte plank!", "Balance on the toes and forearms while holding the rest of the body straight.", "Držte váhu tela na predlaktiach a špičkách, pričom držte zvyšok tela rovno.", 0, 20, "plank", true))
            exercises.add(Exercise("High knees running in place", "Beh na mieste s vysokými kolenami", "Do high knees running in place!", "Bežte na mieste s vysokými kolenami!", "Focus on lifting the knees up (at least 90° angle from the ground) and down with power while keeping your back straight.", "Zamerajte sa na zdvíhanie nôh (aspoň do 90 stupňového uhlu v kolenách) a ich pokladanie so silou pri zachovaní rovnej chrbtice.", 0, 20, "running", true))
            exercises.add(Exercise("Lunge", "Výpady", "Do lunges!", "Robte výpady!", "Step forward with one leg, lowering your hips until both knees are bent at about a 90-degree angle. Make sure your front knee is directly above your ankle, not pushed out too far, and make sure your other knee doesn't touch the floor. Keep the weight in your heels as you push back up to the starting position.", "Urobte krok vpred s jednou nohou a znížte boky, kým obe kolená nie sú ohnuté v uhle asi 90 stupňov. Uistite sa, že predné koleno je priamo nad členkom, nie je vytlačené príliš ďaleko a uistite sa, že vaše druhé koleno sa nedotýka podlahy. Udržujte hmotnosť na pätách, odtlačte sa od prednej nohy do pôvodnej polohy.", 10, 0, "lunge", true))
            exercises.add(Exercise("Side plank - right", "Bočný plank - vpravo", "Do right side plank!", "Robte bočný plank vpravo!", "Start on your right side with your feet together and one forearm directly below your shoulder. Contract your core and raise your hips until your body is in a straight line from head to feet. Hold the position without letting your hips drop.", "Začnite na pravom boku so spojenými nohami a pravým predlaktím priamo pod ramenom. Spevnite svoje jadro a zdvihnite boky, kým vaše telo nebude v priamke od hlavy až po nohy. Držte pozíciu bez toho, aby ste nechali poklesnúť boky.", 0, 15, "rightplank", true))
            exercises.add(Exercise("Side plank - left", "Bočný plank - vľavo", "Do left side plank!", "Robte bočný plank vľavo!", "Start on your left side with your feet together and one forearm directly below your shoulder. Contract your core and raise your hips until your body is in a straight line from head to feet. Hold the position without letting your hips drop.", "Začnite na ľavom boku so spojenými nohami a pravým predlaktím priamo pod ramenom. Spevnite svoje jadro a zdvihnite boky, kým vaše telo nebude v priamke od hlavy až po nohy. Držte pozíciu bez toho, aby ste nechali poklesnúť boky.", 0, 15, "leftplank", true))
            for (exercise in exercises) {
                AppDatabase.getInstance(activity).exerciseModel().insertExercise(exercise = exercise)
            }
        }

        if (AppDatabase.getInstance(activity).tipModel().allTips.isEmpty()) {
            tips.add(Tip("Drink enough water", "Pi veľa vody", "Studies show that even mild dehydration (1-3% of body weight) can impair many aspects of brain function.", "Rôzne štúdie dokázali, že aj mierna dehydratácia (1-3% telesnej hmotnosti) môže oslabiť mnohé mozgové funkcie."))
            tips.add(Tip("Don’t skip breakfast", "Nezabudni na raňajky", "Studies show that eating a proper breakfast is one of the most positive things you can do if you are trying to lose weight.", "Rôzne štúdie dokázali, že konzumácia zdravých raňajok je jednou z najpozitívnejších vecí, ktoré môžete pre seba urobiť, ak sa snažíte schudnúť."))
            tips.add(Tip("Eat your stress away", "Jedlom proti stresu", "Prevent low blood sugar as it stresses you out. Eat regular and small healthy meals and keep fruit and veggies handy. Herbal teas will also soothe your frazzled nerves.", "Zabráňte nízkej hladine cukru v krvi, keďže môže spôsobovať pocity stresu. Jedzte pravidelne malé a zdravé jedlá a nezabudnite na ovocie a zeleninu. Bylinné čaje dokážu tiež upokojiť pocity nervozity."))
            tips.add(Tip("Load up on vitamin C", "Nezabudni na vitamín C", "We need at least 90 mg of vitamin C per day and the best way to get this is by eating at least five servings of fresh fruit and vegetables every day. So hit the oranges and guavas!", "Naše telo potrebuje aspoň 90 mg vitamínu C denne a najlepší spôsob, ako to dosiahnuť, je jesť aspoň päť porcií čerstvého ovocia a zeleniny každý deň. Tak choďte na pomaranče a guavy!"))
            for (tip in tips) {
                AppDatabase.getInstance(activity).tipModel().insertTip(tip)
            }
        }
    }
}