package net.xcreen.restsms.server

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import net.xcreen.restsms.AppContext
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import javax.servlet.MultipartConfigElement

class SMSServer {
    var port = 8080
    var startNSD = false
    var goodToken = ""
    var authEnabled = false
    private var jettyServer: Server? = null
    private var nsdManager: NsdManager? = null
    private var nsdRegistrationListener: NsdManager.RegistrationListener? = null
    private val TAG = "SMSServer"
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

        //Start NSD
        val nsdServiceInfo = NsdServiceInfo()
        nsdServiceInfo.serviceName = "RestSMS"
        nsdServiceInfo.serviceType = "_http._tcp."
        nsdServiceInfo.port = port

        nsdManager = AppContext.appContext.getSystemService(Context.NSD_SERVICE) as NsdManager

        nsdRegistrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                val registeredName = serviceInfo.serviceName
                Log.i(TAG, "NSD Service registered: $registeredName")
                serverLogging!!.log("info", "NSD Service registered: $registeredName")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "NSD Service registration failed: $errorCode")
                serverLogging!!.log("error", "NSD Service registration failed: $errorCode")
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "NSD Service unregistered.")
                serverLogging!!.log("info", "NSD Service unregistered.")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "NSD Service unregistration failed: $errorCode")
                serverLogging!!.log("error", "NSD Service unregistration failed: $errorCode")
            }
        }
        if(startNSD) {
            nsdManager!!.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, nsdRegistrationListener)
        }
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
        nsdRegistrationListener?.let {
            nsdManager?.unregisterService(it)
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