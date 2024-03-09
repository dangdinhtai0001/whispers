package vn.eclipse.whispers.workflow

import vn.eclipse.whispers.task.*
import java.util.*

/**
 * A conditional flow is defined by 4 artifacts:
 *
 *
 *  * The work to execute first
 *  * A predicate for the conditional logic
 *  * The work to execute if the predicate is satisfied
 *  * The work to execute if the predicate is not satisfied (optional)
 *
 *
 * @see ConditionalFlow.Builder
 *
 *
 */
class ConditionalFlow internal constructor(
    name: String,
    private val initialTaskUnit: Task,
    private val nextOnPredicateSuccess: Task,
    private val nextOnPredicateFailure: Task?,
    private val predicate: TaskReportPredicate
) :
    AbstractWorkFlow(name) {
    /**
     * {@inheritDoc}
     */
    override fun execute(taskContext: TaskContext): TaskReport? {
        val processedTaskKey = TaskContext.DefaultKey.PROCESSED_TASKS.name

        // khởi tạo giá trị về processed task trong task context
        if (taskContext.get(processedTaskKey) == null) {
            taskContext.put(key = processedTaskKey, value = mutableListOf<String>(), force = true)
        }

        val processedTasks = taskContext.get(processedTaskKey) as MutableList<String>

        var jobReport = initialTaskUnit.execute(taskContext)
        processedTasks.add(initialTaskUnit.name)

        if (predicate.apply(jobReport!!)) {
            processedTasks.add(nextOnPredicateSuccess.name)

            jobReport = nextOnPredicateSuccess.execute(taskContext)
        } else {
            if (nextOnPredicateFailure != null && nextOnPredicateFailure !is NoOpTask) { // else is optional
                processedTasks.add(nextOnPredicateFailure.name)

                jobReport = nextOnPredicateFailure.execute(taskContext)
            }
        }

        return jobReport
    }

    object Builder {
        fun aNewConditionalFlow(): NameStep {
            return BuildSteps()
        }

        interface NameStep : ExecuteStep {
            fun named(name: String): ExecuteStep
        }

        interface ExecuteStep {
            fun execute(initialTaskUnit: Task): WhenStep
        }

        interface WhenStep {
            fun `when`(predicate: TaskReportPredicate): ThenStep
        }

        interface ThenStep {
            fun then(task: Task): OtherwiseStep
        }

        interface OtherwiseStep : BuildStep {
            fun otherwise(task: Task): BuildStep
        }

        interface BuildStep {
            fun build(): ConditionalFlow
        }

        private class BuildSteps : NameStep, ExecuteStep, WhenStep,
            ThenStep, OtherwiseStep, BuildStep {
            private var name: String
            private var initialTaskUnit: Task
            private var nextOnPredicateSuccess: Task
            private var nextOnPredicateFailure: Task
            private var predicate: TaskReportPredicate

            init {
                this.name = UUID.randomUUID().toString()
                this.initialTaskUnit = NoOpTask()
                this.nextOnPredicateSuccess = NoOpTask()
                this.nextOnPredicateFailure = NoOpTask()
                this.predicate = TaskReportPredicate.ALWAYS_FALSE
            }

            override fun named(name: String): ExecuteStep {
                this.name = name
                return this
            }

            override fun execute(initialTaskUnit: Task): WhenStep {
                this.initialTaskUnit = initialTaskUnit
                return this
            }

            override fun `when`(predicate: TaskReportPredicate): ThenStep {
                this.predicate = predicate
                return this
            }

            override fun then(task: Task): OtherwiseStep {
                this.nextOnPredicateSuccess = task
                return this
            }

            override fun otherwise(task: Task): BuildStep {
                this.nextOnPredicateFailure = task
                return this
            }

            override fun build(): ConditionalFlow {
                return ConditionalFlow(
                    this.name, this.initialTaskUnit,
                    this.nextOnPredicateSuccess, this.nextOnPredicateFailure,
                    this.predicate
                )
            }
        }
    }
}