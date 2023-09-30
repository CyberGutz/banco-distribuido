
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
		try {

			// Cria um objeto do tipo da classe CalculadoraServer.
			//Server obj = new Server();

			// Liga (bind) esta instancia de objeto ao nome "calc" no registro RMI no
			// localhost
			//Naming.bind("rmi://localhost/calc", obj);

			// Liga (bind) esta instancia de objeto ao nome "Calculadora" em um registro RMI
			// contido em outra máquina
			// Naming.rebind("rmi://172.22.70.30:1099/calc", obj);
			JSONArray jsonArray = new JSONArray(10);
			System.out.println(jsonArray);
			// DEBUG
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
