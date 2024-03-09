package vn.eclipse.whispers.workflow

import vn.eclipse.whispers.task.Task
import vn.eclipse.whispers.task.TaskContext
import vn.eclipse.whispers.task.TaskReport
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.function.Consumer


class ParallelFlowExecutor(private val workExecutor: ExecutorService) {
    fun executeInParallel(taskUnits: List<Task>, taskContext: TaskContext): List<TaskReport> {
        // prepare tasks for parallel submission
        val tasks: MutableList<Callable<TaskReport?>> = ArrayList(taskUnits.size)
        taskUnits.forEach(Consumer { task: Task ->
            tasks.add(Callable {
                task.execute(
                    taskContext
                )
            })
        })

        // submit work units and wait for results
        val futures: List<Future<TaskReport?>>
        try {
            futures = workExecutor.invokeAll(tasks)
        } catch (e: InterruptedException) {
            throw RuntimeException("The parallel flow was interrupted while executing work units", e)
        }
        val taskToReportFuturesMap: MutableMap<Task, Future<TaskReport?>> = HashMap()
        for (index in taskUnits.indices) {
            taskToReportFuturesMap[taskUnits[index]] = futures[index]
        }

        // gather reports
        val taskReports: MutableList<TaskReport> = ArrayList()
        for ((key, value) in taskToReportFuturesMap) {
            try {
                if(value.get() != null){
                    taskReports.add(value.get()!!)
                }
            } catch (e: InterruptedException) {
                val message = String.format(
                    "The parallel flow was interrupted while waiting for the result of work unit '%s'",
                    key.name
                )
                throw RuntimeException(message, e)
            } catch (e: ExecutionException) {
                val message = String.format("Unable to execute work unit '%s'", key.name)
                throw RuntimeException(message, e)
            }
        }

        return taskReports
    }
}