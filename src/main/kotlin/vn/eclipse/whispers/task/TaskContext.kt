package vn.eclipse.whispers.task

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * Work execution context. This can be used to pass initial parameters to the
 * workflow and share data between work units.
 *
 * **Work context instances are thread-safe.**
 *
 */
open class TaskContext {
    private val context: MutableMap<String, Any?> = ConcurrentHashMap()

    companion object {
        private val log: Logger = LoggerFactory.getLogger(TaskContext::class.java)
    }

    enum class DefaultKey {
        PROCESSED_TASKS,
    }

    fun put(key: String, value: Any?, force: Boolean = false) {
        if (!force && key in DefaultKey.entries.map { it.name }) {
            log.info("Warning: Key '$key' is a default key.")
        } else {
            context[key] = value
        }
    }

    fun get(key: String): Any? {
        return context[key]
    }

    val entrySet: Set<Map.Entry<String, Any?>>
        get() = context.entries

    override fun toString(): String {
        return "context=$context"
    }
}
