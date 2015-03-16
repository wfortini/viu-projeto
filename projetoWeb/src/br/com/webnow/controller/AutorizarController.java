package br.com.webnow.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import br.com.servicebox.common.domain.Credenciais;
import br.com.servicebox.common.net.UsuarioResponse;
import br.com.servicebox.common.net.Response;
import br.com.webnow.domain.Usuario;
import br.com.webnow.exception.UsuarioException;
import br.com.webnow.repository.AutorizarRepository;
import br.com.webnow.repository.UsuarioRepository;
import br.com.webnow.repository.servico.ServicoRepository;
import br.com.webnow.util.FileUtil;
import br.com.webnow.util.ServiceBoxWebUtil;

@Controller
public class AutorizarController {
	
	private final static Logger logger = LoggerFactory.getLogger(AutorizarController.class);
	 
	 @Autowired
	 private UsuarioRepository usuarioRepository;
	 
	 @Autowired
	 private ServicoRepository servicoRepository;

	 @Autowired
	 private AutorizarRepository autenticarRepository;
	 
	 @RequestMapping(value = "/teste", method = RequestMethod.POST)
	 public String profile(Model model) {
		 
		 String login = "wellington";
		 String pwd = "12345";
		 UsuarioResponse loginResponse = new UsuarioResponse();
		 try {
			Usuario usuario = autenticarRepository.autenticar(login, pwd);		
			
			/**
			if (usuario != null && usuario.getNodeId() != null){						
				
				loginResponse.setmCredenciais(new Credenciais[]{new Credenciais()});
				loginResponse.setCode(Response.SUCESSO);
				loginResponse.setMessage("Usu�rio autorizado");
				loginResponse.setSucesso(true);
				return "/home";
			}else{
				
				
				loginResponse.setCode(Response.USUARIO_NAO_AUTORIZADO);
				loginResponse.setMessage("Usu�rio n�o autorizado.");
				loginResponse.setSucesso(false);
				return "/home";
			} **/
			return "/home";
		} catch (Exception e) {
			logger.error("Erro ao tentar registrar usuario Android: ", e.getMessage());
			loginResponse = new UsuarioResponse();
			loginResponse.setCode(Response.ERRO_DESCONHECIDO);
			loginResponse.setMessage("Erro ao autenticar o usu�rio");			
			loginResponse.setSucesso(false);
			return "/home";
			
		}
	       
	 }
	 
	 @RequestMapping(value = "/autenticar", method = RequestMethod.POST)
	 public @ResponseBody UsuarioResponse autenticar(
			 @RequestParam(value = "login") String login,
	         @RequestParam(value = "pwd") String pwd){
		
		 UsuarioResponse loginResponse = new UsuarioResponse();
		 try {
			Usuario usuario = autenticarRepository.autenticar(login, pwd);		
			
			if (usuario != null && usuario.getId() != null){						
				
				loginResponse = new UsuarioResponse();
				loginResponse.setmCredenciais(new Credenciais[]{new Credenciais()});
				loginResponse.setNodeId(usuario.getId());
				loginResponse.setNome(usuario.getNome());
				loginResponse.setApelido(usuario.getApelido());
				loginResponse.setDataCadastro(usuario.getDataCadastro());
				loginResponse.setFotoPerfil(usuario.getFotoPerfil());
				loginResponse.setLogin(usuario.getLogin());
				loginResponse.setPassword(usuario.getPassword());
				loginResponse.setSexo(usuario.getSexo());
				loginResponse.setSobreNome(usuario.getSobreNome());
				loginResponse.setServicoJSONs(ServiceBoxWebUtil.preencherServicoJSON(usuario));
				
				
				loginResponse.setCode(Response.SUCESSO);
				loginResponse.setMessage("Usu�rio autorizado");				
				loginResponse.setSucesso(true);
				return loginResponse;
			}else{				
				
				loginResponse.setCode(Response.USUARIO_NAO_AUTORIZADO);
				loginResponse.setMessage("Usu�rio n�o autorizado.");
				loginResponse.setSucesso(false);
				return loginResponse;
			}
		} catch (Exception e) {
			logger.error("Erro ao tentar autenticar usuario Android: ", e);
			loginResponse = new UsuarioResponse();
			loginResponse.setCode(Response.ERRO_DESCONHECIDO);
			loginResponse.setMessage("Erro ao autenticar o usu�rio");			
			loginResponse.setSucesso(false);
			return loginResponse;
			
		}
		 
	 }
	 
	 
	@RequestMapping(value = "/registrarUsuario", method = RequestMethod.POST)
    public @ResponseBody Response registrarUsuario(
            @RequestParam(value = "file") MultipartFile foto,
            @RequestParam(value = "login") String login, 
            @RequestParam(value = "nome") String nome, 
            @RequestParam(value = "sobrenome") String sobrenome, 
            @RequestParam(value = "senha") String senha, 
            @RequestParam(value = "sexo") String sexo,
            @RequestParam(value = "apelido") String apelido,
            @RequestParam(value = "telefone") String telefone,
            @RequestParam(value = "imagemPerfil") String imagemPerfil) 
    
    {
    	
    	try {
    		if(foto.getSize() <= 0) throw new UsuarioException("Imagem inv�lida");
    		
	    	Usuario usuario = new Usuario(login,senha,nome,sobrenome,sexo,apelido);
	    	usuario.setTelefone(telefone);
	    	
	    	usuario.setDataCadastro(new Date());
	    	          // n�o estou tratando se o arquivo da foto j� exite
	    	usuario = FileUtil.salvarArquivoLocal(usuario, foto);    	
	    	
	    	usuario = usuarioRepository.registrar(usuario);		    	
	    	return new Response(true, "Usu�rio cadastrado com sucesso.", usuario.getId(), Response.SUCESSO);
    	}catch(UsuarioException ux){
    		return new Response(false, ux.getMessage(), null, ux.getCode());
		} catch (Exception e) {
			logger.error("Erro ao tentar registrar usuario Android: ", e.getMessage());
			return new Response(false, "Falha no cadastro do usu�rio", null, Response.ERRO_DESCONHECIDO);
		}
    }

}
