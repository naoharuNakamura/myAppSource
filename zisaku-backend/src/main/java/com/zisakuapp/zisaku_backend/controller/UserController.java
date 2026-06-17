package com.zisakuapp.zisaku_backend.controller;

import com.zisakuapp.zisaku_backend.model.User;
import com.zisakuapp.zisaku_backend.service.*;
import com.zisakuapp.zisaku_backend.dto.UserLoginRequest;
import com.zisakuapp.zisaku_backend.dto.UserResponse;
import com.zisakuapp.zisaku_backend.dto.UserSignupRequest;
import com.zisakuapp.zisaku_backend.dto.LoginResponse; // 💡 追加
import com.zisakuapp.zisaku_backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager; // 💡 追加

@RestController
@RequestMapping("/api/m_user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager; // 💡 追加

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody UserSignupRequest request) { // DTOに変更

        // 1. DTOからUserモデルへ変換
        User user = new User();
        user.setUserName(request.getUserName());
        user.setUserEmail(request.getUserEmail());
        user.setUserPassword(request.getUserPassword());
        user.setUserPhone(request.getUserPhone());

        // 2. 存在チェック
        if (userService.existsByUserEmail(user.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        // 3. 保存
        UserResponse createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // ログイン

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest) {
        try {
            System.out.println("====== [Controller] Reached to Method ======");
            System.out.println("Email from Front: " + loginRequest.getUserEmail());
            System.out.println("====== [Debug] AuthenticationManager Before ======");
            System.out.println(loginRequest.getUserPassword());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserEmail(),
                            loginRequest.getUserPassword()));
            System.out.println("====== [Debug] Completed ======");

            // 💡 2. 認証が通ったら、DBからユーザー情報を取ってくる
            // (UserService の authenticateUser メソッドはもう使わないので、findById等で取得)
            UserResponse user = userService.getUserByUserEmail(loginRequest.getUserEmail());

            // 💡 3. トークン生成して返す (既存と同じ)
            String token = jwtUtil.generateToken(user.getUserEmail());
            return ResponseEntity.ok(new LoginResponse(token, user));

        } catch (org.springframework.security.core.AuthenticationException e) {
            // Spring Security が認証失敗と判断した場合はここに来る
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

@PutMapping("/profile")
public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
    try {
        User savedUser = userService.updateProfile(updatedUser);
        return ResponseEntity.ok(new UserResponse(savedUser));
    } catch (RuntimeException e) {
        // メッセージの内容でエラーの種類を判断する
        if (e.getMessage().contains("既に他のユーザーに使用されています")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        // それ以外は404エラーとして処理
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam("userEmail") String userEmail) {
        // サービス層で存在チェックを行い、結果（boolean）を返す
        boolean exists = userService.existsByUserEmail(userEmail);
        return ResponseEntity.ok(exists);
    }
}