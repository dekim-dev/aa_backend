package dekim.aa_backend.config;

import dekim.aa_backend.config.jwt.TokenAuthenticationFilter;
import dekim.aa_backend.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration // 설정파일로 등록
@EnableWebSecurity
public class WebSecurityConfig {

  private final UserDetailService userService;
  private final TokenAuthenticationFilter tokenAuthenticationFilter;

  @Bean
  public WebSecurityCustomizer configure() { // SS기능 비활성화
    return (web) -> web.ignoring()
            .requestMatchers(toH2Console());
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf((csrf) -> csrf.disable())
            .sessionManagement((session) -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용
                    .requestMatchers("/signin", "/signup", "/clinics/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin.disable())
            .addFilterBefore(tokenAuthenticationFilter, CorsFilter.class);
    return http.build();
  }

//  @Bean
//  public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
//    return http
//            .getSharedObject(AuthenticationManagerBuilder.class)
//            .userDetailsService(userService)
//            .passwordEncoder(bCryptPasswordEncoder)
//            .and()
//            .build();
//  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
