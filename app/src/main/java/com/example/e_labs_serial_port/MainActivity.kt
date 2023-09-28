package com.example.e_labs_serial_port

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_labs_serial_port.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


//    PackageManager packageManager = activity.getPackageManager();ComponentName componentName = new ComponentName(activity, YourLauncher.class);
//    packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//    Intent intent = new Intent(Intent.ACTION_MAIN);
//    intent.addCategory(Intent.CATEGORY_HOME)
//    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    activity.startActivity(intent);
//    activity.finishAndRemoveTask();
}