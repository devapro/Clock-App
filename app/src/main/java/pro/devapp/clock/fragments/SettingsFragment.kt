package pro.devapp.clock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.R
import pro.devapp.clock.databinding.FragmentSettingsBinding
import pro.devapp.clock.viewModels.SettingsViewModel

class SettingsFragment: Fragment() {
    private var mBinding: FragmentSettingsBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container,false)
        mBinding!!.lifecycleOwner = this
        mBinding!!.model = ViewModelProviders.of(activity!!).get(SettingsViewModel::class.java)
        return  mBinding!!.root
    }
}