package net.xcreen.restsms;

import android.util.Log;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.MultipartConfigElement;

public class SMSServer {

    private int port = 8081;
    private Server jettyServer;

    public void start(String tmpDir) throws Exception{
        Log.i("SMS-Server", "Starting Server...");

        //Setup Jetty
        jettyServer = new Server(port);

        //Sms-Servlet
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        ServletHolder smsServletHolder = new ServletHolder(new SMSServlet());
        smsServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(tmpDir));
        servletContextHandler.addServlet(smsServletHolder, "/send");

        jettyServer.setHandler(servletContextHandler);

        //Start Jetty
        jettyServer.start();
        jettyServer.join();
    }

    /**
     * Stopping Jetty-Server
     * @throws Exception - Jetty Exception
     */
    public void stop() throws Exception{
        if(jettyServer != null) {
            Log.i("SMS-Server", "Stopping Server...");
            jettyServer.stop();
        }
    }

    /**
     * Checks if the Jetty is running
     * @return boolean - true when server is running/starting/stopping, false otherwise
     */
    public boolean isRunning(){
        if(jettyServer != null){
            if(jettyServer.isRunning()){
                return true;
            }
            else if(jettyServer.isStopping()){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    /**
     * Checks if Jetty is stopping
     * @return boolean - True when server is stopping
     */
    public boolean isStopping(){
        if(jettyServer != null) {
            return jettyServer.isStopping();
        }
        else{
            return false;
        }
    }

    /**
     * Set Server-Port
     * @param port - Server-Port
     */
    public void setPort(int port){
        this.port = port;
    }

}
