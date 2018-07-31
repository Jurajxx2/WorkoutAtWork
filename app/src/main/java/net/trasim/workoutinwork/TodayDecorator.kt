package net.trasim.workoutinwork

import android.graphics.Typeface
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class OneDayDecorator : DayViewDecorator {

    private var date: CalendarDay? = null

    init {
        date = CalendarDay.today()
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return date != null && day == date
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(StyleSpan(Typeface.BOLD))
        view.addSpan(RelativeSizeSpan(1.4f))
    }

    /**
     * We're changing the internals, so make sure to call [MaterialCalendarView.invalidateDecorators]
     */
    fun setDate(date: Date) {
        this.date = CalendarDay.from(date)
    }
}