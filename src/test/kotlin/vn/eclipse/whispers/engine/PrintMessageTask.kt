package vn.eclipse.whispers.engine

import vn.eclipse.whispers.task.*

class PrintMessageTask(private val message: String) : Task {
    override val name: String
        get() = message

    override fun execute(taskContext: TaskContext): TaskReport? {
        println(message)

        return DefaultTaskReport(TaskStatus.COMPLETED, taskContext)
    }
}