package hu.bme.aut.android.taskhw.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Insert
    fun insertTask(task: RoomTask)

    @Query("SELECT * FROM task")
    fun getAllTasks(): LiveData<List<RoomTask>>

    @Query("SELECT * FROM task WHERE id == :id")
    fun getTaskById(id: Long?): RoomTask?

    @Query("DELETE FROM task ")
    fun deleteAll(): Unit

    @Update
    fun updateTask(task: RoomTask): Int

    @Delete
    fun deleteTask(task: RoomTask)

}