package com.example.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;

public class TimezoneValidateFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String timezone = httpRequest.getParameter("timezone");

        if (timezone != null && !timezone.isEmpty()) {
            TimeZone timeZone = TimeZone.getTimeZone(timezone);
            if (timeZone.getID().equals("GMT")) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = httpResponse.getWriter();
                out.println("<html><body>");
                out.println("<h1>Invalid timezone</h1>");
                out.println("</body></html>");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}