package com.example.quickmail

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quickmail.model.MailProperties
import com.example.quickmail.ui.theme.QuickMailTheme
import com.example.quickmail.viewmodel.EmailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickMailTheme {
                val emailViewModel: EmailViewModel  = hiltViewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FilePicker(emailViewModel)
                }
            }
        }
    }
}

@Composable
fun FilePicker(emailViewModel: EmailViewModel) {
    val context = LocalContext.current
    val emailInput = remember { mutableStateOf("") }
    val sendermail = remember { mutableStateOf("") }
    val submail = remember { mutableStateOf("") }
    val contentmail = remember { mutableStateOf("") }



    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                Toast.makeText(context, "Selected file: $uri", Toast.LENGTH_SHORT).show()
                emailViewModel.getMailFromUri(context, uri)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Email Management",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )


        OutlinedTextField(
            value = sendermail.value,
            onValueChange = { sendermail.value = it },
            label = { Text("Enter  Sender Email Address") },
            placeholder = { Text("e.g., example@gmail.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = emailInput.value,
            onValueChange = { emailInput.value = it },
            label = { Text("Enter Receiver Email Address") },
            placeholder = { Text("e.g., example@gmail.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )



        OutlinedTextField(
            value = submail.value,
            onValueChange = { submail.value = it },
            label = { Text("Subject") },
            placeholder = { Text("Quick Mail") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = contentmail.value,
            onValueChange = { contentmail.value = it },
            label = { Text("Content ") },
            placeholder = { Text(" This is a QuickMail") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        Button(
            onClick = {
                pickFileLauncher.launch("application/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Select a Document (Word, Excel)")
        }


        Button(
            onClick = {

                if(sendermail.value.contains("@gmail.com") && contentmail.value.isNotEmpty() && submail.value.isNotEmpty()) {

                    val mailprop = MailProperties(sendermail.value,submail.value,contentmail.value)

                    if (emailInput.value.contains("@gmail.com") || emailInput.value.isEmpty()) {
                        if (emailInput.value.isEmpty() && emailViewModel._emailList.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please Enter an Email or Select a File",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            if (emailInput.value.isNotEmpty()) {
                                emailViewModel.addEmail(emailInput.value)
                            }
                            emailInput.value = ""
                            emailViewModel.sendMailsViewModel(context,mailprop)
                        }
                    } else {
                        Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(context,"Please Provide Vallid", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Send Emails")
        }

    }
}
