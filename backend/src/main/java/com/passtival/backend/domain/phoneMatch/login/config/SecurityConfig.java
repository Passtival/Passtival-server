package com.passtival.backend.domain.phoneMatch.login.config;


import com.passtival.backend.global.jwt.JWTUtil;
import com.passtival.backend.global.jwt.JwtAuthenticationFilter;
import com.passtival.backend.global.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;


@Configuration// Config파일이라는 선언
@EnableWebSecurity//Security를 위한 선언
public class SecurityConfig {

    //AuthenticationManager(아이디/비번 담는 토큰 받아서 수행 컴포넌트)가 인자로 받을 AuthenticationConfiguraion(인증에 팔요한 구성들) 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    //LoginFilter가 인자로 JWTUtile를 받기 때문에 선언
    private JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil=jwtUtil;
    }
    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
    @Bean // 비밀번호 암호화(Encoding) 와 암호화된 비밀번호 비교(Matching
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }


    //CORS문제 프론트와 백엔드의 포트 번호가 맞지 않아서 생기는 문제 MVC와 security필터(SecurityFilterChain) 2가지를 처리해야 한다.
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception{
        http
                .cors((cors)->cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration =new CorsConfiguration();
                                //프론트 url 허용.
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                //모든 메소드 허용(get, post 등 )
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                //브라우저가 쿠키, 인증 헤더(Authorization), TLS 인증서 등 사용자 인증 정보를 포함한 요청을 허용하도록 설정
                                configuration.setAllowCredentials(true);
                                //허용 해더
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                //허용 시간
                                configuration.setMaxAge(3600L);

                                //해더를 보내줄때에 Authorization에 JWT를 보내주기 때문에 보내줘야 한다.
                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));


                                return configuration;
                            }
                        }));

        //csrf disable 처리
        /*세션 방식에서는 세션이 항상 고정되기 때문에 csrf 공격에 방어하기 위해 필수적
        하지만 jwt의 경우 세션을 스테이트 리스로 관리하기 때문에 disable이 가능하다.
         */
        http
                .csrf((auth)->auth.disable());
        //우리는 jwt방식으로 사용하기 때문에 form로그인과 http.basic방식 모두 disable하면 된다.
        /*로그인 로직 구현 목표
         * 아이디 비번 검증을 위한 커스텀 필터
         * 로그인 성공시 jwt를 번환할 success 핸들러 생성
         * 새롭게 만든 로그인 커스텀 필터를 SecurityConfig에 등록
         * */
        http//formlogin 필터를 동작하지 않게 했기때문에 직접 커스컴해서 사용해여 한다.
                .formLogin((auth)->auth.disable());
        http
                .httpBasic((auth)->auth.disable());
        //경로별 권한 인가
        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/api/matching/signup").permitAll()
                        .requestMatchers("/api/matching/apply").authenticated()

                        .requestMatchers("/login", "/join").permitAll()          // 로그인/회원가입 허용
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/api/test/**").authenticated()              // 테스트 API 허용
                        .requestMatchers("/admin").hasAnyRole("ADMIN")           // 관리자 전용
                        .anyRequest().authenticated());//authenticated를 통해 나머지는 로그인한 회원들을 기준으로 사용 가능
        //jwt인증하고 토큰을 발급하는 필터 등록
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), LoginFilter.class);


        //기존의 formLogin가 disable 되지 않았다면 /login을 UsernamePasswordAuthenticationFilter가 가로채서 authenticationManager에게 전달해서 동작한다.
        //하지만 지금은 disable 했기 때문에 addFilterAt를 통해 새롭게 만든 커스텀 필터를 대채한다면 UsernamePasswordAuthenticationFilter에게 가던
        //사용자 요청이 새롭게 만든 커스텀 필터에게 전달된다.
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


}