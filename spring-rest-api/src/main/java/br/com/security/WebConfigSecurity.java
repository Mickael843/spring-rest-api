package br.com.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.service.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
/* Mapeia URL, endereços, autoriza ou bloqueia acesso a URL */
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailsService detailsService;

	@Override
	/* Configura as solicitações de acesso por Http */
	protected void configure(HttpSecurity http) throws Exception {
		
		/* Ativando a proteção contra usuários que não estão autorizados por token */
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			/* Ativando a permissão para acesso a página inicial do sistema */
			.disable().authorizeRequests().antMatchers("/").permitAll()
			/* Permitindo acesso para o index.html */
			.antMatchers("/index").permitAll()
			/* URL de Logout - Redireciona após o usuário deslogar do sistema */
			.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
			/* Mapeia URL de Logout e invalida o usuário */
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			/* Filtrar requisições de login para autenticação */ 
			.and().addFilterBefore(new JwtLoginFilter("/login",
					authenticationManager()), UsernamePasswordAuthenticationFilter.class)
			/* Filtrar demais requisições para verificar a presença do TOKEN JWT no HEADER HTTP */
			.addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
		
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/* Service que irá consultar o usuário no banco de dados */
		auth.userDetailsService(detailsService)
				/* Padrão de códificação de senha */
				.passwordEncoder(new BCryptPasswordEncoder());
	}

}
