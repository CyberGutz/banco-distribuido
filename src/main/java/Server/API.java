package Server;

import java.rmi.*;
import java.util.ArrayList;

import Models.Transferencia;
import Models.User;

public interface API extends Remote { 

	User criarConta(String usuario,String senha) throws RemoteException;

	User fazerLogin(String usuario,String senha) throws RemoteException;

	User verSaldo(User user) throws RemoteException;
	
	User transferirDinheiro(User origem,User destino,double valor) throws RemoteException;
	
	ArrayList<Transferencia> obterExtrato(User user) throws RemoteException;
}
