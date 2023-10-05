
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.json.*;

public class Server extends UnicastRemoteObject implements API {

	public static void main(String args[]) {

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Desligando RMI Registry");
			try {
				UnicastRemoteObject.unexportObject(java.rmi.registry.LocateRegistry.getRegistry(1099), true);
			} catch (Exception e) {	
			}
		}));

		try {
			
			Server obj = new Server();
			
			try{
				java.rmi.registry.LocateRegistry.getRegistry(1099);
				System.out.println("Pegando serviço registry já criado");	
				Naming.rebind("rmi://localhost/calc", obj);
			}catch(Exception e){
				System.out.println("Criando registry");
				java.rmi.registry.LocateRegistry.createRegistry(1099);
				Naming.bind("rmi://localhost/calc", obj);
			}

			System.out.println("Server >> ligado no registro RMI sob o nome 'calc' ");

		} catch (Exception erro) {
			// DEBUG
			System.out.println("ERRO: Server " + erro.getMessage());
			erro.printStackTrace();
		}

	}

	// Construtor do objeto
	public Server() throws RemoteException {
		super(); // invoca o construtor do UnicastRemoteObject
	}

	// Implementa os métodos remotos disponibilizados pela interface Calculadora
	public Map<String, String> criarConta(String usuario, String senha) throws RemoteException {
		Map<String, String> retorno = new HashMap<String, String>();
		String path = "users.json";
		try {

			System.out.println(String.format("Usuário pedindo criação de conta. Usuario %s Senha %s",usuario,senha));
			
			JSONArray jsonArray;
			//tenta ler algum arquivo se já existir
			File file = new File(path);
			if(file.exists()){
				jsonArray = new JSONArray(new JSONTokener(new FileReader(path)));
				for(Object user: jsonArray){ //verificando se o usuario existe
					JSONObject jsonUser = new JSONObject(user.toString());
					if (jsonUser.getString("usuario").equals(usuario)){
						throw new Exception("Usuário já existe");
					}
				}
			}else{
				System.out.println("Nao tem arquivo");
				jsonArray = new JSONArray();
			}

			JSONObject user = new JSONObject();
			user.put("usuario", usuario);
			user.put("senha", senha);						
			String token = this.criarToken(user.toString());
			user.put("creditos", 1000.00); //começa com 1000 reais			
			user.put("senha", token); //substitui a senha pelo token

			jsonArray.put(user);
			
			FileWriter fileWriter = new FileWriter(path); 
			fileWriter.write(jsonArray.toString());
			fileWriter.close();

			System.out.println("Conta criada com sucesso.");
			retorno.put("token",token);
		} catch (Exception e) {
			retorno.put("erro", e.getMessage());
		}
		return retorno;
	}

	public Map<String, String> fazerLogin(String usuario, String senha) throws RemoteException {
		Map<String, String> retorno = new HashMap<String, String>();
		String path = "users.json";
		Boolean achou = false;
		try {
			try (FileReader fileReader = new FileReader(path)) {
				JSONArray jsonArray = new JSONArray(new JSONTokener(fileReader));
				
				for(Object user: jsonArray){ //verificando se o usuario existe
					JSONObject jsonUser = new JSONObject(user.toString());
					if (jsonUser.getString("usuario").equals(usuario)){ //achou o usuario
						achou = true;
						//monta objeto de login pra comparar os hashes
						JSONObject userLogin = new JSONObject();
						userLogin.put("usuario", usuario);
						userLogin.put("senha", senha);		
						String token = this.criarToken(userLogin.toString());

						if(jsonUser.getString("senha").equals(token)){ //login deu certo, usuario e senha batem o token cadastrado
							retorno.put("token", jsonUser.getString("senha"));
							return retorno;
						}else{
							throw new Exception("Senha inválida!");
						}
					}
				}
			
				if(!achou){
					throw new Exception("Usuário inexistente.Faça seu cadastro!");
				}

			} catch (IOException e) { 
				throw new Exception("Usuário inexistente. Faça seu cadastro!");
			}

		} catch (Exception e) {
			retorno.put("erro", e.getMessage());
		}
		return retorno;
	}

	private String criarToken(String data){
		try {			
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
   
		   // Update the digest with the input data
		   digest.update(data.getBytes());
   
		   // Generate the hash as a byte array
		   byte[] hashBytes = digest.digest();
   
		   // Convert the byte array to a hexadecimal string
		   StringBuilder hashStringBuilder = new StringBuilder();
		   for (byte b : hashBytes) {
			   hashStringBuilder.append(String.format("%02x", b));
		   }
   
		   return hashStringBuilder.toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
