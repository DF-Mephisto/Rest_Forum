package my.project.forum.security;

import my.project.forum.service.CustomAuthenticationFailureHandler;
import my.project.forum.service.CustomBasicAuthenticationEntryPoint;
import my.project.forum.service.NoRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy(new NoRedirectStrategy());
        return successHandler;
    }

    @Bean
    CustomAuthenticationFailureHandler failureHandler() {
        final CustomAuthenticationFailureHandler failureHandler = new CustomAuthenticationFailureHandler();
        return failureHandler;
    }

    @Autowired
    private UserRepositoryUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure (HttpSecurity http) throws Exception
    {
        http.authorizeRequests()
                //USER
                .antMatchers(HttpMethod.POST,"/user/*/lock").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/user/*").authenticated()
                .antMatchers(HttpMethod.PATCH, "/user/*").authenticated()

                //TOPICS
                .antMatchers(HttpMethod.DELETE, "/topics/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/topics/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/topics").authenticated()

                //TAG
                .antMatchers(HttpMethod.DELETE,"/tag/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH,"/tag/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/tag").hasRole("ADMIN")

                //ROLE
                .antMatchers(HttpMethod.DELETE,"/role/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH,"/role/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/role").hasRole("ADMIN")

                //SECTIONS
                .antMatchers(HttpMethod.DELETE,"/sections/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH,"/sections/*").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/sections").hasRole("ADMIN")

                //COMMENTS
                .antMatchers(HttpMethod.DELETE,"/comments/*/likes").authenticated()
                .antMatchers(HttpMethod.DELETE, "/comments/*").authenticated()
                .antMatchers(HttpMethod.PATCH, "/comments/*").authenticated()
                .antMatchers(HttpMethod.POST,"/comments").authenticated()

                //LIKES
                .antMatchers(HttpMethod.POST,"/likes").authenticated()

                //LOGS
                .antMatchers("/log/**").hasRole("ADMIN")

                .antMatchers("/", "/**").permitAll()
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and().formLogin()
                    .successHandler(successHandler())
                    .failureHandler(failureHandler())
                .and().logout()
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                .and().rememberMe().key("uniqueAndSecret")
                .and().csrf().disable();
    }
}
