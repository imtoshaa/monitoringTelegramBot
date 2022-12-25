package by.telegram.monitoring.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static by.telegram.monitoring.bot.utils.constants.RolesConstants.ROLE_ADMIN;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(securedEnabled = true)
@ComponentScan(basePackages = "by")
@Configuration
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf()
        .disable()
        .authorizeRequests((auth) -> {
              try {
                auth
                    .antMatchers("/registration").not().fullyAuthenticated() //если залогинился, то регистрация не будет доступна
                    .antMatchers("/resources/**", "/").permitAll() //даём разрешение доступа к ресурсам всем
                    .antMatchers("/cart/confirmOrder", "/mypage", "/home").authenticated() //доступ только тем, кто залогинился
                    .antMatchers("/admins/**").hasAuthority(ROLE_ADMIN) //доступ только админам

                    .and()

                    .formLogin() //объявление блока логина ниже
                    .loginPage("/login") //адрес страницы для логина
                    .failureUrl("/login?error=true") //если не зашли, то кидает сюда
                    .usernameParameter("login") //параметр юзернейма
                    .passwordParameter("password") //пароля
                    .permitAll()
                    .defaultSuccessUrl("/home")

                    .and()
                    .logout()
                    .logoutSuccessUrl("/login")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .permitAll();
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            }
        );
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(8);
  }
}
