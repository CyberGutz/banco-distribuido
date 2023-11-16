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

    public ClusterController(JChannel channel){
        this.channel = channel;
        this.dispatcher = new RpcDispatcher(this.channel,this);
        this.dispatcher.setReceiver(this);
        if(!this.souCoordenador()){
            try {
                this.channel.getState(null,10000);
            } catch (Exception e) {}
        }
        //loop serviço principal
        this.bancoServer();
    }

	private void bancoServer() {

		RMIServerController rmiServer = new RMIServerController();

		while(this.channel.getView().size() < TAMANHO_MINIMO_CLUSTER){
			Util.sleep(1000);
		}

		if (souCoordenador()) {
			rmiServer.start();
		}

		while (true) {

		}

	}

    //-- Métodos de Gerenciamento do Grupo
    public void viewAccepted(View view){
        System.out.println("View ");
        System.out.println(view.getMembers());
    }
    
    public void receive(Message msg){
        System.out.println("Mensage braba: " + msg.getSrc());
    }

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
                System.out.println("Enviou o estado");
            } 
        catch (FileNotFoundException e) {
            //expulsar o cara que nao transferi o estado
            System.out.println("Erro filenotfound: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro io: " + e.getMessage());
            //tentar denovo..
        }

    }

    public void setState(InputStream input) {
       
        
        try {
                System.out.println("setState()");

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
        catch (FileNotFoundException e) {
            System.out.println("Erro filenotfound: " + e.getMessage());
            //expulsar o cara que nao transferi o estado
        } catch (IOException e) {
            System.out.println("Erro io: " + e.getMessage());
            //tentar denovo..
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
        }


    }


    // Métodos da Aplicação
    public User verSaldo(User user){
        System.out.println(this.channel.getAddress() + " retornando saldo");
        return ContaController.verSaldo(user);
    }

    //Getters e Métodos Utilitarios
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

}
