package com.example.e_labs_serial_port

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.UserManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_labs_serial_port.databinding.FragmentComunicationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vendor.labworks.serialportmanager.SerialPortManager
import java.lang.Exception


class CommunicationFragment : Fragment() {

    private lateinit var binding: FragmentComunicationBinding
    private var dpm: DevicePolicyManager? = null
    private val viewModel: CommunicationViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComunicationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        binding.txtRx.text = ""
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                viewModel.rxStateFlow.collect {
                    requireActivity().runOnUiThread {
                        val convertedValue = convertByteDecimalsToString(it.rx)
                        onPostExecute(convertedValue)
//                        Log.d("teste", "RXState = ${it.rx}")
                    }
                }
            }
        }
    }


    private fun initListeners() {
        // hide title bar

        // immersive mode

        // immersive mode
        requireActivity().window.setDecorFitsSystemWindows(false)
        val controller: WindowInsetsController? = requireActivity().window.insetsController
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars() or WindowInsets.Type.captionBar() or WindowInsets.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // display always ON

        // display always ON
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Create an intent filter to specify the Home category.

        // Create an intent filter to specify the Home category.
        val filter = IntentFilter(Intent.ACTION_MAIN)
        filter.addCategory(Intent.CATEGORY_HOME)
        filter.addCategory(Intent.CATEGORY_DEFAULT)

        // restrictions

        // restrictions
        val restrictions = arrayOf(
            UserManager.DISALLOW_FACTORY_RESET,
            UserManager.DISALLOW_SAFE_BOOT,
            UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA,
            UserManager.DISALLOW_ADJUST_VOLUME,
            UserManager.DISALLOW_ADD_USER,
            UserManager.DISALLOW_SYSTEM_ERROR_DIALOGS
        )

        // get references

        // get references
        dpm =
            activity?.getSystemService(AppCompatActivity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val activity = ComponentName(requireContext(), CommunicationFragment::class.java)
        val admin = ComponentName(requireContext(), DevAdmin::class.java)
        val packages = arrayOf(requireContext().packageName)

        // configure dpm

        // configure dpm
        if (dpm?.isDeviceOwnerApp(requireContext().packageName) == true) {
            for (restriction in restrictions) dpm?.addUserRestriction(admin, restriction)
            dpm?.setLockTaskFeatures(admin, DevicePolicyManager.LOCK_TASK_FEATURE_NONE)
            dpm?.setLockTaskPackages(admin, packages)
            dpm?.addPersistentPreferredActivity(admin, filter, activity)
        }

        binding.btnTx.setOnClickListener {
            if (binding.edtTx.text.isEmpty()) {
                Toast.makeText(requireContext(), "Some character is required", Toast.LENGTH_SHORT).show()
            } else {
                serialPortTx()
            }
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
        binding.btnClearRx.setOnClickListener {
            binding.txtRx.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
        if (dpm?.isLockTaskPermitted(requireContext().packageName) == true) activity?.startLockTask()
    }

    private fun showErrorMessage(title: String, msg: String) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.show()
    }


    fun serialPortTx() {
        try {
            val sp: SerialPortManager = SerialPortManager.getInstance()
            for (i in 0 until binding.edtTx.text.length) {
                val b = (binding.edtTx.text[i].code.toByte())
                sp.tx(b)
            }
            binding.edtTx.text.clear()
        } catch (e: java.lang.Exception) {
            e.message?.let { showErrorMessage("Error on TX!", it) }
        }
    }

    private fun convertByteDecimalsToString(byteDecimal: Byte): String {
        val asciiChar = byteDecimal.toInt().toChar()
        return asciiChar.toString()
    }

    private fun onPostExecute(b: String) {
        binding.txtRx.setPadding(32,0,0,0)
        try {
            binding.txtRx.append(b)
        }catch (e:Exception){
            Log.d("error","RX Error + $e")
        }
    }
}