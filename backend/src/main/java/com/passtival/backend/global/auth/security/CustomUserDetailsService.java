package com.passtival.backend.global.auth.security;


import com.passtival.backend.domain.user.entity.User;
import com.passtival.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("사용자 조회 시도: username = {}", username);

        // username은 전화번호로 사용
        User user = userRepository.findByPhoneNumber(username)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없음: phoneNumber = {}", username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });

        log.info("사용자 조회 성공: userId = {}, name = {}",
                user.getUserId(), user.getName());

        return new CustomUserDetails(user);
    }
}
