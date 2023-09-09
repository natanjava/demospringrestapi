package demo.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.api.rest.model.UserChart;
import demo.api.rest.model.UserReport;
import demo.api.rest.model.Usuario;
import demo.api.rest.model.UsuarioDTO;
import demo.api.rest.repository.TelefoneRepository;
import demo.api.rest.repository.UsuarioRepository;
import demo.api.rest.service.ImplementacaoUserDetailsService;
import demo.api.rest.service.ServiceRelatorio;

@RestController /* Arquitetura REST */
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired /* Se fose CDI seria @Inject */
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ServiceRelatorio serviceRelatorio;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*
	 * metodo inutil, depois da declaracao do RequestMapping na declaração da classe
	 */
	/*
	 * @GetMapping(value = "/", produces = "application/json") public ResponseEntity
	 * init() { return new ResponseEntity("Ola REST Srping Boot", HttpStatus.OK); }
	 */

	/* Todos os metodos são servico Restful */
	/*
	 * @GetMapping(value = "/", produces = "application/json") public ResponseEntity
	 * init2(@RequestParam(value="nome", defaultValue = "Nome Default") String
	 * nome, @RequestParam Long salario) {
	 * 
	 * return new ResponseEntity("Ola REST Srping Boot \n Seu nome é : " +nome
	 * +". Salario: "+salario, HttpStatus.OK); }
	 */

	/* Servico Restful */
	/*
	 * @GetMapping(value = "/", produces = "application/json") 
	 * public ResponseEntity<Usuario> init3() { 
	 * Usuario usuario = new Usuario();
	 * usuario.setId(50L); usuario.setLogin("email@gmail.com");
	 * usuario.setNome("joao francisco"); usuario.setSenha("dfsd5f4");
	 * 
	 * Usuario usuario2 = new Usuario(); usuario2.setId(49L);
	 * usuario2.setLogin("pedro@gmail.com"); usuario2.setNome("pedro paulo");
	 * usuario2.setSenha("asda3232");
	 * 
	 * List<Usuario> usuarios = new ArrayList<Usuario>(); usuarios.add(usuario);
	 * usuarios.add(usuario2);
	 * 
	 * //return new ResponseEntity(usuario , HttpStatus.OK); //return
	 * ResponseEntity.ok(usuario); // faz a mesma coisa que a linha acima, mas a
	 * primeira pode imprimir lista return new ResponseEntity(usuarios ,
	 * HttpStatus.OK); // }
	 */

	/* metodo 4: retorna dados de servico, via formulario de consulta */
	@PostMapping(value = "/{id}/venda/{idvenda}", produces = "application/json")
	public ResponseEntity init4(@PathVariable Long id, @PathVariable Long idvenda) {

		// Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity("Dados do usuario:" + id + ", e da Venda:  " + idvenda + ".", HttpStatus.OK);
	}

	/* metodo 5: retorna todos os usuarios do banco, subistituido por metodo ABAIXO que usa paginacao */
	/**/
	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarios() {

		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	/*Lista Usuarios: Carregamento por demanda, implementacao inicial
	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarios () throws InterruptedException{
		
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		
		Page<Usuario> list = usuarioRepository.findAll(page);
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}*/
	
	/*Lista Usuarios: Carregamento por demanda, implementacao final*/
	/*
	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarioPagina (@PathVariable("pagina") int pagina) throws InterruptedException{
		
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		Page<Usuario> list = usuarioRepository.findAll(page);
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}*/
	
	
	/* criado na construção do Front-Ent: busca por nome, retorna lista com nomes semelhantes */
	/**/
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException {

		List<Usuario> list = (List<Usuario>) usuarioRepository.findUserByNome(nome);

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	// busca por nomes: Paginação
	/*
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarioPorNome (@PathVariable("nome") String nome) throws InterruptedException{
		
		
		PageRequest pageRequest = null;
		Page<Usuario> list = null;
		
		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) {//Não informou nome
			
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list =  usuarioRepository.findAll(pageRequest);
		}else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome")); as
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}		
				
		
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}*/
	
	// busca por nomes: Paginação detalhada
	
	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarioPorNomePage (@PathVariable("nome") String nome, @PathVariable("page") int page) throws InterruptedException{
		
		
		PageRequest pageRequest = null;
		Page<Usuario> list = null;
		
		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) { //Não informou nome
			
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list =  usuarioRepository.findAll(pageRequest);
		}else {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	

	/* metodo 6: retorna usuario (tipo DTO). Também poderia retornar um servico específico*/
	/*
	@GetMapping(value = "/{id}", produces = "application/json")
	@CachePut("cacheuser")
	public ResponseEntity<UsuarioDTO> usuarioDTO(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}*/
	
	@GetMapping(value = "/{id}", produces = "application/json")
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> usuario(@PathVariable (value = "id") Long id) {
		
	    Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

	    if (usuarioOptional.isPresent()) {
	        Usuario usuario = usuarioOptional.get();
	        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Ou outra resposta apropriada para quando o usuário não é encontrado.
	    }
	}
	
	
	

	/* POST: cadastro de usuario via Postman */
	@PostMapping(value = "/", produces = "application/json")
	//@CacheEvict(value="cacheuser", allEntries=true)
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}

	/* PUT: atualizar cadastro de usuario via Postman */
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

		if (!userTemporario.getSenha().equals(usuario.getSenha())) { /* Senhas diferentes */
			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);
		}

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}

	/* DELETE: deletar cadastro de usuario via Postman */
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String deletar(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		//return new ResponseEntity("Usuario deletado!", HttpStatus.OK); // neste caso retorno teria que ser ResponseEntity
		return "ok"; // nesse caso o tipo de retorno do metodo teria que ser String
	}
	
	@DeleteMapping(value= "/removerTelefone/{id}", produces="application/text" )
	public String removerTelefone(@PathVariable("id") Long id) {
		
		telefoneRepository.deleteById(id);
		
		return "ok";	
		
	}
	
	@GetMapping(value="/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception {
		byte[] pdf = serviceRelatorio.geraRelatorio("relatorio-usuario-api",  new HashMap(), request.getServletContext());
		
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		
		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
		
	}
	
	@PostMapping(value="/relatorio/", produces = "application/text")
	public ResponseEntity<String> downloadRelatorioParam(HttpServletRequest request, 
			@RequestBody UserReport userReport) throws Exception {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		SimpleDateFormat dateFormatParam = new SimpleDateFormat("yyyy-MM-dd");
		
		String dataInicio =  dateFormatParam.format(dateFormat.parse(userReport.getDataInicio()));
		
		String dataFim =  dateFormatParam.format(dateFormat.parse(userReport.getDataFim()));
		
		Map<String,Object> params = new HashMap<String, Object>();
		
		params.put("DATA_INICIO", dataInicio);
		params.put("DATA_FIM", dataFim);
		
		byte[] pdf = serviceRelatorio.geraRelatorio("relatorio-usuario-api-param", params,
				request.getServletContext());
		
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		
		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);		
	}
	
	@GetMapping(value= "/grafico", produces = "application/json")
	public ResponseEntity<UserChart> grafico(){
		
		UserChart userChart = new UserChart();
		
		List<String> resultado = jdbcTemplate.queryForList("select array_agg(nome) from usuario where salario > 0 and nome <> '' union all select  cast(array_agg(salario) as character varying[]) from usuario where salario > 0 and nome <> ''", String.class);
		
		if (!resultado.isEmpty()) {
			String nomes = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
			String salario = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");
			
			userChart.setNome(nomes);
			userChart.setSalario(salario);
		}
		
		return new ResponseEntity<UserChart>(userChart, HttpStatus.OK);		
	}	


}
