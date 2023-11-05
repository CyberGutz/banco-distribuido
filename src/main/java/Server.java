
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import Controllers.AuthController;
import Controllers.ContaController;
import Models.Transferencia;
import Models.User;

public class Server extends UnicastRemoteObject implements API {

	public static void main(String args[]) {

		//Hook que invoca o desligamento do registry quando o programa é encerrado
		Runtime.getRuntime().addShutdownHook(new Thread(() -> { 
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
				Naming.rebind("rmi://localhost/banco", obj);
			}catch(Exception e){
				System.out.println("Criando registry");
				java.rmi.registry.LocateRegistry.createRegistry(1099);
				Naming.bind("rmi://localhost/banco", obj);
			}

			 JChannel channel=new JChannel("protocolos.xml");
  			 channel.setReceiver(new ReceiverAdapter() {
				public void receive(Message msg) {
					System.out.println("mensagem de " + msg.getSrc() + ": " + msg.getObject());
				}
			});

			channel.connect("banco");
			channel.send(new Message(null, "hello world"));
			channel.close();

			System.out.println("Server ON.");

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

}
