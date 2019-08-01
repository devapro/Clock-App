package pro.devapp.clock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.R
import pro.devapp.clock.databinding.FragmentTimerBinding
import pro.devapp.clock.viewModels.TimerViewModel

class TimerFragment: Fragment() {
    private lateinit var mBinding: FragmentTimerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container,false)
        mBinding.lifecycleOwner = this
        mBinding.model = ViewModelProviders.of(activity!!).get(TimerViewModel::class.java)
        return  mBinding.root
    }
}