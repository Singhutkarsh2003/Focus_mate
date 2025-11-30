package com.example.focusmate

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import com.example.focusmate.ui.theme.FocusMateTheme
import com.example.focusmate.ui.theme.NavGraphs
import com.example.focusmate.ui.theme.data.model.Session
import com.example.focusmate.ui.theme.data.model.Subjects
import com.example.focusmate.ui.theme.data.model.Task
import com.example.focusmate.ui.theme.destinations.SessScreenRouteDestination
import com.example.focusmate.ui.theme.session.StudySessTimerService
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)
    private  lateinit var  timerService: StudySessTimerService

    private val connection = object : ServiceConnection{
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            val binder = service as StudySessTimerService.StudySessionTimerBinder
            timerService = binder.getService()
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
           isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessTimerService::class.java).also {intent ->
            bindService(intent , connection , Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if (isBound){
                FocusMateTheme {
                    DestinationsNavHost(navGraph = NavGraphs.root,
                     dependenciesContainerBuilder = {
                         dependency(SessScreenRouteDestination){timerService}
                     }
                    )
                }
            }
        }
        requestPermission()
    }
    private  fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}



