package vn.eclipse.whispers.task

import java.util.concurrent.atomic.AtomicInteger

/**
 * A predicate that returns true after a given number of times.
 *
 */
class TimesPredicate(private val times: Int) : TaskReportPredicate {
    private val counter = AtomicInteger()

    override fun apply(taskReport: TaskReport): Boolean {
        return counter.incrementAndGet() != times
    }

    companion object {
        fun times(times: Int): TimesPredicate {
            return TimesPredicate(times)
        }
    }
}