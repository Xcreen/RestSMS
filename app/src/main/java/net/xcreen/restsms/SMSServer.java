package net.xcreen.restsms;

import android.util.Log;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class SMSServer {

    private int port = 8081;
    private Server jettyServer;

    public void start() throws Exception{
        Log.i("SMS-Server", "Starting Server...");

        //Setup Jetty
        jettyServer = new Server(port);
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(SMSServlet.class, "/send");
        jettyServer.setHandler(servletHandler);

        //Start Jetty
        jettyServer.start();
        jettyServer.join();
    }

    public void stop() throws Exception{
        Log.i("SMS-Server", "Stopping Server...");
        jettyServer.stop();
    }

    /**
     * Set Server-Port
     * @param port - Server-Port
     */
    public void setPort(int port){
        this.port = port;
    }

}
