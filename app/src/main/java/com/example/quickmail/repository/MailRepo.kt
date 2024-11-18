package com.example.quickmail.repository

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.quickmail.Di.SendMailApiInterface
import com.example.quickmail.model.Content
import com.example.quickmail.model.Email
import com.example.quickmail.model.MailProperties
import com.example.quickmail.model.Personalization
import com.example.quickmail.model.SendGridEmailRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.InputStream
import java.util.regex.Pattern
import javax.inject.Inject

class MailRepo @Inject constructor(
    private val sendGrid: SendMailApiInterface
) {

    suspend fun sendEmail(context: Context, value: List<String>, mailprop: MailProperties) {
        val emailRequest = SendGridEmailRequest(
            personalizations = listOf(
                Personalization(
                    to = value.map { Email(it) }
                )
            ),
            from = Email(mailprop.SenderName),
            subject = mailprop.Subject,
            content = listOf(
                Content(type = "text/plain", value = mailprop.Content)
            )
        )

        try {
            val response = sendGrid.sendEmail(emailRequest)
            if (response.isSuccessful) {
                println("Email sent successfully!")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Email Sent", Toast.LENGTH_SHORT).show()
                }
            } else {
                println("Failed to send email: ${response.code()} - ${response.message()}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Email Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            println("Error sending email: ${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error sending email", Toast.LENGTH_SHORT).show()
            }
        }
    }


     fun extractEmailsFromFile(context: Context, uri: Uri): List<String> {
        val emailList = mutableListOf<String>()
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val mimeType = context.contentResolver.getType(uri)

            when {
                mimeType?.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document") == true -> {
                    val document = XWPFDocument(inputStream)
                    emailList.addAll(extractEmailsFromWord(document))
                }
                mimeType?.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") == true -> {
                    val workbook = WorkbookFactory.create(inputStream)
                    emailList.addAll(extractEmailsFromExcel(workbook))
                }
                else -> {
                    Toast.makeText(context, "Unsupported file type", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error reading the document: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return emailList
    }

    private fun extractEmailsFromWord(document: XWPFDocument): List<String> {
        val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        val emailList = mutableListOf<String>()
        for (paragraph in document.paragraphs) {
            val matcher = emailPattern.matcher(paragraph.text)
            while (matcher.find()) {
                emailList.add(matcher.group())
            }
        }
        return emailList
    }

    private fun extractEmailsFromExcel(workbook: org.apache.poi.ss.usermodel.Workbook): List<String> {
        val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        val emailList = mutableListOf<String>()
        for (sheetIndex in 0 until workbook.numberOfSheets) {
            val sheet = workbook.getSheetAt(sheetIndex)
            for (row in sheet) {
                for (cell in row) {
                    val cellValue = cell.toString()
                    val matcher = emailPattern.matcher(cellValue)
                    while (matcher.find()) {
                        emailList.add(matcher.group())
                    }
                }
            }
        }
        return emailList
    }
}
