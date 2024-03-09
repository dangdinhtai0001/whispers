package vn.eclipse.whispers.engine

import org.junit.jupiter.api.Test
import vn.eclipse.whispers.task.TaskContext
import vn.eclipse.whispers.task.TaskReportPredicate
import vn.eclipse.whispers.workflow.ConditionalFlow
import vn.eclipse.whispers.workflow.ParallelFlow
import vn.eclipse.whispers.workflow.RepeatFlow
import vn.eclipse.whispers.workflow.SequentialFlow
import java.util.concurrent.Executors

class InterruptibleWorkflowEngineTest {

    @Test
    fun testSequentialFlow() {
        val workFlowEngine = WorkFlowEngineBuilder.aNewWorkFlowEngine().type(WorkFlowEngineType.INTERRUPTIBLE)
            .build()

        val task1 = PrintMessageTask("task1")
        val task2 = PrintMessageTask("task2")

        val sequentialFlow = SequentialFlow.Builder.aNewSequentialFlow()
            .named("sequentialFlow")
            .execute(task1)
            .then(task2)
            .build()

        val report = workFlowEngine.run(sequentialFlow, TaskContext())

        println(report)
    }

    @Test
    fun testConditionalFlow() {
        val workFlowEngine = WorkFlowEngineBuilder.aNewWorkFlowEngine().type(WorkFlowEngineType.INTERRUPTIBLE)
            .build()

        val conditionalTask = PrintMessageTask("conditional")
        val task1 = PrintMessageTask("task1")
        val task2 = PrintMessageTask("task2")

        val conditionalFlow = ConditionalFlow.Builder.aNewConditionalFlow()
            .named("conditionalFlow")
            .execute(conditionalTask)
            .`when`(TaskReportPredicate.COMPLETED)
            .then(task1)
            .otherwise(task2)
            .build()

        val report = workFlowEngine.run(conditionalFlow, TaskContext())

        println(report)
    }

    @Test
    fun testRepeatFlow() {
        val workFlowEngine = WorkFlowEngineBuilder.aNewWorkFlowEngine().type(WorkFlowEngineType.INTERRUPTIBLE)
            .build()

        val task1 = PrintMessageTask("task1")

        val repeatFlow = RepeatFlow.Builder.aNewRepeatFlow()
            .named("repeatFlow")
            .repeat(task1)
            .times(3)
            .build()

        val report = workFlowEngine.run(repeatFlow, TaskContext())

        println(report)
    }

    @Test
    fun testParallelFlow() {
        val workFlowEngine = WorkFlowEngineBuilder.aNewWorkFlowEngine().type(WorkFlowEngineType.INTERRUPTIBLE)
            .build()

        val executorService = Executors.newFixedThreadPool(3)

        val task1 = PrintMessageTask("task1")
        val task2 = PrintMessageTask("task2")
        val task3 = PrintMessageTask("task3")

        val parallelFlow = ParallelFlow.Builder.aNewParallelFlow()
            .named("parallelFlow")
            .execute(task1, task2, task3)
            .with(executorService)
            .build()

        val report = workFlowEngine.run(parallelFlow, TaskContext())

        println(report)
    }
}