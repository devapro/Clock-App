package pro.devapp.clock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.databinding.ActivityMainBinding
import pro.devapp.clock.viewModels.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainBinding =  DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.lifecycleOwner = this
        mainBinding.model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainBinding.model!!.setActiveTab(1)
    }

    override fun onResume() {
        super.onResume()
        mainBinding.model!!.setListener(object: MainViewModel.OnFragmentChangeListener{
            override fun replaceFragment(fragmentClass: Class<*>) {
                this@MainActivity.replaceFragment(fragmentClass)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mainBinding.model!!.setListener(null)
    }


    private fun replaceFragment(fragmentClass: Class<*>) {
        try {
            val fragment = fragmentClass.newInstance() as Fragment
            // replace fragment
            supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
