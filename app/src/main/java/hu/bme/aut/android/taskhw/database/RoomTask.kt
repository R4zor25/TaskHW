package hu.bme.aut.android.taskhw.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import hu.bme.aut.android.taskhw.model.Task

@Entity(tableName = "task")
data class RoomTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val priority: Task.Priority,
    val dueDate: String,
    val description: String,
    val always: Boolean,
)

class TaskTypeConverter {

    companion object {
        const val LOW = "LOW"
        const val MEDIUM = "MEDIUM"
        const val HIGH = "HIGH"
    }

    @TypeConverter
    fun toPriority(value: String?): Task.Priority {
        return when (value) {
            LOW -> Task.Priority.LOW
            MEDIUM -> Task.Priority.MEDIUM
            HIGH -> Task.Priority.HIGH
            else -> Task.Priority.LOW
        }
    }

    @TypeConverter
    fun toString(enumValue: Task.Priority): String? {
        return enumValue.name
    }
}
