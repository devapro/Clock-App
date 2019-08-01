package pro.devapp.clock.viewModels

import androidx.lifecycle.ViewModel
import pro.devapp.clock.fragments.ClockFragment
import pro.devapp.clock.fragments.MirrorFragment
import pro.devapp.clock.fragments.TimerFragment

class MainViewModel : ViewModel() {
    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private var currentTab = 0

    fun setListener(listener: OnFragmentChangeListener?) {
        fragmentChangeListener = listener
        setActiveTab(currentTab)
    }

    fun setActiveTab(index: Int) {
        currentTab = index
        if (fragmentChangeListener != null) {
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
            fragmentChangeListener!!.replaceFragment(fragmentClass)
        }
    }

    interface OnFragmentChangeListener {
        fun replaceFragment(fragmentClass: Class<*>)
    }
}