package vn.eclipse.whispers.task

/**
 * Execution report of a unit of work.
 *
 */
interface TaskReport {
    /**
     * Get work execution status.
     *
     * @return execution status
     */
    val status: TaskStatus?

    /**
     * Retrieves the error information, if any, related to the task execution.
     * This property might return null, but it's typically not null when the
     * task's status is [TaskStatus.FAILED]. The encapsulated error includes
     * details such as the error message, error code, and the original
     * exception, which might contain useful information like exit codes
     * for handling flow control.
     *
     * @return the error information encapsulated in a [TaskError] object.
     */
    val error: TaskError?

    /**
     * Get the last work context of the flow
     *
     * @return last work context of the flow
     */
    val taskContext: TaskContext?
}