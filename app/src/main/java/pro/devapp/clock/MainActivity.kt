package pro.devapp.clock

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.databinding.ActivityMainBinding
import pro.devapp.clock.services.NoiseDetectorService
import pro.devapp.clock.utils.REQUEST_MIC_PERMISSION
import pro.devapp.clock.utils.ServiceUtils
import pro.devapp.clock.utils.checkPermissionMic
import pro.devapp.clock.viewModels.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private var serviceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainBinding =  DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainBinding.lifecycleOwner = this
        mainBinding.model = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (intent?.getIntExtra("openTab", -1)!! >= 0) {
            mainBinding.model?.setActiveTab(intent?.getIntExtra("openTab", -1)!!)
        }
        if(mainBinding.model != null && checkPermissionMic(this)){
            runService()
        }
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

    private fun runService(){
        serviceIntent = Intent(this, NoiseDetectorService::class.java)
        if (!ServiceUtils.isServiceRunning(this, NoiseDetectorService::class.java)){
           startService(serviceIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_MIC_PERMISSION){
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            } else {
                runService()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
