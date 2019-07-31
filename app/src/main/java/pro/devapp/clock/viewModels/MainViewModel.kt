package pro.devapp.clock.viewModels

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import pro.devapp.clock.R
import pro.devapp.clock.fragments.ClockFragment
import pro.devapp.clock.fragments.MirrorFragment
import pro.devapp.clock.fragments.TimerFragment


class MainViewModel(fm: FragmentManager) : ViewModel() {
    private val fragmentManager = fm
    fun setActiveTab(index: Int) {
        val fragmentClass: Class<*>
        when (index) {
            0 -> {
                fragmentClass = MirrorFragment::class.java
            }
            1 -> {
                fragmentClass = ClockFragment::class.java
            }
            2 -> {
                fragmentClass = TimerFragment::class.java
            }
            else -> fragmentClass = ClockFragment::class.java
        }
        replaceFragment(fragmentClass)
    }

    private fun replaceFragment(fragmentClass: Class<*>) {
        try {
            val fragment = fragmentClass.newInstance() as Fragment
            // replace fragment
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}