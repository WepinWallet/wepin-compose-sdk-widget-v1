package com.wepin.cm.widgetlib.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.wepin.cm.widgetlib.storage.AppData

actual fun getDomain(): String {
    val context = AppData.getContext() as Context
    return context.packageName
}

actual fun getPlatform(): Int {
    return 2
}