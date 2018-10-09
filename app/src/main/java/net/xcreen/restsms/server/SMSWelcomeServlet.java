package net.xcreen.restsms.server;

import android.util.Log;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SMSWelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Log.i("SMS-WelcomeServlet", "Request /");
        response.getWriter().println("RestSMS-Server is running!");
    }
}
