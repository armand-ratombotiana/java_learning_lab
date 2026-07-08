package com.learning.backend21.tenant;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class TenantFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String tenantId = req.getHeader("X-Tenant-ID");
        if (tenantId == null || tenantId.isBlank()) {
            tenantId = extractFromSubdomain(req);
        }
        TenantContext.setTenantId(tenantId);
        log.debug("Request for tenant: {}", TenantContext.getTenantId());
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String extractFromSubdomain(HttpServletRequest request) {
        String serverName = request.getServerName();
        if (serverName != null && serverName.contains(".")) {
            return serverName.substring(0, serverName.indexOf('.'));
        }
        return "default";
    }
}
