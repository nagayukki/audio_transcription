package app.naga.audiotranscription.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * アプリの設定画面を開くユーティリティメソッド
 *
 * @param context コンテキスト
 */
fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}