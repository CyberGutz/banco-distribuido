
import java.rmi.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

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

	public static String BemVindo() {

		int op = 0;
		String usuario, senha;
		Map<String, String> token = new HashMap<String, String>();

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
						token = objetoRemoto.criarConta(usuario, senha);
						if (token.containsKey("erro")) {
							throw new Exception(token.get("erro"));
						} else {
							System.out.println("Conta criada com sucesso!");
							return token.get("token");
						}
					}
					case 2: {
						token = objetoRemoto.fazerLogin(usuario, senha);
						if (token.containsKey("erro")) {
							throw new Exception(token.get("erro"));
						} else {
							System.out.println("Login efetuado com sucesso, bem-vindo de volta!");
							return token.get("token");
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
		return "";
	}

	public static void MenuPrincipal(String token) {
		int op = -1;
		while (op != 0) {
			System.out.println("--- MENU PRINCIPAL ---");
			System.out.println("0- Sair");
			System.out.println("1- Ver Saldo");
			System.out.println("2- Ver Extrato");
			System.out.println("3- Transferir dinheiro");
			System.out.println("------------------");
			System.out.print("Selecione a opcao desejada: ");
			op = scanner.nextInt();

			switch (op) {
				case 0: {
					break;
				}
				case 1: {
					// ver saldo
					break;
				}
				case 2: {
					// ver extrato
					break;
				}
				case 3: {
					// transferir dinheiro
					break;
				}
				default: {
					op = -1;
					System.out.println("Opção Inválida !");
				}
			}

		}
	}

}
