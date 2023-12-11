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
import org.jgroups.util.Util;

import Controllers.ClusterController;
import Models.State;
import Models.Transferencia;
import Models.User;

public class Server extends UnicastRemoteObject implements API {

	static ClusterController cluster;
	static int timeout = 8000;

	public Server() throws RemoteException {
		super(); // invoca o construtor do UnicastRemoteObject
	}

	public static void main(String args[]) throws Exception {
		String meuIP = ClusterController.obterIP();
		cluster = new ClusterController(new JChannel("protocolos.xml"),meuIP);
		// rodando o serviço
		cluster.bancoServer();
	}

	// RPC - Operações ------------------------------------------------------------
	public User criarConta(String usuario, String senha) throws RemoteException {
		User retorno = null;
		try {
			State estadoAtual = new State(cluster);
			System.out.println(String.format("Usuário pedindo criação de conta. Usuario %s Senha %s", usuario, senha));
			MethodCall metodo = new MethodCall("criarConta", new Object[] { usuario, senha },
					new Class[] { String.class,String.class });
			RequestOptions opcoes = new RequestOptions(ResponseMode.GET_ALL, timeout);
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, opcoes);
			retorno = processarRespostas(rsp, estadoAtual);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			retorno.setErro(e.getMessage());
		}
		return retorno;
	}

	public User fazerLogin(String usuario, String senha) throws RemoteException {
		User retorno = null;
		try {
			State estadoAtual = new State(cluster);
			System.out.println(estadoAtual);
			System.out.println(String.format("Usuário pedindo login. Usuario %s Senha %s", usuario, senha));
			MethodCall metodo = new MethodCall("fazerLogin", new Object[] { usuario, senha },
					new Class[] { String.class,String.class });
			RequestOptions opcoes = new RequestOptions(ResponseMode.GET_ALL, timeout);
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, opcoes);
			retorno = processarRespostas(rsp, estadoAtual);
			if(retorno.getErro() == null){ 
				//manda todo mundo adicionar o usuario como logado na instancia deles
				cluster.getDispatcher().callRemoteMethods(null, "setUsuarioLogado",new Object[]{retorno},null,new RequestOptions(ResponseMode.GET_NONE,1000));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			retorno.setErro(e.getMessage());
		}
		return retorno;
	}

	public User consultarConta(User conta) throws RemoteException {
		try {
			MethodCall metodo = new MethodCall("consultarConta", new Object[] { conta },
					new Class[] { User.class });
			System.out.println("Consultando conta pra geral");
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo,
					new RequestOptions(ResponseMode.GET_ALL, timeout));
			ArrayList<Address> problematicos = new ArrayList<Address>();
			for (Entry<Address, Rsp<User>> user : rsp.entrySet()) { // iterando as respostas dos membros
				if (user.getValue().wasReceived()) {
					if (user.getValue().getValue().getErro() == null) { // algum membro nao tem a conta consultada
						if(conta.getVersao() <= user.getValue().getValue().getVersao()){
							conta = user.getValue().getValue();
						}else{
							problematicos.add(user.getKey());
						}
					} else {
						problematicos.add(user.getKey());
					}
				}
			}
			//mando os desatualizados ressincronizarem
			cluster.getDispatcher().callRemoteMethods(problematicos, "desconectar", null, null,
					new RequestOptions(ResponseMode.GET_NONE, timeout));
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
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo,
					new RequestOptions(ResponseMode.GET_ALL, timeout));
			ArrayList<Address> problematicos = new ArrayList<Address>();
			for (Entry<Address, Rsp<User>> userRsp : rsp.entrySet()) { // iterando as respostas dos membros
				if (!userRsp.getValue().wasReceived()) {
					System.out.println("Membro não recebeu: " + userRsp.getKey());
				} else if (userRsp.getValue().getValue().getErro() != null) { // membro recebeu mas deu erro
					problematicos.add(userRsp.getKey());
				} else {
					System.out.println("Sem erro: " + userRsp.getKey());
					if (userRsp.getValue().getValue().getVersao() >= user.getVersao()) { // obtendo o saldo mais
						// atualizado possível
						user = userRsp.getValue().getValue();
					} else { // algum membro ta com a versão desatualizada
						problematicos.add(userRsp.getKey());
					}
				}
			}
			//mando os desatualizados ressincronizarem
			cluster.getDispatcher().callRemoteMethods(problematicos, "desconectar", null, null,
					new RequestOptions(ResponseMode.GET_NONE, timeout));
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
			State estadoAtual = new State(cluster); // fazendo um backup do estado atual
			Transferencia transferencia = new Transferencia(origem, destino, valor);
			MethodCall metodo = new MethodCall("transferirDinheiro", new Object[] { transferencia },
					new Class[] { Transferencia.class });
			Util.sleep(5000);
			RspList<User> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo,
					new RequestOptions(ResponseMode.GET_ALL, timeout));		
			origem = processarRespostas(rsp, estadoAtual);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			origem.setErro(e.getMessage());
		}
		System.out.println("---------------------------------------");
		return origem;
	}

	public ArrayList<Transferencia> obterExtrato(User user) throws RemoteException {
		System.out.println(String.format("Usuário %s consultando extrato", user.getNome()));
		try{
			MethodCall metodo = new MethodCall("obterExtrato", new Object[] {user}, new Class[] {User.class});
			RspList<ArrayList<Transferencia>> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, new RequestOptions(ResponseMode.GET_FIRST,timeout));
			return rsp.getFirst();
		} catch (Exception e){
			System.out.println("Erro ao consultar extrato: " + e);
			return null;
		}
	}

	public Double obterMontante(){
		Double montante = 0.0;
		Double erro = -1.0;
		
		try {
			MethodCall metodo = new MethodCall("obterMontante", null, null);
			System.out.println("Obtendo montante");
			RspList<Double> rsp = cluster.getDispatcher().callRemoteMethods(null, metodo, new RequestOptions(ResponseMode.GET_ALL, timeout));
			System.out.println(rsp);
			ArrayList<Address> problematicos = new ArrayList<Address>();
			for (Entry<Address, Rsp<Double>> monte: rsp.entrySet()){
				if(monte.getValue().wasReceived()){
					Double temp = monte.getValue().getValue();
					if(temp != -1.0){
						System.out.println("Achou montante: " + temp);
						if(montante < temp){
							montante = temp;
						}else{//retornou montante menor, há um dessincronismo aí
							problematicos.add(monte.getKey());
						}
					}else{
						problematicos.add(monte.getKey());
					}
				}
			}
			//mando os desatualizados ressincronizarem
			cluster.getDispatcher().callRemoteMethods(problematicos, "desconectar", null, null,
					new RequestOptions(ResponseMode.GET_NONE, timeout));
		} catch (Exception e) {
			System.out.println("Erro ao consultar montante: " + e);
			return erro;
		}
		return montante;
	}
	
	private static User processarRespostas(RspList<User> rsp, State estadoAtual) {
		ArrayList<Address> membrosSemErros = new ArrayList<Address>();
		ArrayList<Address> membrosComErros = new ArrayList<Address>();
		int erros = 0;
		int semErros = 0;
		User retorno = null;
		User erro = null;
		try {

			for (Entry<Address, Rsp<User>> user : rsp.entrySet()) {
				if (!user.getValue().wasReceived()) {
					System.out.println("Membro não recebeu" + user.getKey());
				}
				if (user.getValue().getValue().getErro() == null) {
					semErros++;
					System.out.println("Sem Erro: " + user.getKey());
					membrosSemErros.add(user.getKey());
					retorno = user.getValue().getValue();
				} else {
					erros++;
					erro = user.getValue().getValue();
					System.out.println("Erro " + user.getKey() + ": " + erro.getErro());
					membrosComErros.add(user.getKey());
				}
			}

			if (erros > 0) {
				if (erros == rsp.size()) {// todos deram erro
					retorno = erro;
					throw new Exception(retorno.getErro());
				}
				if (erros > semErros && semErros > 0) { // A maioria deu erro
					System.out.println("Revertendo...");
					cluster.getDispatcher().callRemoteMethods(membrosSemErros,
							"rollback",
							new Object[] { estadoAtual },
							new Class[] { State.class },
							new RequestOptions(ResponseMode.GET_NONE, timeout));
				} else { // a minoria deu erro, manda esse povo ressincronizar
					System.out.println("Expulsando processos falhos");
					cluster.getDispatcher().callRemoteMethods(membrosComErros, "desconectar", null, null,
							new RequestOptions(ResponseMode.GET_NONE, timeout)); // expulsa o membro pra ele ressincronizar
				}
				throw new Exception("Serviço indisponível,tente novamente mais tarde!");
			}
		} catch (Exception e) {
			retorno.setErro(e.getMessage());
		}
		return retorno;
	}

	public boolean logout(User user){
		try {
			MethodCall metodo = new MethodCall("logout", new Object[]{user}, new Class[]{User.class});
			cluster.getDispatcher().callRemoteMethods(null, metodo, new RequestOptions(ResponseMode.GET_ALL, timeout));
			return true;
		} catch (Exception e) {
			System.out.println("Erro ao terminar login: " + e);
			return false;
		}
	}

}
