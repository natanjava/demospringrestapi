package demo.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import demo.api.rest.model.Usuario;
import demo.api.rest.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		/*consulta usuario no banco no banco*/
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		if (usuario  == null) {
			throw new UsernameNotFoundException("Usuario não encontrado: "+username);
		}
		
		return new User(usuario.getLogin(), 
						usuario.getPassword(), 
						usuario.getAuthorities());
		
	}
	
	
	public void insereAcessoPadrao(Long id) {
		  
		  //Descobre qual a constraint de restricao
		  String constraint = usuarioRepository.consultaConstraintRole();
		  
		  if (constraint != null) {
			  // usuarioRepository.removerConstraintRole(constraint);	
			  //Remove a constraint
			  jdbcTemplate.execute(" alter table usuarios_role  DROP CONSTRAINT " + constraint);
		  }
		  
		  //Insere os acessos padrão
		  usuarioRepository.insereAcessoRolePadrao(id);
		  
	}

}
