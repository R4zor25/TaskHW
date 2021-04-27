package hu.bme.aut.android.taskhw.feature.details

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.taskhw.R
import hu.bme.aut.android.taskhw.feature.list.DatePickerDialogFragment
import hu.bme.aut.android.taskhw.model.Task
import kotlinx.android.synthetic.main.date_fragment.*
import kotlinx.android.synthetic.main.fragment_create.*
import java.lang.StringBuilder
import java.time.LocalDate

class DateFragment : DialogFragment(),
    DatePickerDialogFragment.DateListener{

    private lateinit var listener: TaskModifiedListener
    private lateinit var task: Task

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = if (targetFragment != null) {
                targetFragment as TaskModifiedListener
            } else {
                activity as TaskModifiedListener
            }
        } catch (e: ClassCastException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        task = arguments?.getParcelable<Task>("GET_TASK") as Task

        return inflater.inflate(R.layout.date_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frgDueDate.setOnClickListener { showDatePickerDialog() }
        time_picker_frg.setIs24HourView(true)
        val spl = task.dueDate.split("\t")
        frgDueDate.text = spl[0]
        val spl1 = spl[1].split(":")
        time_picker_frg.hour = spl1[0].toInt()
        time_picker_frg.minute = spl1[1].toInt()


        btnModifyDateCancel.setOnClickListener {
            dismiss()
        }
        btnModifyDateOK.setOnClickListener {
            val s = StringBuilder()
            s.append(frgDueDate.text)
            s.append("\t")
            if (time_picker_frg.hour > 9) {
                s.append(time_picker_frg.hour)
            } else {
                s.append("0")
                s.append(time_picker_frg.hour)
            }
            s.append(":")
            if (time_picker_frg.minute > 9) {
                s.append(time_picker_frg.minute)
            } else {
                s.append("0")
                s.append(time_picker_frg.minute)
            }
            task.dueDate = s.toString()
            listener.onTaskModified(task)
            dismiss()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialogFragment()
        datePicker.setTargetFragment(this, 0)
        datePicker.show(requireFragmentManager(), DatePickerDialogFragment.TAG)
    }

    override fun onDateSelected(date: String) {
        frgDueDate.text = date
    }

    interface TaskModifiedListener {
        fun onTaskModified(t: Task)
    }
}