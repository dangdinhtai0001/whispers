package vn.eclipse.whispers.workflow

import vn.eclipse.whispers.task.Task


/**
 * Interface to define a flow of work units. A workflow is also a work, this is
 * what makes workflows composable.
 *
 */
interface WorkFlow : Task
