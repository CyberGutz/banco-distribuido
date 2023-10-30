
import java.rmi.*;
import java.util.Scanner;

import org.json.JSONObject;

import Models.User;

public class Client {

	public static Scanner scanner = new Scanner(System.in);
	public static API objetoRemoto = null;

	public static void main(String args[]) {

		try {
			// Localiza o objeto remoto, através do nome cadastrado no registro RMI 
			objetoRemoto = (API) Naming.lookup("rmi://localhost/calc");
			MenuPrincipal(BemVindo());
		} catch (Exception erro) {
			// DEBUG
			System.out.println("ERRO: Client " + erro.getMessage());
			erro.printStackTrace();
		}
	}

	public static User BemVindo() {

		int op = 0;
		String usuario, senha;
		User user = null;

		while (op == 0) {

			try {

				System.out.println("--- Bem-Vindo! ---");
				System.out.println("1- Criar Conta");
				System.out.println("2- Fazer Login");
				System.out.println("------------------");
				System.out.print("Selecione a opcao desejada: ");
				op = scanner.nextInt();
				scanner.nextLine(); //pega \n

				System.out.print("Informe o usuario: ");
				usuario = scanner.nextLine();

				System.out.print("Informe a senha: ");
				senha = scanner.nextLine();

				switch (op) {
					case 1: {
						user = objetoRemoto.criarConta(usuario, senha);
						if (user.getErro() != null) {
							throw new Exception(user.getErro());
						} else {
							System.out.println("Conta criada com sucesso!");
							return user;
						}
					}
					case 2: {
						user = objetoRemoto.fazerLogin(usuario, senha);
						if (user.getErro() != null) {
							throw new Exception(user.getErro());
						} else {
							System.out.println("Login efetuado com sucesso, bem-vindo de volta!");
							return user;
						}
					}
					default: {
						op = 0;
						System.out.println("Opção Inválida !");
					}
				}

			} catch (Exception e) {
				op = 0;
				System.out.println("Houve um erro ao tentar prosseguir: " + e.getMessage() + ". Tente novamente.");
			}

		}
		return null;
	}

	public static void MenuPrincipal(User user) throws RemoteException {
		int op = -1;

		while (op != 0) {
			try {
				System.out.println("--- MENU PRINCIPAL ---");
				System.out.println("Olá " + user.getNome());			
				System.out.println("Conta: " + user.getConta()+"\n");
				System.out.println("0- Sair");
				System.out.println("1- Ver Saldo");
				System.out.println("2- Transferir dinheiro");
				System.out.println("3- Ver Extrato");
				System.out.println("------------------");
				System.out.print("Selecione a opcao desejada: ");
				op = scanner.nextInt();

				switch (op) {
					case 0: {
						break;
					}
					case 1: {
						user = objetoRemoto.verSaldo(user);
						if(user.getErro() != null) throw new Exception("Erro ao consultar saldo: " + user.getErro());
						System.out.println("Seu saldo: R$" + user.getCreditos());
						break;
					}
					case 2: {
						System.out.println("Digite a conta de destino: ");
						int contaDestino = scanner.nextInt();
						System.out.println("Insira o valor: ");
						double valor = scanner.nextDouble();
						scanner.nextLine();

						User destino = new User("",contaDestino);

						if(contaDestino == user.getConta()){
							System.out.println("Não é necessário transferir dinheiro para a sua própria conta");
							break;
						}

						if(destino.getUserDB() != null){
							System.out.println("---Dados da conta destino e transferência---");
							System.out.println("Nome: " + destino.getNome());
							System.out.println("Valor da transferência: R$" + valor);
							System.out.println("Gostaria de continuar com a transferência ? (S/N): ");
							String opT = scanner.nextLine();
							System.out.println(opT);
							if(opT.equals("S") || opT.equals("s")){
								user = objetoRemoto.transferirDinheiro(user,destino,valor);
								if(user.getErro() != null) throw new Exception("Erro ao transferir dinheiro: " + user.getErro());
								System.out.println("Transferência realizada com sucesso !");
							}
						}else{
							System.out.println("Conta não encontrada, tente novamente");
							break;
						}
						break;
					}
					case 3: {
						// ver extrato
						break;
					}
					default: {
						op = -1;
						System.out.println("Opção Inválida !");
					}
				}
			} catch (Exception e) {
				op = -1;
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

		}
	}

}