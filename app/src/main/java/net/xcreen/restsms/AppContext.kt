package net.xcreen.restsms

import android.app.Application
import net.xcreen.restsms.server.SMSServer

class AppContext : Application() {

    var smsServer = SMSServer()

}
