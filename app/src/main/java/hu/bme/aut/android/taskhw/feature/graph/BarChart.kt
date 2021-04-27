package hu.bme.aut.android.taskhw.feature.graph

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import hu.bme.aut.android.taskhw.R
import hu.bme.aut.android.taskhw.model.WeekGraph
import kotlinx.android.synthetic.main.activity_bar_chart.*

class BarChart : AppCompatActivity() {

    companion object {
        const val bundleget = "GET_BUNDLE"
        const val bundlegraph = "GET_GRAPH_DATA"
    }
    lateinit var graphdata : WeekGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)
        val bundle = intent.extras?.get(bundleget) as Bundle
        graphdata = bundle.get(bundlegraph) as WeekGraph
        loadGraph()
    }

    private fun loadGraph(){
        val entries = ArrayList<BarEntry>()

        for(i in 0..6){
            for(j in 2 downTo 0){
                if(j == 0) {
                    val bar = BarEntry(i.toFloat(), graphdata.data[i][j].toFloat(), "Low")
                    entries.add(bar)
                }else if (j == 1){
                    val bar = BarEntry(i.toFloat(), graphdata.data[i][j].toFloat() + graphdata.data[i][0].toFloat(), "Medium")
                    entries.add(bar)

                }else if (j == 2){
                    val bar = BarEntry(i.toFloat(), graphdata.data[i][j].toFloat() + graphdata.data[i][0].toFloat() + graphdata.data[i][1].toFloat(), "High")
                    entries.add(bar)
                }
            }
        }
        val colors = mutableListOf<Int>(0xFFFF0000.toInt(),0xFFFFFF00.toInt(),0xFF00FF00.toInt())
        val dataSet = BarDataSet(entries,"Week")
        dataSet.colors = colors
        val data = BarData(dataSet)
        Taskchart.data = data
        Taskchart.invalidate()
    }
}