package br.com.apirest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = {"br.*"})
@EntityScan(basePackages = {"br.com.model"})
@EnableJpaRepositories(basePackages = {"br.com.repository"})
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class SpringRestApiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SpringRestApiApplication.class, args);
//		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}
	
	/*
	 *  Mapeamento global que reflete em todo o sistema
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		/*
		 * Liberando o mapeamento de usuário para todas as origens
		 */
		registry.addMapping("/usuario/**")
			.allowedOrigins("*");
		
		
//		registry.addMapping("/usuario/**")
//		.allowedMethods("POST", "GET") //Libera apenas os metodos post e get para requisições
//		.allowedOrigins("www.cliente40.com.br"); //Liberando apenas requisições para o usuário do servidor 40
	}

}
