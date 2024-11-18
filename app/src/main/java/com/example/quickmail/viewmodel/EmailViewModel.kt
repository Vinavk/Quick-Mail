package com.example.quickmail.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickmail.model.MailProperties
import com.example.quickmail.repository.MailRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class EmailViewModel @Inject constructor(
    private val repo: MailRepo
) : ViewModel() {


     val _emailList = MutableStateFlow<List<String>>(emptyList())

    fun sendMailsViewModel(context: Context, mailprop: MailProperties) {
        viewModelScope.launch {
            repo.sendEmail(context, _emailList.value,mailprop)
        }
    }


    fun getMailFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val emails = repo.extractEmailsFromFile(context, uri)
            _emailList.value = _emailList.value + emails
        }
    }


    fun addEmail(email: String) {
        if (email.isNotEmpty() && email !in _emailList.value) {
            _emailList.value = _emailList.value + email
        }
    }
}

