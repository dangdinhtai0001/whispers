package vn.eclipse.whispers.workflow

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vn.eclipse.whispers.task.Task
import vn.eclipse.whispers.task.TaskContext
import vn.eclipse.whispers.task.TaskReport
import vn.eclipse.whispers.task.TaskStatus
import java.util.*

/**
 * A sequential flow executes a set of work units in sequence.
 *
 * If a unit of work fails, next work units in the pipeline will be skipped.
 *
 */
class SequentialFlow internal constructor(
    name: String, taskUnits: List<Task>?
) : AbstractWorkFlow(name) {
    private val taskUnits: MutableList<Task> = ArrayList()

    init {
        this.taskUnits.addAll(taskUnits!!)
    }

    /**
     * {@inheritDoc}
     */
    override fun execute(taskContext: TaskContext): TaskReport? {
        val processedTaskKey = TaskContext.DefaultKey.PROCESSED_TASKS.name
        var taskReport: TaskReport? = null

        // khởi tạo giá trị về processed task trong task context
        if (taskContext.get(processedTaskKey) == null) {
            taskContext.put(key = processedTaskKey, value = mutableListOf<String>(), force = true)
        }

        val processedTasks = taskContext.get(processedTaskKey) as MutableList<String>

        for (task in taskUnits) {
            processedTasks.add(task.name)

            // Execute task
            taskReport = task.execute(taskContext)

            if (taskReport != null && TaskStatus.FAILED == taskReport.status) {
                log.info("Work unit ''{}'' has failed, skipping subsequent work units", task.name)
                break
            }
        }
        return taskReport
    }

    object Builder {
        fun aNewSequentialFlow(): NameStep {
            return BuildSteps()
        }

        interface NameStep : ExecuteStep {
            fun named(name: String): ExecuteStep
        }

        interface ExecuteStep {
            fun execute(initialTask: Task): ThenStep
            fun execute(initialTaskUnits: List<Task>): ThenStep
        }

        interface ThenStep {
            fun then(nextTask: Task): ThenStep
            fun then(nextTaskUnits: List<Task>): ThenStep
            fun build(): SequentialFlow
        }

        private class BuildSteps : NameStep, ExecuteStep, ThenStep {
            private var name: String
            private val tasks: MutableList<Task>

            init {
                this.name = UUID.randomUUID().toString()
                this.tasks = ArrayList()
            }

            override fun named(name: String): ExecuteStep {
                this.name = name
                return this
            }

            override fun execute(initialTask: Task): ThenStep {
                tasks.add(initialTask)
                return this
            }

            override fun execute(initialTaskUnits: List<Task>): ThenStep {
                tasks.addAll(initialTaskUnits)
                return this
            }

            override fun then(nextTask: Task): ThenStep {
                tasks.add(nextTask)
                return this
            }

            override fun then(nextTaskUnits: List<Task>): ThenStep {
                tasks.addAll(nextTaskUnits)
                return this
            }

            override fun build(): SequentialFlow {
                return SequentialFlow(this.name, this.tasks)
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SequentialFlow::class.java)
    }
}
