package hu.bme.aut.android.taskhw.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.taskhw.R
import hu.bme.aut.android.taskhw.model.Task
import hu.bme.aut.android.taskhw.model.WeekGraph
import kotlinx.android.synthetic.main.row_task.view.*
import kotlinx.android.synthetic.main.row_todo.view.ivPriority
import kotlinx.android.synthetic.main.row_todo.view.tvDueDate
import kotlinx.android.synthetic.main.row_todo.view.tvTitle
import java.time.LocalDate

class SimpleItemRecyclerViewAdapter : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

    private val taskList = mutableListOf<Task>()

    var itemClickListener: TaskItemClickListener? = null

    companion object {
        var filter = false

        var backup = mutableListOf<Task>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]

        holder.task = task

        holder.tvTitle.text = task.title
        holder.tvCategory.text = task.category
        holder.tvDueDate.text = task.dueDate
        if(!task.always)
        holder.tvWeekly.text = "Once"
        else{
            holder.tvWeekly.text = "Weekly Task"
        }

        val resource = when (task.priority) {
            Task.Priority.LOW -> R.drawable.ic_low_priority
            Task.Priority.MEDIUM -> R.drawable.ic_medium_priority
            Task.Priority.HIGH -> R.drawable.ic_high_priority
        }
        holder.ivPriority.setImageResource(resource)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAll(tasks: List<Task>) {
        if(backup.isEmpty())
            backup.addAll(tasks)
        taskList.clear()
        if (filter) {
            val s = LocalDate.now().toString().replace("-",".",false)
            for (t in backup) {
                if(s < t.dueDate)
                    taskList.add(t)
            }
            taskList.sortBy { it.dueDate }
            notifyDataSetChanged()
        } else {
            taskList.addAll(backup)
            backup.clear()
            taskList.sortBy { it.dueDate }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = taskList.size

    fun deleteTask(l: Long?) {
        var r = taskList.get(0)
        for(t in taskList){
            if(t.id == l)
                r = t
        }
        taskList.remove(r)
        backup.remove(r)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDueDate: TextView = itemView.tvDueDate
        val tvTitle: TextView = itemView.tvTitle
        val tvCategory: TextView = itemView.tvCategory
        val ivPriority: ImageView = itemView.ivPriority
        val tvWeekly: TextView = itemView.tvWeekly
        var task: Task? = null

        init {
            itemView.setOnClickListener {
                task?.let { task -> itemClickListener?.onItemClick(task) }
            }
            itemView.setOnLongClickListener { view ->
                task?.let { task -> itemClickListener?.onItemLongClick(adapterPosition, view, task) }
                true
            }
        }
    }

    interface TaskItemClickListener {
        fun onItemClick(task: Task)
        fun onItemLongClick(position: Int, view: View, task: Task): Boolean
    }

    fun sortByPriority(){
        taskList.sortByDescending { it.priority.ordinal }
        this.notifyDataSetChanged()
    }

    fun sortByDate(){
        taskList.sortBy { it.dueDate }
        this.notifyDataSetChanged()
    }

    fun sortByCategory(){
        taskList.sortBy { it.category }
        this.notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeekGraph(task: Task) : WeekGraph{
        return WeekGraph(taskList, task)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filter(b: Boolean) {
        filter = b
        addAll(taskList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateWeekly(): ArrayList<Task>{
        val li : ArrayList<Task> = ArrayList()
        val s = LocalDate.now().toString().replace("-",".",false)
        for(t in taskList){
            if(t.always){
                if(s > t.dueDate){
                    li.add(t)
                }
            }
        }
        return li
    }


}