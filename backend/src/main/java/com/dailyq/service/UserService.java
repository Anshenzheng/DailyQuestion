package com.dailyq.service;

import cn.hutool.json.JSONObject;
import com.dailyq.dto.LoginRequest;
import com.dailyq.dto.LoginResponse;
import com.dailyq.entity.User;
import com.dailyq.repository.UserRepository;
import com.dailyq.security.JwtTokenUtil;
import com.dailyq.util.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final WeChatUtil weChatUtil;
    private final JwtTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository,
                       WeChatUtil weChatUtil,
                       JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.weChatUtil = weChatUtil;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        JSONObject session = weChatUtil.code2Session(request.getCode());
        
        if (session.containsKey("errcode") && session.getInt("errcode") != 0) {
            throw new RuntimeException("微信登录失败: " + session.getStr("errmsg"));
        }
        
        String openId = session.getStr("openid");
        String unionId = session.getStr("unionid");
        
        Optional<User> userOptional = userRepository.findByOpenId(openId);
        boolean isNewUser = !userOptional.isPresent();
        
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (request.getNickname() != null) {
                user.setNickname(request.getNickname());
            }
            if (request.getAvatarUrl() != null) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            if (request.getGender() != null) {
                user.setGender(request.getGender());
            }
            if (unionId != null) {
                user.setUnionId(unionId);
            }
            user = userRepository.save(user);
        } else {
            user = new User();
            user.setOpenId(openId);
            user.setUnionId(unionId);
            user.setNickname(request.getNickname());
            user.setAvatarUrl(request.getAvatarUrl());
            user.setGender(request.getGender() != null ? request.getGender() : 0);
            user = userRepository.save(user);
        }
        
        String token = jwtTokenUtil.generateToken(user.getId(), false);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setNickname(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setNewUser(isNewUser);
        
        return response;
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
