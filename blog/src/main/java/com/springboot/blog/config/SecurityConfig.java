package com.springboot.blog.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
/**
 * Java bean configuration class for security
 * @author Garvit
 *
 */
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;

import com.springboot.blog.security.JwtAuthenticationEntryPoint;
import com.springboot.blog.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@CrossOrigin
public class SecurityConfig {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private JwtAuthenticationFilter authenticationFilter;

	/**
	 * Return Authentication Manager object
	 * 
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	/**
	 * Return BCryptPasswordEncoder object
	 * 
	 * @return
	 */
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Return SecurityFilterChain object and use for authentication and
	 * authorization
	 * 
	 * @param httpSecurity
	 * @return
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
//	        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
//	        corsConfiguration.setAllowedOrigins(List.of("/**"));

		corsConfiguration.addAllowedOriginPattern("*"); // this allows all origin
		corsConfiguration.addAllowedHeader("*"); // this allows all headers
		corsConfiguration
				.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setExposedHeaders(List.of("Authorization"));

		httpSecurity.csrf().disable().cors().configurationSource(requesrt -> corsConfiguration).and()
				.authorizeHttpRequests((authorize) ->

				{
//					authorize.anyRequest().authenticated();
					authorize.requestMatchers(HttpMethod.GET, "/api/posts").permitAll().requestMatchers("/api/auth/**")
							.permitAll().
							requestMatchers("/images/*").permitAll()
							.anyRequest().authenticated();
				}

				).exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();

	}

	/**
	 * Save default user for login
	 * 
	 * @return
	 */

//	  @Bean public UserDetailsService userDetailsService() { UserDetails user =
//	  User.builder().username("garvit").password(passwordEncoder().encode("garvit")
//	  ).roles("USER") .build();
//	  
//	  UserDetails admin =
//	  User.builder().username("admin").password(passwordEncoder().encode("admin")).
//	  roles("ADMIN") .build();
//	  
//	  return new InMemoryUserDetailsManager(user, admin);
//	  
//	 }
//	 
}
