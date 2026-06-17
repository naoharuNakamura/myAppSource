package com.zisakuapp.zisaku_backend.security;

import com.zisakuapp.zisaku_backend.mapper.UserMapper;
import com.zisakuapp.zisaku_backend.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DataSource dataSource;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        // ★3. ここにパス確認のデバッグコードを追加
        try {
            String dbUrl = dataSource.getConnection().getMetaData().getURL();
            System.out.println("====== [Debug] DBpass: " + dbUrl + " ======");
        } catch (Exception e) {
            System.out.println("====== [Debug] DBpass failure: " + e.getMessage() + " ======");
        }
        System.out.println("====== [Debug] start: " + userEmail + " ======");
        System.out.println("====== [Debug] Querying DB for: " + userEmail + " ======");
        System.out.println("====== [Debug] :" + userEmail + "( " + userEmail.length() + ") ======");
        // 検索だけして、例外は投げない
        Optional<User> userOpt = userMapper.findByUserEmail(userEmail);
        System.out.println(userOpt);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("====== [Debug] finally got user! ======");
            System.out.println("ID: " + user.getUserId());
            System.out.println("Email: " + user.getUserEmail());
            System.out.println("Name: " + user.getUserName());
            return new org.springframework.security.core.userdetails.User(
                    user.getUserEmail(),
                    user.getUserPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        } else {
            System.out.println("====== [Debug] still no user, why? ======");
            int count = userMapper.countAllUsers();
            System.out.println("====== [Debug] m_user all Records " + count + "  ======");
            List<String> emails = userMapper.getAllEmails();
            System.out.println("====== [Debug] DB Sample: ======");
            for (String email : emails) {
                // 前後に[ ]をつけて、スペースが含まれていないか確認しやすくします
                System.out.println(" -> [" + email + "]");
            }
            throw new UsernameNotFoundException("User not found: " + userEmail);
        }
    }
}