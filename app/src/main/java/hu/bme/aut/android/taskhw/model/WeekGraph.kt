package hu.bme.aut.android.taskhw.model

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import kotlin.collections.ArrayList
@Parcelize
data class WeekGraph(var data: ArrayList<ArrayList<Int>> = ArrayList<ArrayList<Int>>()) : Parcelable {


    @RequiresApi(Build.VERSION_CODES.O)
    constructor(v :MutableList<Task>, t: Task):this(){
        for(i in 0..6){
             data.add(ArrayList<Int>())
            for(j in 0..2){
               data[i].add(j,0)
            }
        }
        for(task in v){
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.")
            val s1 = task.dueDate.split("\t")      //Ciklus dátum
            val s2 = t.dueDate.split("\t")        // kapott dátum
            if(s2[0] == "  -  " || s1[0] == "  -  "){ //s2[1]
                return
            }
            val d1 = LocalDate.parse(s1[0],formatter)       //  Ciklus dátum formatted
            val d2 = LocalDate.parse(s2[0],formatter)      //   Kapott dátum formatted
            val d2plus = d1.plusDays(7)        //    Kapott dátum formatted + 1 hét
            if(d1.isAfter(d2.minusDays(1)) && d1.isBefore(d2plus)){
                val help : Int = when(task.priority){
                    Task.Priority.LOW -> 0
                    Task.Priority.MEDIUM -> 1
                    Task.Priority.HIGH -> 2
                }
                for(i in 0..6){
                    if(d1.dayOfMonth == d2.plusDays(i.toLong()).dayOfMonth) {
                        data[i][help] = data[i][help] + 1
                    }
                }
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

}