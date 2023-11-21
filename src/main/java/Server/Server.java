package Server;

import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

import org.jgroups.JChannel;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.RspList;

import Controllers.AuthController;
import Controllers.ClusterController;
import Controllers.ContaController;
import Models.Transferencia;
import Models.User;

public class Server extends UnicastRemoteObject implements API {

	static ClusterController cluster;

	public Server() throws RemoteException {
		super(); // invoca o construtor do UnicastRemoteObject
	}
	public static void main(String args[]) throws Exception {
		cluster = new ClusterController(new JChannel("protocolos.xml"));
		//rodando o serviço
		cluster.bancoServer();	
	}

	// RPC - Operações ------------------------------------------------------------
	public User criarConta(String usuario, String senha) throws RemoteException {
		System.out.println(String.format("Usuário pedindo criação de conta. Usuario %s Senha %s", usuario, senha));
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
			System.out.println("Erro ao enviar saldo: " + e.getMessage());
			user.setErro(e.getMessage());
		}
		return user;
	}

	public User transferirDinheiro(User origem, User destino, double valor) throws RemoteException {
		System.out.println(
				String.format("Usuário %s transferindo R$%.2f pro %s", origem.getNome(), valor, destino.getNome()));
		MethodCall metodo = new MethodCall("transferirDinheiro",new Object[]{origem,destino,valor},new Class[]{User.class,User.class,Double.class});
		RequestOptions opcoes = new RequestOptions(); 
        	opcoes.setMode(ResponseMode.GET_ALL); 
		try {
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, opcoes);
			System.out.println(rsp);
		} catch (Exception e) {
			origem.setErro(e.getMessage());
		}
		return origem;
	}

	public ArrayList<Transferencia> obterExtrato(User user) throws RemoteException {
		System.out.println(String.format("Usuário %s consultando extrato", user.getNome()));
		return ContaController.obterExtrato(user);
	}

}
