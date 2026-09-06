package com.anikesh.saas_backend.tenant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import com.anikesh.saas_backend.repository.TenantMembershipRepository;
import com.anikesh.saas_backend.security.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantContextFilter extends OncePerRequestFilter{
    
    private final TenantMembershipRepository membershipRepository;

    public TenantContextFilter(TenantMembershipRepository membershipRepository){
        this.membershipRepository=membershipRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException{

        String path=request.getRequestURI();

        boolean isTenantScoped= !(path.startsWith("/auth") || (path.equals("/tenants") && request.getMethod().equals("POST")));

    if (!isTenantScoped) {
            filterChain.doFilter(request, response);
            return;
    }

     try{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        String tenantHeader=request.getHeader("X-Tenant-Id");
        if(tenantHeader==null || tenantHeader.isBlank()){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Tenant-Id header");
                return;
        }

        Long tenantId;
        try{
            tenantId=Long.valueOf(tenantHeader);
        }catch(NumberFormatException e){
               response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid X-Tenant-Id header");
                return;
        }

        boolean isMember = membershipRepository.existsByTenantIdAndUserId(tenantId, userDetails.getUserId());
            if (!isMember) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not a member of this tenant");
                return;
            }
        
            TenantContext.setTenantId(tenantId);
            filterChain.doFilter(request,response);

     }finally{
             TenantContext.clear();
     }
}
}
