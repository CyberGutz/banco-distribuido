
import java.rmi.*; 
import java.util.Map;

public interface API extends Remote { 

	Map<String,String> criarConta(String usuario,String senha) throws RemoteException;

	Map<String,String> fazerLogin(String usuario,String senha) throws RemoteException;

}
