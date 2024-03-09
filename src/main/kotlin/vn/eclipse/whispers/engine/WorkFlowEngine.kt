package vn.eclipse.whispers.engine

import vn.eclipse.whispers.task.TaskContext
import vn.eclipse.whispers.task.TaskReport
import vn.eclipse.whispers.workflow.WorkFlow


/**
 * Interface for a workflow engine.
 */
interface WorkFlowEngine {
    /**
     * Run the given workflow and return its report.
     *
     * @param workFlow to run
     * @param taskContext context in which the workflow will be run
     * @return workflow report
     */
    fun run(workFlow: WorkFlow, taskContext: TaskContext): TaskReport?
}