package vn.eclipse.whispers.workflow

import vn.eclipse.whispers.task.Task
import vn.eclipse.whispers.task.TaskContext
import vn.eclipse.whispers.task.TaskReport
import java.util.*
import java.util.concurrent.ExecutorService


/**
 * A parallel flow executes a set of work units in parallel. A [ParallelFlow]
 * requires a [ExecutorService] to execute work units in parallel using multiple
 * threads.
 *
 * **It is the responsibility of the caller to manage the lifecycle of the
 * executor service.**
 *
 * The status of a parallel flow execution is defined as:
 *
 *
 *  * [vn.eclipse.whispers.task.TaskStatus.COMPLETED]: If all work units have successfully completed
 *  * [vn.eclipse.whispers.task.TaskStatus.FAILED]: If one of the work units has failed
 *
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
class ParallelFlow internal constructor(
    name: String?,
    taskUnits: List<Task>?,
    parallelFlowExecutor: ParallelFlowExecutor
) :
    AbstractWorkFlow(name!!) {
    private val taskUnits: MutableList<Task> = ArrayList()
    private val workExecutor: ParallelFlowExecutor

    init {
        this.taskUnits.addAll(taskUnits!!)
        this.workExecutor = parallelFlowExecutor
    }

    /**
     * {@inheritDoc}
     */
    override fun execute(taskContext: TaskContext): ParallelFlowReport {
        val workFlowReport = ParallelFlowReport()
        val taskReports: List<TaskReport> = workExecutor.executeInParallel(taskUnits, taskContext)
        workFlowReport.addAll(taskReports)
        return workFlowReport
    }

    object Builder {
        fun aNewParallelFlow(): NameStep {
            return BuildSteps()
        }

        interface NameStep : ExecuteStep {
            fun named(name: String): ExecuteStep
        }

        interface ExecuteStep {
            fun execute(vararg taskUnits: Task): WithStep
        }

        interface WithStep {
            /**
             * A [ParallelFlow] requires an [ExecutorService] to
             * execute work units in parallel using multiple threads.
             *
             * **It is the responsibility of the caller to manage the lifecycle
             * of the executor service.**
             *
             * @param executorService to use to execute work units in parallel
             * @return the builder instance
             */
            fun with(executorService: ExecutorService?): BuildStep
        }

        interface BuildStep {
            fun build(): ParallelFlow
        }

        private class BuildSteps : NameStep, ExecuteStep, WithStep,
            BuildStep {
            private var name: String
            private val tasks: MutableList<Task>
            private var executorService: ExecutorService? = null

            init {
                this.name = UUID.randomUUID().toString()
                this.tasks = ArrayList()
            }

            override fun named(name: String): ExecuteStep {
                this.name = name
                return this
            }

            override fun execute(vararg taskUnits: Task): WithStep {
                tasks.addAll(listOf(*taskUnits))
                return this
            }

            override fun with(executorService: ExecutorService?): BuildStep {
                this.executorService = executorService
                return this
            }

            override fun build(): ParallelFlow {
                return ParallelFlow(
                    this.name, this.tasks,
                    ParallelFlowExecutor(this.executorService!!)
                )
            }
        }
    }
}
