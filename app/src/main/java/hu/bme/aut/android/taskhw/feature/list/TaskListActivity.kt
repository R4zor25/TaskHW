package hu.bme.aut.android.taskhw.feature.list

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import hu.bme.aut.android.taskhw.R
import hu.bme.aut.android.taskhw.adapter.SimpleItemRecyclerViewAdapter
import hu.bme.aut.android.taskhw.feature.details.TaskDetailActivity
import hu.bme.aut.android.taskhw.feature.graph.BarChart
import hu.bme.aut.android.taskhw.feature.webview.CalendarActivity
import hu.bme.aut.android.taskhw.model.Task
import hu.bme.aut.android.taskhw.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_task_list.*
import kotlinx.android.synthetic.main.activity_todo_list.toolbar
import kotlinx.android.synthetic.main.task_list.*
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


class TaskListActivity :
    AppCompatActivity(),
    TaskCreateFragment.TaskCreatedListener,
    SimpleItemRecyclerViewAdapter.TaskItemClickListener {

    private lateinit var simpleItemRecyclerViewAdapter: SimpleItemRecyclerViewAdapter

    private lateinit var taskViewModel: TaskViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(toolbar)
        toolbar.title = title

        fabCreateTask.setOnClickListener {
            val taskCreateFragment = TaskCreateFragment()
            taskCreateFragment.show(supportFragmentManager, "TAG")
        }

        setupRecyclerView()
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            simpleItemRecyclerViewAdapter.addAll(tasks)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.itemDeleteAll){
            taskViewModel.deleteAll()
            }
        if(item.itemId == R.id.SortByPrio){
           sortByPrio()
        }
        if(item.itemId == R.id.SortByDate){
            sortByDate()
        }
        if(item.itemId == R.id.SortByCategory){
            sortByCategory()
        }
        if(item.itemId == R.id.GoogleCalendar){
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
        if(item.itemId == R.id.PastEvent){
            if(item.title == getString(R.string.hide_past_events)) {
                item.title = getString(R.string.show_past_events)
                simpleItemRecyclerViewAdapter.filter(true)
            }
            else{
                item.title = getString(R.string.hide_past_events)
                simpleItemRecyclerViewAdapter.filter(false)
            }
        }
        if(item.itemId == R.id.ExportAll){
            val li = taskViewModel.allTasks.value
            if (li != null) {
                for(task in li){
                    exportToCalendar(task)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        simpleItemRecyclerViewAdapter = SimpleItemRecyclerViewAdapter()
        simpleItemRecyclerViewAdapter.itemClickListener = this
        rvTaskList.adapter = simpleItemRecyclerViewAdapter
    }


    override fun onItemClick(task: Task) {
            val intent = Intent(this, TaskDetailActivity::class.java)
            val b = Bundle()
            b.putParcelable("GET_TASK_DATA", task)
            intent.putExtra("GET_BUNDLE", b)
            startActivityForResult(intent,0)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0){
            if(data != null) {
                val t = data.getParcelableExtra<Task>("task") as Task
                taskViewModel.delete(t)
                simpleItemRecyclerViewAdapter.deleteTask(t.id)
                taskViewModel.insert(t)
                simpleItemRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemLongClick(position: Int, view: View, task: Task): Boolean {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.menu_task)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> {
                    taskViewModel.delete(task)
                    return@setOnMenuItemClickListener true
                }
                R.id.showDiagram ->{
                    val w = simpleItemRecyclerViewAdapter.getWeekGraph(task)
                    val b = Bundle()
                    b.putParcelable("GET_GRAPH_DATA", w)
                    val intent = Intent(this, BarChart::class.java).apply {  }
                    intent.putExtra("GET_BUNDLE",b)
                    startActivity(intent)
                }
                R.id.exportEvent -> {
                    exportToCalendar(task)
                }
            }
            false
        }
        popup.show()
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTaskCreated(task: Task) {
        taskViewModel.insert(task)
        val uriNotif = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION )
        val r = RingtoneManager.getRingtone( applicationContext, uriNotif )
        r.play()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        updateWeekly()
    }

    private fun sortByPrio() {
        simpleItemRecyclerViewAdapter.sortByPriority()
    }
    private fun sortByDate() {
        simpleItemRecyclerViewAdapter.sortByDate()
    }
    private fun sortByCategory(){
        simpleItemRecyclerViewAdapter.sortByCategory()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateWeekly(){
        val t : ArrayList<Task> = simpleItemRecyclerViewAdapter.updateWeekly()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.")
        val str = StringBuilder()
        val s = LocalDate.now().toString().replace("-",".",false)
        for(task in t) {
            taskViewModel.delete(task)
            val s1= task.dueDate.split("\t")
            val d1 = LocalDate.parse(s1[0],formatter)
            var d1plus = d1.plusDays(7)
            var d1str = d1plus.toString().replace("-",".",false)
            while(s > d1str){
                d1plus = d1plus.plusDays(7)
                d1str = d1plus.toString().replace("-",".",false)
            }
            str.append(d1str)
            str.append(".")
            str.append("\t")
            str.append(s1[1])
            task.dueDate = str.toString()
            taskViewModel.insert(task)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun exportToCalendar(t: Task){
        requestNeededPermission()
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            return
        }

        val strs= t.dueDate.split("\t")
        var d = strs[0].replace(".","-",false)
        d = d.dropLast(1)
        val s = StringBuilder()
        s.append(d)
        s.append("T")
        s.append(strs[1])

        val input = LocalDateTime.parse(s.toString())

        val calID: Long = 1
        val startMillis: Long = Calendar.getInstance().run {
            set(input.year, input.monthValue - 1, input.dayOfMonth, input.hour, input.minute)
            timeInMillis
        }
        val inputplus = input.plusMinutes(10)
        val endMillis: Long = Calendar.getInstance().run {
            set(inputplus.year, inputplus.monthValue - 1, inputplus.dayOfMonth, inputplus.hour, inputplus.minute)
            timeInMillis
        }

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, t.title)
            put(CalendarContract.Events.DESCRIPTION, t.description)
            put(CalendarContract.Events.CALENDAR_ID, calID)
            put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles")
        }
        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_CALENDAR), 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Calendar permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Calendar permission NOT granted",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
