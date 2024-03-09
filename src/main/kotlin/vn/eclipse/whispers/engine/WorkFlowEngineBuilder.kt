package vn.eclipse.whispers.engine


/**
 * Main entry point to create [WorkFlowEngine] instances.
 *
 * This class allows the construction of various types of [WorkFlowEngine] instances.
 * It provides a fluent API for configuring and creating instances of [WorkFlowEngine].
 */
class WorkFlowEngineBuilder private constructor() {
    private var type: WorkFlowEngineType = WorkFlowEngineType.DEFAULT

    /**
     * Builds and returns a new [WorkFlowEngine] instance.
     *
     * @return a new [WorkFlowEngine] instance.
     */
    fun build(): WorkFlowEngine {
        return when (type) {
            WorkFlowEngineType.DEFAULT -> DefaultWorkFlowEngine()
            WorkFlowEngineType.INTERRUPTIBLE -> InterruptibleWorkflowEngine()
        }
    }

    /**
     * Sets the type of the [WorkFlowEngine] to be built.
     *
     * @param type the type of the [WorkFlowEngine].
     * @return a reference to this [WorkFlowEngineBuilder] for method chaining.
     */
    fun type(type: WorkFlowEngineType): WorkFlowEngineBuilder {
        this.type = type
        return this
    }

    companion object {
        /**
         * Create a new [WorkFlowEngineBuilder].
         *
         * @return a new [WorkFlowEngineBuilder].
         */
        fun aNewWorkFlowEngine(): WorkFlowEngineBuilder {
            return WorkFlowEngineBuilder()
        }
    }
}