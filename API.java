/* 
Classname: Calculadora 
Proposito: A interface remota

Implementação de um simples sistema RMI

Este é um sistema de RMI simples com um cliente e um servidor. 
O servidor contém os métodos descritos na interface Calculadora, 
que retornam para o cliente o valor calculado pelo servidor.

Este sistema RMI contém os seguintes arquivos:

- Calculadora.java: 	  interface remota.
- CalculadoraClient.java: aplicação cliente no sistema RMI.
- CalculadoraServer.java: aplicativo de servidor no sistema RMI.

*/

import java.rmi.*; 

public interface API extends Remote { 

	int soma(int a, int b)       throws RemoteException; 

	int subtrai(int a, int b)    throws RemoteException; 
	
	int multiplica(int a, int b) throws RemoteException; 
	
	int divide(int a, int b)     throws RemoteException; 

}
