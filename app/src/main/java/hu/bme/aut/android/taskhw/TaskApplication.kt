package hu.bme.aut.android.taskhw

import android.app.Application
import androidx.room.Room
import hu.bme.aut.android.taskhw.database.TaskDatabase


class TaskApplication : Application() {

	companion object {
		lateinit var taskDatabase: TaskDatabase
			private set
	}
	
	override fun onCreate() {
		super.onCreate()
		taskDatabase = Room.databaseBuilder(
				applicationContext,
				TaskDatabase::class.java,
				"task_database"
		).build()

	}
}