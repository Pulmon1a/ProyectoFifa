package co.edu.unbosque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder encoder) {
		UserDetails aficionado = User.builder().username("aficionado").password(encoder.encode("aficionado123"))
				.roles("FAN").build();

		UserDetails funcionario = User.builder().username("fifa").password(encoder.encode("fifa2026")).roles("FIFA")
				.build();

		return new InMemoryUserDetailsManager(aficionado, funcionario);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/h2-console/**").permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/fifa-world-cup/api/v1/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/**").hasRole("FIFA")
						.requestMatchers(HttpMethod.POST, "/fifa-world-cup/api/v1/**").hasRole("FIFA")
						.requestMatchers(HttpMethod.PUT, "/api/v1/**").hasRole("FIFA")
						.requestMatchers(HttpMethod.PUT, "/fifa-world-cup/api/v1/**").hasRole("FIFA")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("FIFA")
						.requestMatchers(HttpMethod.DELETE, "/fifa-world-cup/api/v1/**").hasRole("FIFA").anyRequest()
						.authenticated())
				.httpBasic(basic -> {
				}).headers(headers -> headers.frameOptions(frame -> frame.disable()));

		return http.build();
	}
}