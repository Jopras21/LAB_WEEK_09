package com.example.lab_week_09

// import android.R.id.input // <--- DIHAPUS
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.lab_week_09.ui.theme.App
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.net.URLDecoder
import java.net.URLEncoder
// import java.sql.Types // <--- DIHAPUS
import com.squareup.moshi.Types // <--- DIPERBAIKI: Import yang benar untuk Moshi
import com.example.lab_week_09.R // <--- DIPERBAIKI: Import yang benar untuk Resource

// Previously we extend AppCompatActivity
// now we extend ComponentActivity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Here, we use setContent instead of setContentView
        setContent {
            // Here, we wrap our content with the theme
            // You can check out LAB_WEEK_09Theme inside Theme.kt
            LAB_WEEK_09Theme {
                // A surface container using the 'background' color from the theme
                Surface (
                    // We use Modifier.fillMaxSize() to make the surface fill the whole screen
                    modifier = Modifier.fillMaxSize(),
                    // We use MaterialTheme.colorScheme.background to get the background color and set it as the color or the surface
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App (
                        navController = navController
                    )
                }
            }
        }
    }
}

data class Student (
    var name: String
)

@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit
) {
    val listData = remember { mutableStateListOf (
        Student("Tanu"),
        Student("Tina"),
        Student("Tono")
    )}

    var inputField = remember { mutableStateOf(Student(""))}

    HomeContent(
        listData = listData,
        inputField = inputField.value,
        onInputValueChange = { input -> inputField.value = inputField.value.copy(name = input) },
        onButtonClick = {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        },
        navigateFromHomeToResult = {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            // DIPERBAIKI: Menggunakan com.squareup.moshi.Types
            val type = Types.newParameterizedType(List::class.java, Student::class.java)
            val adapter = moshi.adapter<List<Student>>(type)
            val json = adapter.toJson(listData)
            val encodedJson = URLEncoder.encode(json, "UTF-8")
            navigateFromHomeToResult(encodedJson)
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
){
    LazyColumn {
        item {
            Column (
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Here, we use Text to display a text
                OnBackgroundTitleText(text = "Enter Item")
                TextField (
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),

                    onValueChange = {
                        onInputValueChange(it)
                    }
                )

                Row {
                    PrimaryTextButton(text = stringResource (
                        id = R.string.button_click)) { // <--- DIPERBAIKI: R sekarang dikenali
                        onButtonClick()
                    }
                    PrimaryTextButton(text = stringResource (
                        id = R.string.button_navigate)) { // <--- DIPERBAIKI: R sekarang dikenali
                        navigateFromHomeToResult()
                    }
                }
            }
        }
        items(listData) { item ->
            Column (
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

@Composable
fun ResultContent(listData: String) {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    // DIPERBAIKI: Menggunakan com.squareup.moshi.Types
    val type = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(type)

    val decodedList = remember(listData) {
        runCatching {
            val decodedJson = URLDecoder.decode(listData, "UTF-8")
            adapter.fromJson(decodedJson) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    // DIPERBAIKI: Mengganti Column menjadi LazyColumn
    LazyColumn(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // DIPERBAIKI: 'items' sekarang valid di dalam LazyColumn
        items(decodedList) { student ->
            OnBackgroundItemText(text = "Student(name =" + student.name + ")")
        }
    }
}