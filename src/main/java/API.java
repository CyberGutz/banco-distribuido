
import java.rmi.*; 
import Models.User;

public interface API extends Remote { 

	User criarConta(String usuario,String senha) throws RemoteException;

	User fazerLogin(String usuario,String senha) throws RemoteException;

}
