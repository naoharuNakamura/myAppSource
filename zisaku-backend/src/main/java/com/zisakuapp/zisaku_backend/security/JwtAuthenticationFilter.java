package com.zisakuapp.zisaku_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. リクエストのヘッダーから「Authorization」を取り出す
        String authHeader = request.getHeader("Authorization");

        // 2. 「Bearer <トークン>」の形式になっているか確認
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 「Bearer 」の7文字を削ってトークン本体だけにする

            // 3. トークンを検証し、OKならSpring Securityに「このユーザーは認証済です」と伝える
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 次のフィルター（またはコントローラー）に処理を流す
        filterChain.doFilter(request, response);
    }
}