package vn.eclipse.whispers.task

import java.util.*

/**
 * This interface represents a unit of work. Implementations of this interface must:
 *
 *
 *  * catch any checked or unchecked exceptions and return a [TaskReport]
 * instance with a status of [TaskStatus.FAILED] and a reference to the exception
 *  * make sure the work is finished in a finite amount of time
 *
 *
 * Work name must be unique within a workflow definition.
 *
 */
interface Task {
    val name: String
        /**
         * The name of the unit of work. The name must be unique within a workflow definition.
         *
         * @return name of the unit of work.
         */
        get() = UUID.randomUUID().toString()

    /**
     * Execute the unit of work and return its report. Implementations are required
     * to catch any checked or unchecked exceptions and return a [TaskReport] instance
     * with a status of [TaskStatus.FAILED] and a reference to the exception.
     *
     * @param taskContext context in which this unit of work is being executed
     * @return the execution report
     */
    fun execute(taskContext: TaskContext): TaskReport?
}