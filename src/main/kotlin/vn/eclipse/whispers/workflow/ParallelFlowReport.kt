package vn.eclipse.whispers.workflow

import vn.eclipse.whispers.task.TaskContext
import vn.eclipse.whispers.task.TaskError
import vn.eclipse.whispers.task.TaskReport
import vn.eclipse.whispers.task.TaskStatus


/**
 * Aggregate report of the partial reports of work units executed in a parallel flow.
 *
 */
class ParallelFlowReport
/**
 * Create a new [ParallelFlowReport].
 */ @JvmOverloads constructor(private val reports: MutableList<TaskReport> = ArrayList()) :
    TaskReport {
    /**
     * Create a new [ParallelFlowReport].
     *
     * @param reports of works executed in parallel
     */

    /**
     * Get partial reports.
     *
     * @return partial reports
     */
    fun getReports(): List<TaskReport> {
        return reports
    }

    fun add(taskReport: TaskReport) {
        reports.add(taskReport)
    }

    fun addAll(taskReports: List<TaskReport>?) {
        reports.addAll(taskReports!!)
    }

    override val status: TaskStatus
        /**
         * Return the status of the parallel flow.
         *
         * The status of a parallel flow is defined as follows:
         *
         *
         *  * [vn.eclipse.whispers.task.TaskStatus.COMPLETED]: If all work units have successfully completed
         *  * [vn.eclipse.whispers.task.TaskStatus.FAILED]: If one of the work units has failed
         *
         * @return workflow status
         */
        get() {
            for (report in reports) {
                if (report.status == TaskStatus.FAILED) {
                    return TaskStatus.FAILED
                }
            }
            return TaskStatus.COMPLETED
        }

    override val error: TaskError?
        /**
         * Return the first error of partial reports.
         *
         * @return the first error of partial reports.
         */
        get() {
            for (report in reports) {
                val error = report.error
                if (error != null) {
                    return error
                }
            }
            return null
        }

    override val taskContext: TaskContext
        /**
         * The parallel flow context is the union of all partial contexts. In a parallel
         * flow, each work unit should have its own unique keys to avoid key overriding
         * when merging partial contexts.
         *
         * @return the union of all partial contexts
         */
        get() {
            val taskContext = TaskContext()
            for (report in reports) {
                val partialContext = report.taskContext
                for ((key, value) in partialContext!!.entrySet) {
                    taskContext.put(key, value)
                }
            }
            return taskContext
        }
}