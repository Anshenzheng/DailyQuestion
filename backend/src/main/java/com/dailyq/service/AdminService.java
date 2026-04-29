package com.dailyq.service;

import com.dailyq.dto.AdminLoginRequest;
import com.dailyq.entity.Admin;
import com.dailyq.repository.AdminRepository;
import com.dailyq.security.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AdminService(AdminRepository adminRepository,
                        PasswordEncoder passwordEncoder,
                        JwtTokenUtil jwtTokenUtil) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostConstruct
    @Transactional
    public void initAdmin() {
        if (!adminRepository.existsByUsername("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("超级管理员");
            admin.setStatus(1);
            adminRepository.save(admin);
        } else {
            Admin admin = adminRepository.findByUsername("admin").orElse(null);
            if (admin != null && !passwordEncoder.matches("admin123", admin.getPassword())) {
                if ("admin123".equals(admin.getPassword())) {
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    adminRepository.save(admin);
                }
            }
        }
    }

    public String login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (admin.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        return jwtTokenUtil.generateToken(admin.getId(), true);
    }

    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
    }
}
