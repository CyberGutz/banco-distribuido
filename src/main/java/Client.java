import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import Models.Transferencia;
import Models.User;
import Server.API;
import Server.Server;

public class Client {

	public static Scanner scanner = new Scanner(System.in);
	public static API objetoRemoto = null;

	public static void main(String args[]) {

		try {
			// Localiza o objeto remoto, através do nome cadastrado no registro RMI
			String nomeServer = null;
			// while (nomeServer == null) {
				// Thread.sleep(2000);
			nomeServer = obterNomeServidor();
			// }
			objetoRemoto = (API) Naming.lookup(nomeServer);
			MenuPrincipal(BemVindo());
		} catch (Exception erro) {
			System.out.println("ERRO: Client " + erro.getMessage());
			erro.printStackTrace();
		}
	}

	public static String obterNomeServidor() throws IOException {

		MulticastSocket socket = null;
		InetAddress addr = null;

		try {

			// criando socket UDP multicast
			socket = new MulticastSocket(9000);
			InetAddress.getByName("239.1.1.1");
			socket.joinGroup(addr);

			byte[] bufferSend = "rmiclient".getBytes();
			DatagramPacket pacote = new DatagramPacket(bufferSend, bufferSend.length);
			System.out.println(new String(pacote.getData(), 0, pacote.getLength()).toString());
			System.out.println("Enviando mensagem");
			socket.send(pacote);
			System.out.println("Mandou mensagem");

			byte[] buffer = new byte[256];
			pacote = new DatagramPacket(buffer, buffer.length);
			System.out.println("Esperando chegar mensagem");
			socket.receive(pacote);

			String msg = new String(pacote.getData(), 0, pacote.getLength());

			System.out.println("Mensagem recebida: " + msg);

			if (msg != "rmiclient") {
				socket.leaveGroup(addr);
				socket.close();
				return msg;
			}

		} catch (Exception e) {
			System.out.println("Erro ao receber stub: " + e.getMessage());
			e.printStackTrace();
		} finally {
			socket.leaveGroup(addr);
			socket.close();
		}
		return null;
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
				scanner.nextLine(); // pega \n

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
				System.out.println("Conta: " + user.getConta() + "\n");
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
						if (user.getErro() != null)
							throw new Exception("Erro ao consultar saldo: " + user.getErro());
						System.out.println("Seu saldo: R$" + user.getCreditos());
						break;
					}
					case 2: {

						System.out.println("Digite a conta de destino: ");
						int contaDestino = scanner.nextInt();
						System.out.println("Insira o valor: ");
						double valor = scanner.nextDouble();

						User destino = new User("", contaDestino);

						if (contaDestino == user.getConta()) {
							System.out.println("Não é necessário transferir dinheiro para a sua própria conta");
							break;
						}

						if (destino.getUserDB(true) != null) {
							System.out.println("---Dados da conta destino e transferência---");
							System.out.println("Nome: " + destino.getNome());
							System.out.println("Valor da transferência: R$" + valor);
							System.out.println("Gostaria de continuar com a transferência ? (S/N): ");
							scanner.nextLine();
							String opT = scanner.nextLine();
							System.out.println(opT);
							if (opT.equals("S") || opT.equals("s")) {
								user = objetoRemoto.transferirDinheiro(user, destino, valor);
								if (user.getErro() != null)
									throw new Exception("Erro ao transferir dinheiro: " + user.getErro());
								System.out.println("Transferência realizada com sucesso !");
							}
						} else {
							System.out.println("Conta não encontrada, tente novamente");
							break;
						}
						break;
					}
					case 3: {
						final User userTransf = user; // bkp do usuario para que eu possa usar o mesmo como comparação
														// no forEach
						ArrayList<Transferencia> transferencias = objetoRemoto.obterExtrato(userTransf);
						System.out.println(String.format("------- Extrato Bancario %s --------", user.getNome()));
						if (transferencias.isEmpty()) {
							System.out.println("Nenhuma transferencia para esta conta foi encontrada");
						} else {
							System.out.println("##################################");
							transferencias.forEach(transferencia -> {
								if (transferencia.getUserOrigem().getConta() == userTransf.getConta()) {
									System.out.println("Enviou para: " + transferencia.getUserDestino().getNome()
											+ " - Conta " + transferencia.getUserDestino().getConta());
								} else {
									System.out.println("Recebeu de: " + transferencia.getUserOrigem().getNome()
											+ " - Conta " + transferencia.getUserOrigem().getConta());
								}
								System.out.println("Valor da transferência: " + transferencia.getValor());
								System.out.println("Data da transferência: "
										+ new SimpleDateFormat("dd/MM/yyyy").format(transferencia.getData()) + " às "
										+ new SimpleDateFormat("HH:mm:ss").format(transferencia.getData()));
								System.out.println("##################################");
							});
						}
						System.out.println("----------------------------------------------------");
						break;
					}
					default: {
						op = -1;
						System.out.println("Opção Inválida !");
					}
				}
			} catch (InputMismatchException e) {
				op = -1;
				scanner.nextLine();
				System.out.println("Entrada inválida ! Tente novamente");
			} catch (Exception e) {
				op = -1;
				scanner.nextLine();
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

		}
	}

}