package com.example.weclean

import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.weclean.ui.theme.WeCleanTheme

class EventPopup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_eventpopup)

        val displayMetrics = getResources().displayMetrics
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        // Set the width and height of the window
        val lp = window.attributes
        lp.width = (width * .8).toInt()
        lp.height = (height * .7).toInt()

        // Get the layout parameters of the window
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = windowManager.currentWindowMetrics.bounds.let { rect ->
            WindowManager.LayoutParams(
                lp.width,
                lp.height,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND or
                        WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                PixelFormat.TRANSLUCENT
            )
        }

        // Set the blur radius behind the window
        //layoutParams.blurBehindRadius(5f)

        // Set the dim amount behind the window
        layoutParams.dimAmount = 0.5f

        // Set a click listener on the window decor view
        window.decorView.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // Set the window to not be touch modal
        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL

        // Apply the layout parameters to the window
        window.attributes = layoutParams
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeCleanTheme {
        Greeting("Android")
    }
}