package pro.devapp.clock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.R
import pro.devapp.clock.databinding.FragmentMirrorBinding
import pro.devapp.clock.viewModels.MirrorViewModel

class MirrorFragment: Fragment(){
    private lateinit var mBinding: FragmentMirrorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mirror, container,false)
        mBinding.lifecycleOwner = this
        mBinding.model = ViewModelProviders.of(this).get(MirrorViewModel::class.java)
        return  mBinding.root
    }
}