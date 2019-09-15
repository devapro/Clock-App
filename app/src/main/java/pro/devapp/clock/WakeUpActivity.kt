package pro.devapp.clock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager



class WakeUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wake_up)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val flags =
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        window.addFlags(flags)
    }
}
