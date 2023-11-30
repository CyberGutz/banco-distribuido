package Controllers;

import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;

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
            String meuIP = obterIP();
            //sobrescrevendo o nome do hostname do rmi senao ao realizar o lookup, o IP sai como e endereço de loopback
            //isso pode ser problematica caso o cliente esteja em outra maquina, tal que o IP do coordenador não é o localhost
            System.setProperty("java.rmi.server.hostname", meuIP);
            Server obj = new Server();
            System.out.println("IP do Registry: " + meuIP);
            String rmiAddr = String.format("rmi://%s/banco", meuIP);
            try {
                java.rmi.registry.LocateRegistry.getRegistry(1099);
                System.out.println("Pegando serviço registry já criado");
                Naming.rebind(rmiAddr, obj);
            } catch (Exception e) {
                System.out.println("Criando registry");
                java.rmi.registry.LocateRegistry.createRegistry(1099);
                Naming.bind(rmiAddr, obj);
            }

            byte[] bufferSend = rmiAddr.getBytes();
            DatagramPacket stub = new DatagramPacket(bufferSend, bufferSend.length, addr, port);

            while (true) { //loop eterno que aguarda mensagem dos clientes pedindo o stub

                // System.out.println("Aguardando receber pedido de stub");
                byte[] buffer = new byte[256];
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length, addr, port);
                socket.receive(pacote);

                String msg = new String(pacote.getData(), 0, pacote.getLength());
                System.out.println("---------------------------------------");
                System.out.println("Mensagem recebida: " + msg);

                if (msg.equals("rmiclient")) {
                    System.out.println("Retornando stub: " + rmiAddr);
                    System.out.println("---------------------------------------");
                    socket.send(stub);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro rmiserver: " + e.getMessage());
        }

    }

    public static String obterIP() throws SocketException {
        String ip = null;
        for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
                .hasMoreElements();) {
            NetworkInterface iface = ifaces.nextElement();
            if (iface.getName().equals("eno1") || iface.getName().contains("eth")) {
                //procurando a interface ethernet POSSIVELMENTE adequeada
                // System.out.println(iface.getName() + ":");
                for (Enumeration<InetAddress> addresses = iface.getInetAddresses(); addresses.hasMoreElements();) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet6Address) //nao quero ipv6
                        continue;
                    ip = address.getHostAddress();
                    // System.out.println(" " + ip);
                }
            }
        }
        return ip;
    }

}
