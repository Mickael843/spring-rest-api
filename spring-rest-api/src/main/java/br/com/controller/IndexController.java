package br.com.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.model.Usuario;
import br.com.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	/* Serviço RESTfull */
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
	public ResponseEntity<Usuario> initV1(@PathVariable("id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		/* O retorno seria um relatorio */
		return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
	}
	
	/* Exemplo de controle de versão no mesmo controle */
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2")
	public ResponseEntity<Usuario> initV2(@PathVariable("id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		/* O retorno seria um relatorio */
		return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	@Cacheable("cacheUsuarios")
	public ResponseEntity<List<Usuario>> usuarios() throws InterruptedException {
		/*
		 * Vamos supor que o carregamento de usuarios
		 *  seja um processo lento e queremos carregar ele com cache
		 */
		
		// Na primeira vez que chama o método ele demora 6 segundo para ser executado
		// Nas proximas vezes ele retorna os valores armazenados no cache
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
		
		//segura o código por 6 s para testes
		Thread.sleep(6000);
		
		return new ResponseEntity<>(usuarios, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		
		for(int i = 0; i < usuario.getTelefones().size(); i++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}
		
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		
		Usuario usuarioNovo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<>(usuarioNovo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		for(int i = 0; i < usuario.getTelefones().size(); i++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}
		
		Usuario usuarioTmp = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if(!usuarioTmp.getSenha().equals(usuario.getSenha())) {
			
			usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		}
		
		Usuario usuarioAtualizado = usuarioRepository.save(usuario);
		
		return new ResponseEntity<>(usuarioAtualizado, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text") 
	public String delete(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);
		return "ok";
	}
	
}
