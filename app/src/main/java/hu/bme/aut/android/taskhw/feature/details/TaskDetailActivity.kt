package hu.bme.aut.android.taskhw.feature.details

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.taskhw.R
import hu.bme.aut.android.taskhw.feature.list.TaskListActivity
import hu.bme.aut.android.taskhw.model.Task
import kotlinx.android.synthetic.main.activity_task_detail.*
import kotlinx.android.synthetic.main.activity_todo_detail.detail_toolbar

class TaskDetailActivity : AppCompatActivity(),
    DateFragment.TaskModifiedListener{

    companion object {
        const val GET_BUN = "GET_BUNDLE"
        const val GET_TASK = "GET_TASK_DATA"
    }

    lateinit var task : Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        setSupportActionBar(detail_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

            val bundle = intent.extras?.get(GET_BUN) as Bundle
            task = bundle.get(GET_TASK) as Task
            detailsTitle.setText(task.title)

            detailsCategory.adapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_item,
                Task.categoryList
            )
            detailsPriority.adapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_item,
                listOf("Low", "Medium", "High")
            )
            detailsDescription.setText(task.description)
            if(task.always){
                details_checkbox.isChecked = true
            }
            details_duedate.text = task.dueDate
            for(i in 0..4) {
                if (detailsCategory.getItemAtPosition(i).toString() == task.category)
                    detailsCategory.setSelection(i)
            }

            for(i in 0..2) {
                if (detailsPriority.getItemAtPosition(i).toString().toLowerCase() == task.priority.toString().toLowerCase())
                    detailsPriority.setSelection(i)
            }

            item.setOnClickListener {
                val selectedPriority = when (detailsPriority.selectedItemPosition) {
                    0 -> Task.Priority.LOW
                    1 -> Task.Priority.MEDIUM
                    2 -> Task.Priority.HIGH
                    else -> Task.Priority.LOW
                }
                        task.title = detailsTitle.text.toString()
                        task.priority = selectedPriority
                        task.category = detailsCategory.selectedItem.toString()
                        task.dueDate = details_duedate.text.toString()
                        task.description = detailsDescription.text.toString()
                        task.always = details_checkbox.isChecked
                val i = Intent()
                i.putExtra("task", task)
                setResult(RESULT_OK, i)
                finish()
            }

    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, TaskListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    fun showFragment(view: View){
        val dateFragment = DateFragment()
        val bundle = Bundle()
        bundle.putParcelable("GET_TASK", task)
        dateFragment.arguments = bundle
        dateFragment.show(supportFragmentManager, "TAG")
    }

    override fun onTaskModified(t: Task) {
        details_duedate.text = t.dueDate
        task.dueDate = t.dueDate
    }
}
