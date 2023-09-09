package demo.api.rest.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import demo.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository   extends JpaRepository<Usuario, Long>{
	
	/* Validação pelo login
	 * Em algum ponto vai passar como parametro so o login porque tem so o login disponivel
	 * */
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String Login);
	
	@Query("select u from Usuario u where lower(u.nome) like lower(concat('%', ?1, '%'))")
	List<Usuario> findUserByNome(String nome);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value ="update usuario set token =?1 where login = ?2")
	void atualizaTokenUser(String token, String login); 
	
	/*native query =true convrte SQL puro para o tipo de banco que esta sendo usado */
	@Query(value="SELECT constraint_name from information_schema.constraint_column_usage  where table_name = 'usuarios_role' and column_name = 'role_id' and constraint_name <> 'unique_role_user';", nativeQuery = true)
	String consultaConstraintRole();
	
	/* no more usefull */
	@Modifying
	@Query(value = "alter table usuarios_role DROP CONSTRAINT ?1;", nativeQuery = true)
	void removerConstraintRole(String constraint);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "insert into usuarios_role (usuario_id, role_id) values(?1, (select id from role where nome_role = 'ROLE_USER')); ")
	void insereAcessoRolePadrao(Long idUser);

	default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequest) {
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		
		/* Configurando para pesquisar por nome e paginação*/
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers
						.contains().ignoreCase());
		/* Concatena configurações de busca com Usuario*/
		Example<Usuario> example = Example.of(usuario, exampleMatcher);
		/* Faz consulta com as configurações reunidas e a paginação*/
		Page<Usuario> retorno = findAll(example, pageRequest);
		
		return retorno;
		
	};
	
	
	
	@Transactional
	@Modifying
	@Query(value = "update usuario set senha = ?1 where id = ?2", nativeQuery = true)
	void updateSenha(String senha, Long codUser);


}
