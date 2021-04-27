package hu.bme.aut.android.taskhw.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import hu.bme.aut.android.taskhw.database.RoomTask
import hu.bme.aut.android.taskhw.database.TaskDao
import hu.bme.aut.android.taskhw.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val taskDao: TaskDao) {

    fun getAllTasks(): LiveData<List<Task>> {
        return taskDao.getAllTasks()
            .map { roomTasks ->
                roomTasks.map { roomTask ->
                    roomTask.toDomainModel()
                }
            }
    }

    suspend fun insert(task: Task) = withContext(Dispatchers.IO) {
        taskDao.insertTask(task.toRoomModel())
    }

    suspend fun delete(task: Task) = withContext(Dispatchers.IO) {
        val roomTask = taskDao.getTaskById(task.id) ?: return@withContext
        taskDao.deleteTask(roomTask)
    }
    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        taskDao.deleteAll()
    }

    private fun RoomTask.toDomainModel(): Task {
        return Task(
            id = id,
            title = title,
            category = category,
            priority = priority,
            description = description,
            dueDate = dueDate,
            always = always,
        )
    }

    private fun Task.toRoomModel(): RoomTask {
        return RoomTask(
            title = title,
            category = category,
            priority = priority,
            description = description,
            dueDate = dueDate,
            always = always,
        )
    }
}