/* 
Classname: CalculadoraServer 
Proposito: Implementacao do servidor RMI

Implementação de um simples sistema RMI

Este é um sistema de RMI simples com um cliente e um servidor. 
O servidor contém os métodos descritos na interface CalculadoraAPI, 
que retornam para o cliente o valor calculado pelo servidor.

Este sistema RMI contém os seguintes arquivos:

- CalculadoraAPI.java: 	  interface remota.
- CalculadoraClient.java: aplicação cliente no sistema RMI.
- CalculadoraServer.java: aplicativo de servidor no sistema RMI.

*/

import java.rmi.*; 
import java.rmi.server.*; 

public class Server extends UnicastRemoteObject implements API { 

	public static void main(String args[]) { 
		try { 

			// Cria um objeto do tipo da classe CalculadoraServer. 
			Server obj = new Server(); 

			// Liga (bind) esta instancia de objeto ao nome "calc" no registro RMI no localhost
			Naming.rebind("rmi://localhost/calc", obj); 
			
			// Liga (bind) esta instancia de objeto ao nome "Calculadora" em um registro RMI contido em outra máquina
			//Naming.rebind("rmi://172.22.70.30:1099/calc", obj); 

			//DEBUG
			System.out.println("Server >> ligado no registro RMI sob o nome 'calc' "); 

		} 
		catch (Exception erro) { 
			//DEBUG
			System.out.println("ERRO: Server " + erro.getMessage()); 
			erro.printStackTrace(); 
		} 

	}

	//Construtor do objeto
	public Server() throws RemoteException { 
		super(); //invoca o construtor do UnicastRemoteObject
	} 

	//Implementa os métodos remotos disponibilizados pela interface Calculadora
	public int soma(int a, int b) throws RemoteException{
		return (a + b);
	}
	public int subtrai(int a, int b) throws RemoteException{
		return (a - b);
	}
	public int multiplica(int a, int b) throws RemoteException{
		return (a * b);
	}
	public int divide(int a, int b) throws RemoteException{
		return (a / b);
	}

}

/* 

@depecrated Atualmengte o compilador javac já invoca a compilação dos apêndices pelo rmic

Para criar o sistema RMI todos os arquivos tem que estar compilados. Em seguida, o skeleton e stub, 
que são mecanismos de comunicação padrão com objetos remotos, são criadas com o compilador rmic. 

Após todos os arquivos estiverem compilados (CalculadoraClient, CalculadoraServer e Calculadora), 
executando o seguinte comando irá criar o stub e o skeleton: 

	rmic CalculadoraServer

Dois apêndices serão criadas, CalculadoraServer_Stub.class e CalculadoraServer_Skel.class, 
onde o primeiro representa o lado do cliente do sistema RMI e o segundo representa o lado do 
servidor do sistema RMI.

*/