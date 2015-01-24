package br.com.webnow.net;

import java.util.Date;
import java.util.Set;

import br.com.servicebox.common.domain.TipoServico;
import br.com.servicebox.common.json.ServicoJSON;
import br.com.servicebox.common.net.Response;
import br.com.webnow.domain.Carona;
import br.com.webnow.domain.Credenciais;
import br.com.webnow.domain.Estacionamento;
import br.com.webnow.domain.Reboque;
import br.com.webnow.domain.Servico;
import br.com.webnow.domain.Usuario;

/**
 * Classe LoginResponse modulo Web
 * @author wpn0510
 *
 */
public class LoginResponse extends Response{
	
	private static final long serialVersionUID = -2114605692059685414L;
	
	 private Credenciais[] mCredenciais;		 	 
	 private Long nodeId;	
	 private String login;	
	 private String password;	
	 private String nome;	
	 private String sobreNome;
	 private String sexo;
	 private String apelido;
	 private String fotoPerfil;
	 private Date dataCadastro;	
	 private ServicoJSON[] servicoJSONs;
	
	
	public LoginResponse() {
		// TODO Auto-generated constructor stub
	}
    public LoginResponse(Usuario usuario){
    	
    	this.preencherServicoJSON(usuario);		 
    	this.nodeId = usuario.getId();
    	this.login = usuario.getLogin();
    	this.apelido = usuario.getApelido();
    	this.dataCadastro = usuario.getDataCadastro();
    	this.fotoPerfil = usuario.getFotoPerfil();
    	this.nome = usuario.getNome();
    	this.sexo = usuario.getSexo();   	
    	
	 } 
    
    private void preencherServicoJSON(Usuario usuario) {
 	   
    	Set<Servico> lista = usuario.getServicosDisponiveis();
    	if(lista != null && lista.size() > 0){
    		//this.servicos = lista.toArray(new Servico[lista.size()]);  
    		
    		int i = 0;
    		this.servicoJSONs = new ServicoJSON[lista.size()];
    		for(Servico s : lista){
    			
    			ServicoJSON sj = new ServicoJSON(s.getId(),
    					         s.getServicoDisponivel(), s.getDataInicialPrestacao(), s.getTipoServico());
    			
    			this.servicoJSONs[i] = sj;
    			i++;	   			
    			
    		}		
    		
    	}
     }  	 
	
    
    public Usuario preencherUsuario(){
   	 
   	 Usuario usuario = new Usuario(this.login, this.password,this.nome,this.sobreNome,this.sexo,this.apelido);
   	 usuario.setId(this.nodeId);
   	 
   	 for(ServicoJSON s : this.servicoJSONs){
   		 
   		 if(s.getTipoServico().equals(TipoServico.CARONA.getCodigo())){
   			 
   			 Carona c = new Carona();
   			 c.setId(s.getNodeId());
   			 c.setDataInicialPrestacao(s.getDataInicialPrestacao());
   			 c.setServicoDisponivel(s.getServicoDisponivel());
   			 c.setTipoServico(s.getTipoServico());
   			 usuario.addServico(c);
   			 
   		 } else if(s.getTipoServico().equals(TipoServico.ESTACIONAMENTO.getCodigo())){
   			 
   			 Estacionamento c = new Estacionamento();
   			 c.setId(s.getNodeId());
   			 c.setDataInicialPrestacao(s.getDataInicialPrestacao());
   			 c.setServicoDisponivel(s.getServicoDisponivel());
   			 c.setTipoServico(s.getTipoServico());
   			 usuario.addServico(c);
   			 
   		 } else if(s.getTipoServico().equals(TipoServico.REBOQUE.getCodigo())){
   			
   			 Reboque c = new Reboque();
   			 c.setId(s.getNodeId());
   			 c.setDataInicialPrestacao(s.getDataInicialPrestacao());
   			 c.setServicoDisponivel(s.getServicoDisponivel());
   			 c.setTipoServico(s.getTipoServico());
   			 usuario.addServico(c);
   		 }
   		 
   	 }
   	 
   	 return usuario;
   	 
    }

	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSobreNome() {
		return sobreNome;
	}

	public void setSobreNome(String sobreNome) {
		this.sobreNome = sobreNome;
	}
	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getApelido() {
		return apelido;
	}
	public void setApelido(String apelido) {
		this.apelido = apelido;
	}
	public String getFotoPerfil() {
		return fotoPerfil;
	}
	public void setFotoPerfil(String fotoPerfil) {
		this.fotoPerfil = fotoPerfil;
	}
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	public Credenciais[] getmCredenciais() {
		return mCredenciais;
	}

	public void setmCredenciais(Credenciais[] mCredenciais) {
		this.mCredenciais = mCredenciais;
	} 
	
	public ServicoJSON[] getServicoJSONs() {
		return servicoJSONs;
	}
	public void setServicoJSONs(ServicoJSON[] servicoJSONs) {
		this.servicoJSONs = servicoJSONs;
	}
}
