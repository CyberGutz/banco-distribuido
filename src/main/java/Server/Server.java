package Server;


import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

import org.jgroups.JChannel;
import org.jgroups.Message;

import Controllers.AuthController;
import Controllers.ContaController;
import Controllers.RMIServerController;
import Models.Transferencia;
import Models.User;

public class Server extends UnicastRemoteObject implements API {

	static JChannel channel;
	public static void main(String args[]) {

		//Hook que invoca o desligamento do registry quando o programa é encerrado
		Runtime.getRuntime().addShutdownHook(new Thread(() -> { 
			try {
				UnicastRemoteObject.unexportObject(java.rmi.registry.LocateRegistry.getRegistry(1099), true);
			} catch (Exception e) {	
			}
		}));

		try {
			
			channel= new JChannel("protocolos.xml");
			channel.connect("banco");
				bancoServer();
			channel.close();

		} catch (Exception erro) {
			// DEBUG
			System.out.println("ERRO: Server " + erro.getMessage());
			erro.printStackTrace();
		}

	}

	public Server() throws RemoteException {
		super(); // invoca o construtor do UnicastRemoteObject
	}


	public User criarConta(String usuario, String senha) throws RemoteException {
		System.out.println(String.format("Usuário pedindo criação de conta. Usuario %s Senha %s",usuario,senha));
		return AuthController.criarConta(usuario, senha);
	}

	public User fazerLogin(String usuario, String senha) throws RemoteException {
		System.out.println(String.format("Usuário pedindo login. Usuario %s Senha %s",usuario,senha));
		return AuthController.fazerLogin(usuario, senha);
	}

	public User verSaldo(User user) throws RemoteException {
		System.out.println(String.format("Usuário %s consultando seu saldo",user.getNome()));
		return ContaController.verSaldo(user);
	}

	public User transferirDinheiro(User origem,User destino,double valor) throws RemoteException {
		System.out.println(String.format("Usuário %s transferindo R$%.2f pro %s",origem.getNome(),valor,destino.getNome()));
		return ContaController.transferirDinheiro(origem, destino, valor);
	}

	public ArrayList<Transferencia> obterExtrato(User user) throws RemoteException {
		System.out.println(String.format("Usuário %s consultando extrato",user.getNome()));
		return ContaController.obterExtrato(user);
	}

	private static void bancoServer() throws RemoteException, IOException, InterruptedException{

		RMIServerController rmiServer = new RMIServerController();

		System.out.println("Rodando");
		while(true){
			if(souCoordenador()){
				System.out.println("executando controller");
				rmiServer.run();
				System.out.println("Chamei thread bancoServer()");	
			}
			System.out.println('.');
			Thread.sleep(2000);
		}

	}

	private static boolean souCoordenador(){
        return ( channel.getAddress()
                    .equals( 
                 channel.getView().getMembers().get(0) 
                           ) 
        ); 
    }

}
