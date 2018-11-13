package net.xcreen.restsms.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.MultipartConfigElement;

public class SMSServer {

    private int port = 8080;
    private Server jettyServer;
    private ServerLogging serverLogging;

    public void start(String tmpDir) throws Exception{
        serverLogging.log("info", "Starting Server...");

        //Setup Jetty
        jettyServer = new Server(port);

        ServletContextHandler servletContextHandler = new ServletContextHandler();
        //Setup SMS-Servlet
        ServletHolder smsServletHolder = new ServletHolder(new SMSServlet(serverLogging));
        smsServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(tmpDir));
        servletContextHandler.addServlet(smsServletHolder, "/send");
        //Setup Welcome-Servlet
        ServletHolder smsWelcomeServletHolder = new ServletHolder(new SMSWelcomeServlet(serverLogging));
        smsWelcomeServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(tmpDir));
        servletContextHandler.addServlet(smsWelcomeServletHolder, "/");

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
        if(!isStopping()) {
            serverLogging.log("info", "Stopping Server...");
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

    /**
     * Set Server-Logger
     * @param serverLogging - ServerLogging-Object
     */
    public void setServerLogging(ServerLogging serverLogging){
        this.serverLogging = serverLogging;
    }
}
