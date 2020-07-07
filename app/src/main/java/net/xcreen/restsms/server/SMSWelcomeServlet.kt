package net.xcreen.restsms.server

import java.io.IOException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SMSWelcomeServlet(private val serverLogging: ServerLogging) : HttpServlet() {

    @Throws(IOException::class)
    override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val requestURI = request.requestURI
        if (requestURI != null) {
            serverLogging.log("info", "Welcome-Servlet [" + request.method + "] Request " + requestURI + " From: " + request.remoteAddr)
        }
        else {
            serverLogging.log("info", "Welcome-Servlet [" + request.method + "] Request / From: " + request.remoteAddr)
        }
        response.writer.println("RestSMS-Server is running!")
    }

}