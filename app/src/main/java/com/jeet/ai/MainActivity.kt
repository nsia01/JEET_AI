package com.jeet.ai

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.rgb(15, 20, 35))

        val title = TextView(this)
        title.text = "JEET AI"
        title.textSize = 32f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        val status = TextView(this)
        status.text = "Your Personal AI Assistant"
        status.textSize = 18f
        status.setTextColor(Color.LTGRAY)
        status.gravity = Gravity.CENTER

        val start = Button(this)
        start.text = "START LISTENING"

        val stop = Button(this)
        stop.text = "STOP"

        layout.addView(title)
        layout.addView(status)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)
    }
}
