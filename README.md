# Trabalho para a Disciplina de Sistemas Distribuídos 1

# Membros:

- Alberto Gusmão Cabral Junior 
- Gustavo Teixeira Magalhaes
- Henrique Fugie de Macedo

## Compilando o projeto

Caso queira compilar e ver os arquivos resultantes por conta própria, é necessário utilizar o maven para que seja feito o download das dependências, após a instalação do maven, digite o comando:

    mvn clean install

Navegue dentro da pasta target/classes e você encontrará os respectivos arquivos compilados.

A classe cliente e/ou servidor podem ser executadas pelo jar gerados dentro da pasta target com o seguinte comando (você deve estar dentro da pasta no seu terminal):

    java -jar cliente.jar


# Agora... caso queira uma forma mais rápida de executar o programa:

Abra um terminal e execute (para plataforma windows):

    ./run-windows


 Abra um terminal e execute (para plataforma Linux):
    
    ./run-linux

### Demais instruções de argumentos nos scripts estão no help oferecido pelos mesmos (basta executá-lo passando o argumento "-h"):

      ./run-linux -h ou ./run-windows -h

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
- Transferência de Dinheiro (saldo não pode ficar negativo)
- Ver Extrato

## Requisitos Não Funcionais Básicos

- Definição da pilha de protocolos JGROUPS (.XML)
- Um relatório apresentando a arquitetura do sistema, explicando e  justificando as principais decisões do projeto.
Justificando a escolha dos protocolos, análise de desempenho da pilha de protocolos (tempo gasto, msgs por segundo e vaãzão do text com o MPerf configurado para 100k msgs).
Apontando os pontos fortes e fracos da solução.
- Distribuição vertical (MVC)
- Distribuição Horizontal (replicação de componentes em cada camada)
O sistema deverá ser tolerante a falhas, permanecendo operante mesmo se membros do cluster falharem.
- Novos membros do cluster devem receber uma transferencia de estado. Multicast confiável e com ordenação das mensagens.

## Requisitos Não Funcionais Intermediários
- O valor do montante de dinheiro do banco deve ser visível e consistente em todos os membros do cluster.
- O sistema distribuído deve utilizar mais de um canal (JChannel) ou preferencialmente subcanais
(ForkChannel) de comunicação para devidamente implementar a distribuição vertical (ex.: clusters
“modelo”, “controle”, “visão”), segmentando as mensagens trocadas conforme a função de cada
componente;
- O sistema distribuído deve providenciar armazenamento em disco do estado do sistema (cadastros,
movimentações, etc.) em memória secundária (persistente);
- No reingresso de um membro ao cluster, deverá ser obtido o estado do sistema, ou seja, atualizações
que ele possa não ter recebido enquanto estava desconectado;
- O sistema deverá prover mecanismos de segurança, como criptografia das mensagens trocadas,
autenticação dos usuários e autenticidade das solicitações.

