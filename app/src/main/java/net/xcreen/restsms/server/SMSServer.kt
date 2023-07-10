package net.xcreen.restsms.server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import javax.servlet.MultipartConfigElement

class SMSServer {
    var port = 8080
    var goodToken = ""
    var authEnabled = false
    private var jettyServer: Server? = null

    /**
     * Get Server-Logger
     * @return serverLogging - ServerLogging-Object
     */
    /**
     * Set Server-Logger
     * @param serverLogging - ServerLogging-Object
     */
    var serverLogging: ServerLogging? = null

    @Throws(Exception::class)
    fun start(tmpDir: String?) {
        serverLogging!!.log("info", "Starting Server...")

        //Setup Jetty
        jettyServer = Server(port)
        val servletContextHandler = ServletContextHandler()
        //Setup SMS-Servlet
        val smsServletHolder = ServletHolder(SMSServlet(serverLogging!!, authEnabled, goodToken))
        smsServletHolder.registration.setMultipartConfig(MultipartConfigElement(tmpDir))
        servletContextHandler.addServlet(smsServletHolder, "/send")
        //Setup Welcome-Servlet
        val smsWelcomeServletHolder = ServletHolder(SMSWelcomeServlet(serverLogging!!))
        smsWelcomeServletHolder.registration.setMultipartConfig(MultipartConfigElement(tmpDir))
        servletContextHandler.addServlet(smsWelcomeServletHolder, "/")
        jettyServer!!.handler = servletContextHandler

        //Start Jetty
        jettyServer!!.start()
        jettyServer!!.join()
    }

    /**
     * Stopping Jetty-Server
     * @throws Exception - Jetty Exception
     */
    @Throws(Exception::class)
    fun stop() {
        if (!isStopping) {
            serverLogging!!.log("info", "Stopping Server...")
            jettyServer!!.stop()
        }
    }

    /**
     * Checks if the Jetty is running
     * @return boolean - true when server is running/starting/stopping, false otherwise
     */
    val isRunning: Boolean
        get() = if (jettyServer != null) {
            if (jettyServer!!.isRunning) {
                true
            } else jettyServer!!.isStopping
        }
        else {
            false
        }

    /**
     * Checks if Jetty is stopping
     * @return boolean - True when server is stopping
     */
    val isStopping: Boolean
        get() = if (jettyServer != null) {
            jettyServer!!.isStopping
        } else {
            false
        }
}