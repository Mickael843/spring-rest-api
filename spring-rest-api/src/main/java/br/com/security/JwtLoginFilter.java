package br.com.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.model.Usuario;

/* Estabelece o nosso gerente de token */
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

	/* Configurando o gerenciador de autenticação */
	protected JwtLoginFilter(String url, AuthenticationManager authenticationManager) {
	
		/* Obrigamos a autenticar a url */
		super(new AntPathRequestMatcher(url));
		
		/* Gerenciador de autenticação */
		setAuthenticationManager(authenticationManager);
		
	}

	@Override
	/* Retorna o usuário ao processar a autenticação */
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		/* Está pegando o token para autenticar */	
		Usuario user = new ObjectMapper()
				.readValue(request.getInputStream(), Usuario.class);
		
		/* Retorna o usuário login, senha e acesso */
		return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(user.getLogin(), 
						user.getSenha(), 
						user.getAuthorities()));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
	}
	
}
