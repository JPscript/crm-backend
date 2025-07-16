package org.ediae.tfm.crmapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ediae.tfm.crmapi.entity.AppUser;
import org.ediae.tfm.crmapi.exception.GeneralException;
import org.ediae.tfm.crmapi.service.iAppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private iAppUserService appUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1) Leemos el header Authorization
        String header = request.getHeader("Authorization");

        // 2) Si no viene, delegamos SIN bloquear
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Si viene, extraemos y validamos
        String token = header.substring(7);
        try {
            if (jwtService.validateToken(token)) {
                String email = jwtService.extractUsername(token);
                AppUser appUser = appUserService.findAppUserByEmail(email);
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(appUser, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (GeneralException | RuntimeException e) {
            // token inválido o usuario no existe → recogemos, pero NO bloqueamos
            logger.warn("[JwtAuthFilter] No se pudo autenticar con el token: {}", e.getMessage());
        }

        // 4) Continuamos con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
