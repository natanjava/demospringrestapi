package demo.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import demo.api.rest.ObjetoErro;
import demo.api.rest.model.Usuario;
import demo.api.rest.repository.UsuarioRepository;
import demo.api.rest.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value="/recuperar")
public class RecuperarController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;

	@ResponseBody
	@PostMapping(value="/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) throws Exception{
		
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		if (user == null){
			objetoErro.setCode("404"); /* Não encontrado*/
			objetoErro.setError("Usuario não encontrado");
		}
		else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String newPassword = dateFormat.format(Calendar.getInstance().getTime());
			String passwordCript = new BCryptPasswordEncoder().encode(newPassword);
			
			usuarioRepository.updateSenha(passwordCript, user.getId());
			
			serviceEnviaEmail
			.enviarEmail("Recuperação de senha", 
						user.getLogin(), 
						"Sua nova senha é: " +newPassword);
			
			/* Fazer a rotina de email*/
			objetoErro.setCode("200"); /* Encontrado*/
			objetoErro.setError("Acesso enviado para email.");
		}
		
		return new ResponseEntity<ObjetoErro>(objetoErro, HttpStatus.OK);
		
	}
}
