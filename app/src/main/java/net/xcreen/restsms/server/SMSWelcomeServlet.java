package net.xcreen.restsms.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SMSWelcomeServlet extends HttpServlet {

    private ServerLogging serverLogging;

    public SMSWelcomeServlet(ServerLogging serverLogging){
        this.serverLogging = serverLogging;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        if(requestURI != null){
            serverLogging.log("info", "Welcome-Servlet [" + request.getMethod() + "] Request " + requestURI + " From: " + request.getRemoteAddr());
        }
        else {
            serverLogging.log("info", "Welcome-Servlet [" + request.getMethod() + "] Request / From: " + request.getRemoteAddr());
        }
        response.getWriter().println("RestSMS-Server is running!");
    }
}
