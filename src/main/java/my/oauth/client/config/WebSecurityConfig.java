package my.oauth.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import my.oauth.client.service.UsersDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/home", "/oauth").permitAll().antMatchers("/js/**").permitAll()
				.antMatchers("/css/**").permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login")
				.permitAll().and().logout().permitAll();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder(int n) {
		return new BCryptPasswordEncoder(n);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UsersDetailsService usersDetailsService)
			throws Exception {
		auth.userDetailsService(usersDetailsService).passwordEncoder(passwordEncoder(4));
	}
}