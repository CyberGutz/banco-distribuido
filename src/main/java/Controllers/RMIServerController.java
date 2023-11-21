package Controllers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

import Server.Server;

public class RMIServerController extends Thread {

    public void run() {

        final String ip = "239.1.1.1";
        final int port = 9000;

        // Hook que invoca o desligamento do registry quando o programa é encerrado
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                UnicastRemoteObject.unexportObject(java.rmi.registry.LocateRegistry.getRegistry(1099), true);
            } catch (Exception e) {
            }
        }));

        try {

            // criando socket UDP multicast
            MulticastSocket socket = new MulticastSocket(port);
            InetAddress addr = InetAddress.getByName(ip);
            socket.joinGroup(addr);

            while (true) {

                byte[] buffer = new byte[256];
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length, addr, port);
                System.out.println("Aguardando receber pedido de stub");

                socket.receive(pacote);

                String msg = new String(pacote.getData(), 0, pacote.getLength());
                System.out.println("Mensagem recebida: " + msg);

                if (msg.equals("rmiclient")) {

                    Server obj = new Server();
                    try {
                        java.rmi.registry.LocateRegistry.getRegistry(1099);
                        System.out.println("Pegando serviço registry já criado");
                        Naming.rebind("rmi://localhost/banco", obj);
                    } catch (Exception e) {
                        System.out.println("Criando registry");
                        java.rmi.registry.LocateRegistry.createRegistry(1099);
                        Naming.bind("rmi://localhost/banco", obj);
                    }

                    msg = String.format("rmi://%s/banco", InetAddress.getLocalHost().getHostAddress());
                    System.out.println("Retornando stub: " + msg);

                    byte[] bufferSend = msg.getBytes();
                    pacote = new DatagramPacket(bufferSend, bufferSend.length, addr, port);
                    socket.send(pacote);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro rmiserver: " + e.getMessage());
        }

    }

}
