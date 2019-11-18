package pro.devapp.clock.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.R
import pro.devapp.clock.databinding.FragmentClockBinding
import pro.devapp.clock.viewModels.ClockViewModel

class ClockFragment: Fragment() {
    private var mBinding: FragmentClockBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock, container,false)
        mBinding?.lifecycleOwner = this
        mBinding?.model = ViewModelProviders.of(activity!!).get(ClockViewModel::class.java)
        mBinding?.model?.setChangeFragmentListener(changeListener)
        return  mBinding?.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mBinding?.model?.setChangeFragmentListener(changeListener)
    }

    override fun onDetach() {
        super.onDetach()
        mBinding?.model?.setChangeFragmentListener(null)
    }

    private val changeListener = object : ClockViewModel.FragmentChangeListener{
        override fun replaceFragment(fragment: Fragment) {
            activity?.supportFragmentManager?.beginTransaction()?.addToBackStack("fr")?.add(R.id.content, fragment)?.commit()
        }
    }
}