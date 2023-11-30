package Server;

import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

import org.jgroups.JChannel;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

import Controllers.AuthController;
import Controllers.ClusterController;
import Controllers.ContaController;
import Controllers.RMIServerController;
import Models.Transferencia;
import Models.User;

public class Server extends UnicastRemoteObject implements API {

	static JChannel channel;
	static ClusterController cluster;

	public Server() throws RemoteException {
		super(); // invoca o construtor do UnicastRemoteObject
	}
	public static void main(String args[]) {

		try {
			// talvez usar buildind blocks como uma forma de abstração na comunicação entre
			// os membros do cluster
			channel = new JChannel("protocolos.xml");
				cluster = new ClusterController(channel);
			channel.close(); 

		} catch (Exception erro) {
			// DEBUG
			System.out.println("ERRO: Server " + erro.getMessage());
			erro.printStackTrace();
		}

	}

	// RPC - Operações ------------------------------------------------------------
	public User criarConta(String usuario, String senha) throws RemoteException {
		System.out.println(String.format("Usuário pedindo criação de conta. Usuario %s Senha %s", usuario, senha));
		User retorno;
		MethodCall metodo = new MethodCall("criarConta", new Object[]{usuario, senha}, new Class[]{String.class});
		RequestOptions opcoes = new RequestOptions();
			opcoes.setMode(ResponseMode.GET_ALL);
		try{
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, opcoes);
			retorno = rsp.getFirst();
		} catch (Exception e){
			retorno.setErro(e.getMessage());
		}
		return AuthController.criarConta(usuario, senha);
	}

	public User fazerLogin(String usuario, String senha) throws RemoteException {
		System.out.println(String.format("Usuário pedindo login. Usuario %s Senha %s", usuario, senha));
		return AuthController.fazerLogin(usuario, senha);
	}

	public User verSaldo(User user) throws RemoteException {
		System.out.println(String.format("Usuário %s consultando seu saldo", user.getNome()));
		MethodCall metodo = new MethodCall("verSaldo",new Object[]{user},new Class[]{User.class});
		RequestOptions opcoes = new RequestOptions(); 
        	opcoes.setMode(ResponseMode.GET_FIRST); 
		try {
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, opcoes);
			user = rsp.getFirst();
		} catch (Exception e) {
			user.setErro(e.getMessage());
		}
		return user;
	}

	public User transferirDinheiro(User origem, User destino, double valor) throws RemoteException {
		System.out.println(
				String.format("Usuário %s transferindo R$%.2f pro %s", origem.getNome(), valor, destino.getNome()));
		return ContaController.transferirDinheiro(origem, destino, valor);
	}

	public ArrayList<Transferencia> obterExtrato(User user) throws RemoteException {
		System.out.println(String.format("Usuário %s consultando extrato", user.getNome()));
		return ContaController.obterExtrato(user);
	}

}
