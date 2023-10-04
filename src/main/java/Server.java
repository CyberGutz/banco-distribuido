
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
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

		try {

			System.out.println(String.format("Usuário pedindo criação de conta. Usuario %s Senha %s",usuario,senha));
			JSONArray jsonArray;

			//tenta ler algum arquivo se já existir
			try (FileReader fileReader = new FileReader("users.json")) {
				jsonArray = new JSONArray(new JSONTokener(fileReader));
			} catch (IOException e) {
				jsonArray = new JSONArray();
			}

			JSONObject user = new JSONObject();
			user.put("usuario", usuario);
			user.put("senha", senha);

			jsonArray.put(user);

			FileWriter fileWriter = new FileWriter("users.json"); 
			jsonArray.write(fileWriter);
			System.out.println();
			System.out.println("Conta criada com sucesso.");
			retorno.put("token","zazaza");
		} catch (Exception e) {
			retorno.put("erro", e.getMessage());
		}
		return retorno;
	}

	public Map<String, String> fazerLogin(String usuario, String senha) throws RemoteException {
		Map<String, String> retorno = new HashMap<String, String>();
		retorno.put("token", "1234");

		return retorno;
	}

}
