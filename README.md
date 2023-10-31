
## Abra um terminal e execute (para plataforma windows):
./run-windows

## Abra um terminal e execute (para plataforma Linux):
./run-linux

### Demais instruções de argumentos nos scripts estão no help oferecido pelos mesmos (basta executá-lo passando o argumento "-h")

## A interface remota

Uma interface remota é definida pela extensão da interface Remote que está no pacote java.rmi. 
A interface que declara os métodos que os clientes podem invocar a partir de uma máquina virtual 
remoto é conhecido como interface remota. A interface remota deve satisfazer as seguintes condições:
- Deve estender-se a interface Remote.
- Cada declaração de método na interface remota deve incluir a exceção RemoteException 
  (ou uma de suas superclasses), em sua cláusula lançada.

## A classe RemoteObject

Funções do servidor RMI são fornecidos pela classe RemoteObject e suas subclasses Remote Server, 
Activatable e UnicastRemoteObject. Aqui está uma breve descrição de como lidar com as diferentes classes:

- RemoteObject fornece implementações dos métodos toString, equals e hashCode na classe java.lang.Object.
- As classes UnicastRemoteObject e Activatable cria objetos remotos e os exporta, ou seja, 
  essas classes fazem os objetos remotos usados por clientes remotos.

## A classe RemoteException

A classe RemoteException é uma super-classe das exceções que o sistema RMI joga durante uma invocação 
de método remoto. Cada método remoto que é declarado em uma interface remota deve especificar 
RemoteException (ou uma de suas superclasses), em sua cláusula throws para garantir a robustez das 
aplicações no sistema RMI.

Quando uma chamada de método remoto tiver um erro, a exceção RemoteException é lançada. 
Falha de comunicação, erros de protocolo e falha durante a triagem ou unmarshalling de parâmetros 
ou valores de retorno são algumas das razões para o fracasso da comunicação RMI. RemoteException 
é uma exceção que deve ser tratada pelo método chamador. O compilador confirma que o programador 
de ter lidado com essas exceções.

## Requisitos Funcionais

 - Login
 - Cadastro
  - Visualização de Saldo
 -  Transferência de Dinheiro
 - Ver Extrato

## Requisitos Não Funcionais

...