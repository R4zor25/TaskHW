package hu.bme.aut.android.taskhw.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.taskhw.TaskApplication
import hu.bme.aut.android.taskhw.adapter.SimpleItemRecyclerViewAdapter
import hu.bme.aut.android.taskhw.model.Task
import hu.bme.aut.android.taskhw.repository.Repository
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val repository: Repository

    val allTasks: LiveData<List<Task>>

    init {
        val taskDao = TaskApplication.taskDatabase.taskDao()
        repository = Repository(taskDao)
        allTasks = repository.getAllTasks()
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
        if(SimpleItemRecyclerViewAdapter.filter)
            SimpleItemRecyclerViewAdapter.backup.add(task)
    }
    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
    fun deleteAll() = viewModelScope.launch{
        repository.deleteAll()
    }

}