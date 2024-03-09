package vn.eclipse.whispers.workflow

import vn.eclipse.whispers.task.*
import java.util.*


/**
 * A repeat flow executes a work repeatedly until its report satisfies a given predicate.
 *
 */
class RepeatFlow internal constructor(
    name: String?,
    private val task: Task,
    private val predicate: TaskReportPredicate
) :
    AbstractWorkFlow(name!!) {
    /**
     * {@inheritDoc}
     */
    override fun execute(taskContext: TaskContext): TaskReport {
        val processedTaskKey = TaskContext.DefaultKey.PROCESSED_TASKS.name
        var taskReport: TaskReport?

        // khởi tạo giá trị về processed task trong task context
        if (taskContext.get(processedTaskKey) == null) {
            taskContext.put(key = processedTaskKey, value = mutableListOf<String>(), force = true)
        }

        val processedTasks = taskContext.get(processedTaskKey) as MutableList<String>

        do {
            processedTasks.add(task.name)

            taskReport = task.execute(taskContext)
        } while (predicate.apply(taskReport!!))
        return taskReport
    }

    object Builder {
        fun aNewRepeatFlow(): NameStep {
            return BuildSteps()
        }

        interface NameStep : RepeatStep {
            fun named(name: String): RepeatStep
        }

        interface RepeatStep {
            fun repeat(task: Task): UntilStep
        }

        interface UntilStep {
            fun until(predicate: TaskReportPredicate): BuildStep
            fun times(times: Int): BuildStep
        }

        interface BuildStep {
            fun build(): RepeatFlow
        }

        private class BuildSteps : NameStep, RepeatStep,
            UntilStep, BuildStep {
            private var name: String
            private var task: Task
            private var predicate: TaskReportPredicate

            init {
                this.name = UUID.randomUUID().toString()
                this.task = NoOpTask()
                this.predicate = TaskReportPredicate.ALWAYS_FALSE
            }

            override fun named(name: String): RepeatStep {
                this.name = name
                return this
            }

            override fun repeat(task: Task): UntilStep {
                this.task = task
                return this
            }

            override fun until(predicate: TaskReportPredicate): BuildStep {
                this.predicate = predicate
                return this
            }

            override fun times(times: Int): BuildStep {
                until(TimesPredicate.times(times))
                return this
            }

            override fun build(): RepeatFlow {
                return RepeatFlow(name, task, predicate)
            }
        }
    }
}