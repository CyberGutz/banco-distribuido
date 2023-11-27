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
		System.out.println("---------------------------------------");
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
		System.out.println("---------------------------------------");
		return user;
	}

	public User transferirDinheiro(User origem, User destino, double valor) throws RemoteException {
		System.out.println("---------------------------------------");
		System.out.println(
			String.format("Usuário %s transferindo R$%.2f pro %s", origem.getNome(), valor, destino.getNome()));
			Transferencia transferencia = new Transferencia(origem,destino,valor);
			MethodCall metodo = new MethodCall("transferirDinheiro",new Object[]{transferencia},new Class[]{Transferencia.class});
		try {
			System.out.println("Enviando mensagem pra geral");
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, new RequestOptions(ResponseMode.GET_ALL,2000));
			int erros = 0;
			int semErros = 0;
			System.out.println(rsp);
			for(User user: rsp.getResults()){
				if(user.getErro() !=  null){
					erros++;
				}else{
					semErros++;
				}
			}
			
			System.out.println(String.format("%d com erros, %d sem erro",erros,semErros));
			
		} catch (Exception e) {
			System.out.println("Erro na transferencia: " + e.getMessage());
			origem.setErro(e.getMessage());
		}
		System.out.println("---------------------------------------");
		return origem;
	}
	
	public ArrayList<Transferencia> obterExtrato(User user) throws RemoteException {
		System.out.println(String.format("Usuário %s consultando extrato", user.getNome()));
		return ContaController.obterExtrato(user);
	}

}
