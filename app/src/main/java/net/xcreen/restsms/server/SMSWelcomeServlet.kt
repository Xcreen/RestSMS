package net.xcreen.restsms.server

import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SMSWelcomeServlet(private val serverLogging: ServerLogging) : HttpServlet() {

    @Throws(IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val requestURI = request.requestURI
        val localIpAddress = getLocalIpAddress()

        if (requestURI != null) {
            serverLogging.log("info", "Welcome-Servlet [" + request.method + "] Request " + requestURI + " From: " + request.remoteAddr)
        } else {
            serverLogging.log("info", "Welcome-Servlet [" + request.method + "] Request / From: " + request.remoteAddr)
        }

        response.writer.println("RestSMS-Server is running!")
        response.writer.println("Server IP Address: $localIpAddress")
    }

    private fun getLocalIpAddress(): String {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        for (interfaceElement in Collections.list(networkInterfaces)) {
            val inetAddresses = interfaceElement.inetAddresses
            for (inetAddress in Collections.list(inetAddresses)) {
                if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.indexOf(':') == -1) {
                    return inetAddress.hostAddress
                }
            }
        }
        return "Unknown IP Address"
    }
}
