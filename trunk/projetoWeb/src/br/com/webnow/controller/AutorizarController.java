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

import br.com.servicebox.common.net.Response;
import br.com.webnow.boundingCoordinates.GeoLocation;
import br.com.webnow.domain.Credenciais;
import br.com.webnow.domain.Usuario;
import br.com.webnow.exception.UsuarioException;
import br.com.webnow.net.LoginResponse;
import br.com.webnow.repository.AutorizarRepository;
import br.com.webnow.repository.UsuarioRepository;
import br.com.webnow.repository.servico.ServicoRepository;
import br.com.webnow.util.FileUtil;

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
		 LoginResponse loginResponse = new LoginResponse();
		 try {
			Usuario usuario = autenticarRepository.autenticar(login, pwd);
			
			
			double earthRadius = 6371.01;
			GeoLocation myLocation = GeoLocation.fromRadians(-22.7099105, -43.56431449999999);
			double distance = 1000;
			
			GeoLocation[] boundingCoordinates = myLocation.boundingCoordinates(distance, earthRadius);
			
			System.out.println(" @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			
			System.out.println("Latitude Minima 1" + boundingCoordinates[0].getLatitudeInRadians());
			System.out.println("Latitude Maxima 2" + boundingCoordinates[1].getLatitudeInRadians());
			System.out.println("Longitude Minima 3" + boundingCoordinates[0].getLongitudeInRadians());
			System.out.println("Longitude Maxima 4" + boundingCoordinates[1].getLongitudeInRadians());
			System.out.println("Latitude busca 5" + myLocation.getLatitudeInRadians());
			System.out.println("Latitude busca 6" + myLocation.getLatitudeInRadians());
			System.out.println("Longitude busca 7" + myLocation.getLongitudeInRadians());
			System.out.println("Distancia 8" + distance / earthRadius);
			
			
			
			/**
			for(Servico s : usuario.getServicosDisponiveis()){
				
				System.out.println("=================" + s);
				
			}
			
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
			loginResponse = new LoginResponse();
			loginResponse.setCode(Response.ERRO_DESCONHECIDO);
			loginResponse.setMessage("Erro ao autenticar o usu�rio");			
			loginResponse.setSucesso(false);
			return "/home";
			
		}
	       
	 }
	 
	 @RequestMapping(value = "/autenticar", method = RequestMethod.POST)
	 public @ResponseBody LoginResponse autenticar(
			 @RequestParam(value = "login") String login,
	         @RequestParam(value = "pwd") String pwd){
		
		 LoginResponse loginResponse = new LoginResponse();
		 try {
			Usuario usuario = autenticarRepository.autenticar(login, pwd);		
			
			if (usuario != null && usuario.getNodeId() != null){						
				
				loginResponse = new LoginResponse(usuario);
				loginResponse.setmCredenciais(new Credenciais[]{new Credenciais()});
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
			loginResponse = new LoginResponse();
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
            @RequestParam(value = "imagemPerfil") String imagemPerfil) 
    
    {
    	
    	try {
    		if(foto.getSize() <= 0) throw new UsuarioException("Imagem inv�lida");
    		
	    	Usuario usuario = new Usuario(login,senha,nome,sobrenome,sexo,null);
	    	usuario.setDataCadastro(new Date());
	    	          // n�o estou tratando se o arquivo da foto j� exite
	    	usuario = FileUtil.salvarArquivoLocal(usuario, foto);    	
	    	
	    	usuario = usuarioRepository.registrar(usuario);		    	
	    	return new Response(true, "Usu�rio cadastrado com sucesso.", usuario.getNodeId(), Response.SUCESSO);
    	}catch(UsuarioException ux){
    		return new Response(false, ux.getMessage(), null, ux.getCode());
		} catch (Exception e) {
			logger.error("Erro ao tentar registrar usuario Android: ", e.getMessage());
			return new Response(false, "Falha no cadastro do usu�rio", null, Response.ERRO_DESCONHECIDO);
		}
    }

}
