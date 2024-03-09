package vn.eclipse.whispers.task

data class TaskError(
    val task: String?,
    val exception: Throwable?
)