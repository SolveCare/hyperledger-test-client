package care.solve.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private String password = "password";
    private String doctor = "DOCTOR";
    private String patient = "PATIENT";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/chaincode").permitAll()
                .antMatchers("/**").authenticated().
                and().formLogin().
                and().csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("tim.Doctor").password(password).roles(doctor);
        auth.inMemoryAuthentication().withUser("tim").password(password).roles(patient);
    }

//    @Bean
//    public SimpleGrantedAuthority grantedAuthorityDefaults() {
//        return new SimpleGrantedAuthority(""); // Remove the ROLE_ prefix
//    }
}
