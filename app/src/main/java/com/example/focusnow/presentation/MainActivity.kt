/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.focusnow.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.focusnow.R
import com.example.focusnow.presentation.theme.FocusNowTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            FocusApp("Hello Android")
        }
    }
}

suspend fun sendFocusRequest() {
    withContext(Dispatchers.IO) {
        val url = URL(BuildConfig.FOCUS_API_URL)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connect()
            
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Handle error
                println("HTTP error: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }
}

@Composable
fun FocusButton(onClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = {
            coroutineScope.launch {
                sendFocusRequest()
                onClick()
            }
        },
    ) {
        Text(text = "Focus")
    }
}

@Composable
fun FocusApp(greetingName: String) {
    var isFocusing by remember { mutableStateOf(false) }

    FocusNowTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            if (isFocusing) {
                FocusingScreen { isFocusing = false }
            } else {
                FocusButton { isFocusing = true }
            }
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Composable
fun FocusingScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(5000)
        onFinish()
    }
    
    Text(
        text = "Focusing...",
        style = MaterialTheme.typography.body1,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    FocusApp("Preview Android")
}
