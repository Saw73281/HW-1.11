package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCacheable(false);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String timezone = request.getParameter("timezone");

        if (timezone == null || timezone.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("lastTimezone".equals(cookie.getName())) {
                        timezone = cookie.getValue();
                        break;
                    }
                }
            }
            if (timezone == null || timezone.isEmpty()) {
                timezone = "UTC";
            }
        } else {
            TimeZone timeZoneCheck = TimeZone.getTimeZone(timezone);
            if (!timeZoneCheck.getID().equals("GMT")) {
                Cookie cookie = new Cookie("lastTimezone", timezone);
                cookie.setMaxAge(60 * 60 * 24 * 30); // 30 днів
                response.addCookie(cookie);
            } else {
                timezone = "UTC";
            }
        }

        TimeZone timeZone = TimeZone.getTimeZone(timezone);
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(calendar.getTime());

        Context ctx = new Context();
        ctx.setVariable("currentTime", currentTime);
        ctx.setVariable("timezone", timezone);

        response.setContentType("text/html");
        templateEngine.process("time", ctx, response.getWriter());
    }
}
