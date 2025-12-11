package com.nextgen.DiuNextGen.service;

import com.nextgen.DiuNextGen.entity.User;
import com.nextgen.DiuNextGen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByVjudgeHandle(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        // --- SAFETY CHECK ---
        String role = user.getRole();
        // If role is missing, force it to USER to prevent crash
        if (role == null || role.trim().isEmpty()) {
            role = "USER";
        }

        // Create the Authority List safely
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new org.springframework.security.core.userdetails.User(
                user.getVjudgeHandle(),
                user.getPassword(),
                authorities
        );
    }
}