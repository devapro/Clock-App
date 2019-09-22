package pro.devapp.clock.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import pro.devapp.clock.fragments.ClockFragment
import pro.devapp.clock.fragments.MirrorFragment
import pro.devapp.clock.fragments.TimerFragment

class MainViewModel : ViewModel() {
    /**
     * Change tab listener
     */
    private var fragmentChangeListener: OnFragmentChangeListener? = null
    /**
     * Current tab index
     */
    private var currentTab : MutableLiveData<Int> = MutableLiveData()

    init {
        currentTab.value = 1
    }

    fun setListener(listener: OnFragmentChangeListener?) {
        fragmentChangeListener = listener
        setActiveTab(currentTab.value!!)
    }

    /**
     * Set current tab
     */
    fun setActiveTab(index: Int) {
        currentTab.value = index
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

    fun isActiveTab(index: Int): LiveData<Boolean> {
        return Transformations.map(currentTab, fun(input: Int): Boolean? {
            return currentTab.value == index
        })
    }

    interface OnFragmentChangeListener {
        fun replaceFragment(fragmentClass: Class<*>)
    }
}