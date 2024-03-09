package vn.eclipse.whispers.task

/**
 * Default implementation of [TaskReport].
 *
 */
class DefaultTaskReport(
    override val status: TaskStatus,
    override val taskContext: TaskContext?,
) : TaskReport {
    override var error: TaskError? = null
        private set

    /**
     * Create a new [DefaultTaskReport].
     *
     * @param status of work
     * @param error if any
     */
    constructor(
        status: TaskStatus,
        taskContext: TaskContext,
        error: Throwable?,
        taskName: String? = null
    ) : this(status, taskContext) {
        this.error = TaskError(task = taskName, exception = error)
    }


    override fun toString(): String {
        return "DefaultWorkReport {" +
                "status=" + status +
                ", context=" + taskContext +
                ", error=" + (if (error == null) "''" else error) +
                '}'
    }
}
