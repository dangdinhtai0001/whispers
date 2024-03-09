package vn.eclipse.whispers.engine

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vn.eclipse.whispers.task.TaskContext
import vn.eclipse.whispers.task.TaskReport
import vn.eclipse.whispers.workflow.WorkFlow

class DefaultWorkFlowEngine : WorkFlowEngine {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(DefaultWorkFlowEngine::class.java)
    }

    override fun run(workFlow: WorkFlow, taskContext: TaskContext): TaskReport? {
        log.info("Running workflow ''{}''", workFlow.name)
        return workFlow.execute(taskContext)
    }
}