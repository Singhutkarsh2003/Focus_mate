package com.example.focusmate.ui.theme.session

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.focusmate.MainActivity
import com.example.focusmate.ui.theme.util.Constants.CLICK_REQUEST_CODE

object ServiceHelper {

    fun clickPendingIntent(context: Context): PendingIntent{
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "Focus_mate://dashboard/session".toUri(),
            context,
            MainActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    fun triggerService(context: Context , action: String){
        Intent(context, StudySessTimerService::class.java).apply{
            this.action = action
            context.startService(this)
        }
    }

}