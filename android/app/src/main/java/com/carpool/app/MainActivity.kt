package com.carpool.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.carpool.app.core.designsystem.CarPoolTheme
import com.carpool.app.feature.auth.AuthApp
class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  enableEdgeToEdge()
  setContent { CarPoolTheme { AuthApp() } }
 }
}
