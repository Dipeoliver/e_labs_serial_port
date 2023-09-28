package com.example.e_labs_serial_port

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import vendor.labworks.serialportmanager.SerialPortManager
import java.lang.Exception

data class RxState(val rx: Byte)


class CommunicationViewModel : ViewModel() {
    private val _rxStateFlow = MutableStateFlow(RxState(0))
    val rxStateFlow: Flow<RxState> = flow {

        while (true) {
            try {
                val sp = SerialPortManager.getInstance()
                emit(RxState(sp.rx()))
                delay(100)
            } catch (e: Exception) {
                Log.d("error","RX Error + $e")
            }
        }
    }
}