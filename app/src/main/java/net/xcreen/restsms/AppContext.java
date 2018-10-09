package net.xcreen.restsms;

import android.app.Application;

import net.xcreen.restsms.server.SMSServer;

public class AppContext extends Application {

    public SMSServer smsServer = new SMSServer();

}
