package hu.bme.aut.android.taskhw.feature.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.android.taskhw.R
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        webview.settings.javaScriptEnabled = true
        webview.settings.builtInZoomControls = true
        webview.loadUrl("https://calendar.google.com/calendar")
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }
}