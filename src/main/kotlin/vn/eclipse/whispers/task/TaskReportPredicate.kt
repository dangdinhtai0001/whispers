package vn.eclipse.whispers.task

/**
 * A predicate interface on work report.
 *
 */
fun interface TaskReportPredicate {
    /**
     * Apply the predicate on the given work report.
     *
     * @param taskReport on which the predicate should be applied
     * @return true if the predicate applies on the given report, false otherwise
     */
    fun apply(taskReport: TaskReport): Boolean


    companion object {
        val ALWAYS_TRUE: TaskReportPredicate = TaskReportPredicate { true }
        val ALWAYS_FALSE: TaskReportPredicate = TaskReportPredicate { false }
        val COMPLETED: TaskReportPredicate =
            TaskReportPredicate { taskReport: TaskReport -> taskReport.status == TaskStatus.COMPLETED }
        val FAILED: TaskReportPredicate =
            TaskReportPredicate { taskReport: TaskReport -> taskReport.status == TaskStatus.FAILED }
    }
}
