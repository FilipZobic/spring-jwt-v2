package com.zobicfilip.springjwtv2.components;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ServletRequestListenerImpl implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ThreadContext.remove("X_PS_REQUEST_ID");
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // Problem when starting new threads from thread with ID
        ThreadContext.put("X_PS_REQUEST_ID", UUID.randomUUID().toString());
        if (sre.getServletRequest() instanceof HttpServletRequest request) {
            log.info("IP: {} URL: {} Method: {}", request.getRemoteAddr(), request.getRequestURL().toString(), request.getMethod());
        }
    }
}
