package my.project.forum.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                .and().formLogin().loginPage("/login")
                .and().logout().logoutSuccessUrl("/")
                .and().rememberMe().key("uniqueAndSecret")
                .and().csrf().disable();
    }
}
