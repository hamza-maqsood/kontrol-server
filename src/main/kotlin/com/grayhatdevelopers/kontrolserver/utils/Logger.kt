package com.grayhatdevelopers.kontrolserver.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

object Logger {

    fun log(message: String) {
        val formattedMessage = formatMessage(message)
        Files.createDirectories(Paths.get("./Logs/"))
        File(getFileName()).appendText(formattedMessage, charset = Charsets.UTF_8)
        println(formattedMessage)
    }

    private fun getFileName(): String = StringBuilder().apply {
        append("./Logs/")
        append("Logs_")
        append(SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT, Locale.ENGLISH).format(Date()))
        append(".txt")
    }.toString()

    private fun formatMessage(message: String) = StringBuilder().apply {
        append(SimpleDateFormat(Constants.DEFAULT_TIME_FORMAT, Locale.ENGLISH).format(Date()))
        append(": ")
        append(message)
    }.toString()
}