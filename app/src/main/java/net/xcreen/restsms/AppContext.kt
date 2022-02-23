package net.xcreen.restsms

import android.app.Application
import android.content.Context
import net.xcreen.restsms.server.SMSServer

class AppContext : Application() {

    companion object {
        lateinit var appContext: Context
    }

    var smsServer = SMSServer()

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
