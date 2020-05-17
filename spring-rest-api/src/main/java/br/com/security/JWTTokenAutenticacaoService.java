package br.com.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.apirest.ApplicationContextLoad;
import br.com.model.Usuario;
import br.com.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/* Tempo de validade do token em ms : 172800000 equivale a 2 dias */
	private static final long EXPIRATION_TIME = 172800000;

	/* Uma senha única para compor a autenticação */
	private static final String SECRET = "senhaMuitoDificilSecretamenteSecreta";

	/* Prefixo padrão de token */
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";

	/* Gerando token de autenticação e audicionando ao cabeçalho e resposta Http */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		/* Montagem do Token */
		String JWT = Jwts.builder() // Chama o gerador de token
				.setSubject(username) // Adiciona o usuário que está fazendo o login
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Tempo de expiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); // Compactação e augoritimo de geração de senha

		/* Junta o token com o prefixo */
		String token = TOKEN_PREFIX + " " + JWT; // Bear 23432423dsfser34r3545sdfsd4w

		/* Adiciona no cabeçalho Http */
		response.addHeader(HEADER_STRING, token); // Authorization: Bear 23432423dsfser34r3545sdfsd4w

		/* liberando a resposta para um servividor/porta diferente */
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		/* Escreve token como resposta no corpo do http */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");

	}

	/* Retorna o usuário validado com token ou caso não seja valido retorna null */
	public Authentication getAuthentication(HttpServletRequest request) {

		/* Pega o token enviado no cabeçalho Http */
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {

			/* Faz a validação do token do usuário na requisição */
			String user = Jwts.parser() // Bear 23432423dsfser34r3545sdfsd4w
					.setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")) // 23432423dsfser34r3545sdfsd4w
					.getBody().getSubject(); /* Retorna o usuário ex: mickael luiz */

			if (user != null) {

				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class)
						.findUserByLogin(user);

				if (usuario != null) {

					/* Retornar o usuario logado */
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(), 
							usuario.getSenha(),
							usuario.getAuthorities());
					
				}
			}
		}
		
		/* Não autorizado */
		return null;
	}

}
