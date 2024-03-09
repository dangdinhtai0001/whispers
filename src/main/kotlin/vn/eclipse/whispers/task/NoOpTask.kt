package vn.eclipse.whispers.task

import java.util.*

/**
 * No operation task.
 *
 */
class NoOpTask : Task {
    override val name: String
        get() = UUID.randomUUID().toString()

    override fun execute(taskContext: TaskContext): TaskReport {
        return DefaultTaskReport(TaskStatus.COMPLETED, taskContext)
    }
}