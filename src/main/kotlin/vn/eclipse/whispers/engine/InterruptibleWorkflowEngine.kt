package vn.eclipse.whispers.engine

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vn.eclipse.whispers.task.DefaultTaskReport
import vn.eclipse.whispers.task.TaskContext
import vn.eclipse.whispers.task.TaskReport
import vn.eclipse.whispers.task.TaskStatus
import vn.eclipse.whispers.workflow.WorkFlow

class InterruptibleWorkflowEngine : WorkFlowEngine {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(InterruptibleWorkflowEngine::class.java)
    }

    override fun run(workFlow: WorkFlow, taskContext: TaskContext): TaskReport? {
        log.info("Running workflow ''{}''", workFlow.name)

        return try {
            workFlow.execute(taskContext)
        } catch (e: Exception) {
            DefaultTaskReport(
                taskContext = taskContext,
                status = TaskStatus.FAILED,
                error = e,
                taskName = workFlow.name
            )
        }
    }
}