/* 
Classname: CalculadoraClient 
Proposito: O cliente RMI

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


public class Client { 
	
	public static void main(String args[]) { 

		API objetoRemoto = null; 

		try { 

			// Localiza o objeto remoto, através do nome cadastrado no registro RMI do localhost
			objetoRemoto = (API) Naming.lookup("rmi://localhost/calc"); 
			
			// Localiza o objeto remoto, através do nome cadastrado no registro RMI em outra máquina
			//objetoRemoto = (CalculadoraAPI) Naming.lookup("rmi://172.22.70.30:1099/calc"); 
			
			//Invoca os métodos remotos disponibilizados pela interface CalculadoraAPI
			System.out.println( "Client >> Recebido do servidor RMI: 3 + 2 = " + objetoRemoto.soma(3,2)       ); 
			System.out.println( "Client >> Recebido do servidor RMI: 3 - 2 = " + objetoRemoto.subtrai(3,2)    ); 
			System.out.println( "Client >> Recebido do servidor RMI: 3 * 2 = " + objetoRemoto.multiplica(3,2) ); 
			System.out.println( "Client >> Recebido do servidor RMI: 3 / 2 = " + objetoRemoto.divide(3,2)     ); 

		} 
		catch (Exception erro) { 
			//DEBUG
			System.out.println("ERRO: Client " + erro.getMessage()); 
			erro.printStackTrace(); 
		} 
	} 
}

/* 

@depecrated Atualmengte o compilador javac já invoca a compilação dos apêndices pelo rmic

Para criar o sistema RMI todos os arquivos tem que estar compilados. Em seguida, o skeleton e stub, 
que são mecanismos de comunicação padrão com objetos remotos, são criadas com o compilador rmic. 

Após todos os arquivos estiverem compilados (CalculadoraClient, CalculadoraServer e CalculadoraAPI), 
executando o seguinte comando irá criar o stub e o skeleton: 

	rmic CalculadoraServer

Dois apêndices serão criadas, CalculadoraServer_Stub.class e CalculadoraServer_Skel.class, 
onde o primeiro representa o lado do cliente do sistema RMI e o segundo representa o lado do 
servidor do sistema RMI.

*/