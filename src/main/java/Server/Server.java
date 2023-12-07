package Server;

import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import Controllers.AuthController;
import Controllers.ClusterController;
import Controllers.ContaController;
import Models.State;
import Models.Transferencia;
import Models.User;

public class Server extends UnicastRemoteObject implements API {

	static ClusterController cluster;

	public Server() throws RemoteException {
		super(); // invoca o construtor do UnicastRemoteObject
	}

	public static void main(String args[]) throws Exception {
		cluster = new ClusterController(new JChannel("protocolos.xml"));
		// rodando o serviço
		cluster.bancoServer();
	}

	// RPC - Operações ------------------------------------------------------------
	public User criarConta(String usuario, String senha) throws RemoteException {
		User retorno = null;
		try {
			State estadoAtual = new State();
			System.out.println(String.format("Usuário pedindo criação de conta. Usuario %s Senha %s", usuario, senha));
			MethodCall metodo = new MethodCall("criarConta", new Object[]{usuario, senha}, new Class[]{String.class});
			RequestOptions opcoes = new RequestOptions(ResponseMode.GET_ALL, 2000);
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, opcoes);
			ArrayList<Address> membrosSemErros = new ArrayList<Address>();
			int erros = 0;
			int semErros = 0;
			String msg = "";
			System.out.println(rsp);

			for(Entry<Address, Rsp<User>> user : rsp.entrySet()){
				if(user.getValue().wasReceived()){
					System.out.println("Membro não recebeu" + user.getKey());
				}
				if(user.getValue().getValue().getErro() == null){
					semErros ++;
					membrosSemErros.add(user.getKey());
					retorno = user.getValue().getValue();
				}
				else{
					// Revertendo contas criadas
					erros++;
					msg = user.getValue().getValue().getErro();
				}
			}
			System.out.println(String.format("%d com erros, %d sem erro", erros, semErros));
			if(erros > 0){
				//todos deram erro
				if(erros == rsp.size()){
					throw new Exception(msg);
				}
				if(erros > semErros && semErros > 0){ // A maioria deu erro
					System.out.println("Revertendo criação de conta...");
					cluster.getDispatcher().callRemoteMethods(membrosSemErros, 
						"rollback",
						new Object[]{estadoAtual},
						new Class[]{State.class}, 
						new RequestOptions(ResponseMode.GET_NONE,2000));
					throw new Exception("Serviço indisponível,tente novamente mais tarde!");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			retorno.setErro(e.getMessage());
		}
		return retorno;
	}

	public User fazerLogin(String usuario, String senha) throws RemoteException {
		System.out.println(String.format("Usuário pedindo login. Usuario %s Senha %s", usuario, senha));
		return AuthController.fazerLogin(usuario, senha);
	}

	public User consultarConta(User conta) throws RemoteException {
		try {
			MethodCall metodo = new MethodCall("consultarConta", new Object[] {conta},
					new Class[] { User.class });
			System.out.println("Consultando conta pra geral");
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo,
					new RequestOptions(ResponseMode.GET_ALL, 2000));
			for (Entry<Address, Rsp<User>> user : rsp.entrySet()) { //iterando as respostas dos membros
				if(user.getValue().wasReceived()){
					if(user.getValue().getValue().getErro() == null){ //algum membro nao tem a conta consultada 
						conta = user.getValue().getValue();
					}else{
						return null;
					}
				} 
			}
		} catch (Exception e) {
			return null;
		}
		return conta;
	}
	
	public User verSaldo(User user) throws RemoteException {
		System.out.println("---------------------------------------");
		System.out.println(String.format("Usuário %s consultando seu saldo", user.getNome()));
		MethodCall metodo = new MethodCall("verSaldo", new Object[] { user }, new Class[] { User.class });
		try {
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, new RequestOptions(ResponseMode.GET_ALL,2000));
			for (Entry<Address, Rsp<User>> userRsp : rsp.entrySet()) { //iterando as respostas dos membros
				if(!userRsp.getValue().wasReceived()){
					System.out.println("Membro não recebeu: " + userRsp.getKey());
				}else if(userRsp.getValue().getValue().getErro() != null){ //membro recebeu mas deu erro 
					cluster.getDispatcher().callRemoteMethod(userRsp.getKey(), "desconectar",null,null,new RequestOptions(ResponseMode.GET_NONE,2000));
				} else {
					System.out.println("Sem erro: " + userRsp.getKey());
					if(userRsp.getValue().getValue().getVersao() >= user.getVersao()){ //obtendo o saldo mais atualizado possível
						user = userRsp.getValue().getValue();					
					}else{ //algum membro ta com a versão desatualizada
						cluster.getDispatcher().callRemoteMethod(userRsp.getKey(), "desconectar",null,null,new RequestOptions(ResponseMode.GET_NONE,2000));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Erro ao verificar saldo: " + e.getMessage());
			user.setErro(e.getMessage());
		}
		System.out.println("---------------------------------------");
		return user;
	}

	public User transferirDinheiro(User origem, User destino, double valor) throws RemoteException {
		System.out.println("---------------------------------------");
		System.out.println(
				String.format("Usuário %s transferindo R$%.2f pro %s", origem.getNome(), valor, destino.getNome()));
		try {
			State estadoAtual = new State(); // fazendo um backup do estado atual
			Transferencia transferencia = new Transferencia(origem, destino, valor);
			MethodCall metodo = new MethodCall("transferirDinheiro", new Object[] { transferencia },
					new Class[] { Transferencia.class });
			System.out.println("Enviando mensagem pra geral");
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo,
					new RequestOptions(ResponseMode.GET_ALL, 2000));
			ArrayList<Address> membros = new ArrayList<Address>();
			int erros = 0;
			String msg = "";
			System.out.println(rsp);
			
			for (Entry<Address, Rsp<User>> user : rsp.entrySet()) { //iterando as respostas dos membros
				if(!user.getValue().wasReceived()){
					System.out.println("Membro não recebeu: " + user.getKey());
					erros++;
					cluster.getDispatcher().callRemoteMethod(user.getKey(), "desconectar",null,null,new RequestOptions(ResponseMode.GET_NONE,2000)); //expulsa o membro pra ele ressincronizar
				}else if(user.getValue().getValue().getErro() != null){ //membro recebeu mas deu erro 
					System.out.println("Erro " + user.getKey() + ": " + user.getValue().getValue().getErro());
					msg = user.getValue().getValue().getErro();
					erros++;
				} else {
					System.out.println("Sem erro: " + user.getKey());					
				}
				membros.add(user.getKey());
			}
			System.out.println(String.format("%d com erros",erros));

			if (erros > 0) {
				// reverte
				System.out.println("Revertendo transferência...");
				cluster.getDispatcher().callRemoteMethods(membros,
					"rollback",
					new Object[]{estadoAtual},
					new Class[]{State.class}, 
					new RequestOptions(ResponseMode.GET_NONE,2000));
				//todos deram erro,possivelmente possuem uma mensagem de erro em comum (como insuficiencia de saldo)
				if(erros == rsp.size()){
					throw new Exception(msg);
				}
				throw new Exception("Serviço indisponível,tente novamente mais tarde!");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			origem.setErro(e.getMessage());
		}
		System.out.println("---------------------------------------");
		return origem;
	}

	public ArrayList<Transferencia> obterExtrato(User user) throws RemoteException {
		System.out.println(String.format("Usuário %s consultando extrato", user.getNome()));
		return ContaController.obterExtrato(user);
	}

	public int consultarMontante(){
		try {
			State estadoAtual = new State();
		} catch (Exception e) {
			// TODO: handle exception
		}


		return 0;
	}

}
