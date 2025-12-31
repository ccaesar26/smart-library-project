package ro.unitbv.tpd.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // Gateway este reactiv (WebFlux), nu MVC clasic!
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Dezactivăm CSRF pentru simplitate în teste API
                .authorizeExchange(exchanges -> exchanges
                        // Reguli de Autorizare (Cine ce are voie)
                        .pathMatchers("/api/books/**").hasAnyRole("USER", "ADMIN") // Oricine poate vedea cărțile
                        .pathMatchers("/api/ai/chat/**").hasRole("USER")           // Chat-ul e pentru Useri
                        .pathMatchers("/api/ai/ingest/**").hasRole("ADMIN")        // Doar Adminul adaugă cărți în AI
                        .anyExchange().authenticated()                             // Restul necesită login
                )
                .httpBasic(Customizer.withDefaults()) // Activăm Basic Auth (Pop-up simplu în browser)
                .formLogin(Customizer.withDefaults()); // Activăm și formularul de login default

        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        // Definim utilizatorii în memorie
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("student")
                .password("password")
                .roles("USER")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("ADMIN", "USER")
                .build();

        return new MapReactiveUserDetailsService(user, admin);
    }
}
