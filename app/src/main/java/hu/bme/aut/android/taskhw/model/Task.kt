package hu.bme.aut.android.taskhw.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Task(
    var id: Long? = null,
    var title: String,
    var category: String,
    var priority: Priority,
    var dueDate: String,
    var description: String,
    var always : Boolean,

) : Parcelable {
    companion object {
        var categoryList = mutableListOf("Education","Job","Sport","Hobby","Entertainment")
    }

    enum class Priority {
        LOW, MEDIUM, HIGH
    }

}