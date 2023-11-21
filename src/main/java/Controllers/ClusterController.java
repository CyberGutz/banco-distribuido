package Controllers;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Util;

import Models.State;
import Models.User;

public class ClusterController implements Receiver {

    private JChannel channel;
    private RpcDispatcher dispatcher;
	static final int TAMANHO_MINIMO_CLUSTER = 1;
    private RMIServerController rmiServer;
    private boolean eraCoordenador = false;


    public ClusterController(JChannel channel){
        this.channel = channel;
        this.conectarNoCanal();
        if(!this.souCoordenador()){
            this.obterEstado();    
        }
        //loop serviço principal
        this.bancoServer();
    }

	private void bancoServer() {

		while(this.channel.getView().size() < TAMANHO_MINIMO_CLUSTER){
			Util.sleep(1000);
		}

		if (souCoordenador()) {
            eraCoordenador = true;
            rmiServer = new RMIServerController();
			rmiServer.start();
		}

		while (true) {

		}

	}

    //-- Métodos de Gerenciamento do Grupo
    public void viewAccepted(View view){
        System.out.println("View ");
        if(souCoordenador()){
            if(!this.eraCoordenador){
                eraCoordenador = true;
                rmiServer = new RMIServerController();
                rmiServer.start();
                System.out.println("Virei o novo coordenador");
            }
        }else{ //geral vai pedir o novo estado pro coordenador
            System.out.println("Composição mudou, vou pedir estado pro ademir.");
            eraCoordenador = false;
            this.obterEstado();
        }
    }
    
    // public void receive(Message msg){
        // System.out.println("Mensage braba: " + msg.getSrc());
    // }

    public void getState(OutputStream output){

        try {
                State state = new State();
                
                File file = new File("users.json");
                System.out.println("getState()" + file.getAbsolutePath());
                
                BufferedInputStream bfis = new BufferedInputStream(new FileInputStream(file));
                
                state.setUsers(bfis.readAllBytes());
                bfis.close();
                
                file = new File("transferencias.json");
                bfis = new BufferedInputStream(new FileInputStream(file));
                
                state.setTransferencias(bfis.readAllBytes());
                bfis.close();

                //escreve na saída do pedinte do estado
                Util.objectToStream(state, new DataOutputStream(output));
                System.out.println("Estado enviado.");
            } 
        catch (FileNotFoundException e) {
            System.out.println("Arquivo(s) não encontrado(s) ao transferir estado: " + e.getMessage());
            this.channel.disconnect();
            this.conectarNoCanal();
        } catch (IOException e) {
            System.out.println("Erro ao enviar estado: " + e.getMessage());
        }

    }

    public void setState(InputStream input) {
       
        try {
                State state = (State)Util.objectFromStream(new DataInputStream(input));
                
                BufferedOutputStream bfos = new BufferedOutputStream(new FileOutputStream(new File("users.json")));

                bfos.write(state.getUsers());
                bfos.flush();
                bfos.close();

                bfos = new BufferedOutputStream(new FileOutputStream(new File("transferencias.json")));
                bfos.write(state.getTransferencias());
                bfos.flush();
                bfos.close();
                System.out.println("Leu o estado do coordenador: " + this.channel.view().getCoord());
            } 
        catch (IOException e) {
            System.out.println("Erro ao ler estado: " + e.getMessage());
            this.obterEstado();
        } catch (ClassNotFoundException e) {
            System.out.println("Não foi possivel obter a classe do Estado: " + e.getMessage());
            this.obterEstado();
        }


    }
    // ----------------------------------------------------------------------------

    // Métodos da Aplicação -----------
    public User verSaldo(User user){
        System.out.println(this.channel.getAddress() + " retornando saldo");
        return ContaController.verSaldo(user);
    }


    //-------------------------------------------------------------------------------

    //Getters e Métodos Utilitarios --------
    public RpcDispatcher getDispatcher(){
        return this.dispatcher;
    }

    public Address getRandomMember(){
        return this.channel.getView().getMembers().get(
             (new Random()).nextInt(this.channel.getView().getMembers().size())
            );
    }

    private boolean souCoordenador() {
		return (this.channel.getAddress()
				.equals(
						this.channel.getView().getMembers().get(0)));
	}

    private void conectarNoCanal(){
        boolean conectou = false;
        while (true) {
            try {
                this.channel.connect("banco");
                this.dispatcher = new RpcDispatcher(this.channel,this);
                this.dispatcher.setReceiver(this);
                conectou = true;   
                System.out.println("Conectado!");
            } catch (Exception e) {
                //dá uma relaxa por 2 segundos e tenta denovo.
                System.out.println("Erro ao tentar conectar no canal: " + e.getMessage());
                System.out.println("Tentando novamente...");
                Util.sleep(2000);
            }
            if(conectou){
                break;
            }
        }
    }

    private void obterEstado(){
        boolean obteuEstado = false;
        while (true) {
            try {
                this.channel.getState(null,10000);
                obteuEstado = true;   
            } catch (Exception e) {
                //dá uma relaxa por 2 segundos e tenta denovo.
                System.out.println("Erro ao tentar obter o estado: " + e.getMessage());
                System.out.println("Tentando novamente...");
                Util.sleep(2000);
            }
            if(obteuEstado){
                break;
            }
        }
    }

    // --------------------------------------------------------------------------------

}
