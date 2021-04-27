package hu.bme.aut.android.taskhw.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 1,
    exportSchema = false,
    entities = [RoomTask::class]
)
@TypeConverters(
    TaskTypeConverter::class
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

}