package hu.bme.aut.android.taskhw.feature.list

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.taskhw.R
import hu.bme.aut.android.taskhw.model.Task
import kotlinx.android.synthetic.main.fragment_create.*
import java.lang.StringBuilder

class TaskCreateFragment : DialogFragment(),
    DatePickerDialogFragment.DateListener {

    private lateinit var listener: TaskCreatedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = if (targetFragment != null) {
                targetFragment as TaskCreatedListener
            } else {
                activity as TaskCreatedListener
            }
        } catch (e: ClassCastException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create, container, false)
        dialog?.setTitle(R.string.itemCreateTask)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spnrTaskPriority.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listOf("Low", "Medium", "High")
        )
        spnrcategory.adapter = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item,
                Task.categoryList
        )
        tvTaskDueDate.text = "  -  "
        tvTaskDueDate.setOnClickListener { showDatePickerDialog() }

        btnCreateTask.setOnClickListener {
            val selectedPriority = when (spnrTaskPriority.selectedItemPosition) {
                0 -> Task.Priority.LOW
                1 -> Task.Priority.MEDIUM
                2 -> Task.Priority.HIGH
                else -> Task.Priority.LOW
            }
            val selectedCategory = spnrcategory.selectedItem.toString()
            val checked :Boolean = checkbox_weekly.isChecked
            val s = StringBuilder()
            if(tvTaskDueDate.text == "  -  "){
                Toast.makeText(context,"Please give a valid date!", Toast.LENGTH_LONG).show()
            }else {
                s.append(tvTaskDueDate.text)
                s.append("\t")
                if (time_picker.hour > 9) {
                    s.append(time_picker.hour)
                } else {
                    s.append("0")
                    s.append(time_picker.hour)
                }
                s.append(":")
                if (time_picker.minute > 9) {
                    s.append(time_picker.minute)
                } else {
                    s.append("0")
                    s.append(time_picker.minute)
                }
                tvTaskDueDate.text = s.toString()

                listener.onTaskCreated(
                        Task(
                                title = etTaskTitle.text.toString(),
                                priority = selectedPriority,
                                category = selectedCategory,
                                dueDate = tvTaskDueDate.text.toString(),
                                description = etTaskDescription.text.toString(),
                                always = checked,
                            )
                )
            }
            dismiss()
        }

        btnCancelCreateTask.setOnClickListener {
            dismiss()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialogFragment()
        datePicker.setTargetFragment(this, 0)
        datePicker.show(requireFragmentManager(), DatePickerDialogFragment.TAG)
    }

    override fun onDateSelected(date: String) {
        tvTaskDueDate.text = date
        time_picker.visibility = View.VISIBLE
        time_picker.setIs24HourView(true)
    }

    interface TaskCreatedListener {
        fun onTaskCreated(task: Task)
    }

}