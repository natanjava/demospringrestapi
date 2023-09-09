package demo.api.rest.model;

import java.io.Serializable;
import java.util.List;

public class UsuarioDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long userId;
	private String userSenha;
	private String userLogin;
	private String userNome;
	private List<Telefone>userListTelefones;
	//private String userCpf; // não tem cpf
	
	
	public UsuarioDTO(Usuario usuario) {
		this.userLogin = usuario.getLogin();
		this.userNome = usuario.getNome();
		this.userId = usuario.getId();
		this.userSenha = usuario.getSenha();
		//this.userCpf = usuario.getCpf(); // não tem cpf
		this.userListTelefones = usuario.getTelefones();
				
	}
	
	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	public String getUserNome() {
		return userNome;
	}
	public void setUserNome(String userNome) {
		this.userNome = userNome;
	}
		
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserSenha() {
		return userSenha;
	}

	public void setUserSenha(String userSenha) {
		this.userSenha = userSenha;
	}

	public List<Telefone> getUserListTelefones() {
		return userListTelefones;
	}

	public void setUserListTelefones(List<Telefone> userListTelefones) {
		this.userListTelefones = userListTelefones;
	}	
	
	
	
}
