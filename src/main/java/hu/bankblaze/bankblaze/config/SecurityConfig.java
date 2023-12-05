package hu.bankblaze.bankblaze.config;

import hu.bankblaze.bankblaze.repo.EmployeeRepository;
import hu.bankblaze.bankblaze.service.JpaUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private final JpaUserDetailsService jpaUserDetailsService;

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder, EmployeeRepository employeeRepository) {
        return new JpaUserDetailsService(employeeRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/home",
                                "/queue/**",
                                "/corporate/**",
                                "/retail/**",
                                "/teller/**",
                                "/premium",
                                "/queueCall",
                                "/styles.css",
                                "/firstPage",
                                "/login.css",
                                "/flashing.css").permitAll()
                        .requestMatchers("/webjars/jquery/jquery.min.js",
                                "/webjars/sockjs-client/sockjs.min.js",
                                "/webjars/stomp-websocket/stomp.min.js",
                                "/SocketConfig.js",
                                "/bankBlaze-websocket/**").permitAll()
                        .requestMatchers("/audio/notification.mp3",
                                "/audio/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {

                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
                                response.sendRedirect("/admin");
                            } else if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("USER"))) {
                                response.sendRedirect("/employee");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                )
                .logout((logout) -> logout.logoutUrl("/logout"))
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(jpaUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
