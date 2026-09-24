package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.repository.VolunteerRepository
import com.example.ui.navigation.ServeSyncApp
import com.example.ui.theme.ServeSyncTheme
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.OrganizerViewModel
import com.example.viewmodel.VolunteerViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = VolunteerRepository(database)

        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                        AuthViewModel(repository) as T
                    }
                    modelClass.isAssignableFrom(VolunteerViewModel::class.java) -> {
                        VolunteerViewModel(repository) as T
                    }
                    modelClass.isAssignableFrom(OrganizerViewModel::class.java) -> {
                        OrganizerViewModel(repository) as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
                }
            }
        }

        val authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        val volunteerViewModel = ViewModelProvider(this, viewModelFactory)[VolunteerViewModel::class.java]
        val organizerViewModel = ViewModelProvider(this, viewModelFactory)[OrganizerViewModel::class.java]

        setContent {
            ServeSyncTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ServeSyncApp(
                        authViewModel = authViewModel,
                        volunteerViewModel = volunteerViewModel,
                        organizerViewModel = organizerViewModel
                    )
                }
            }
        }
    }
}
