package Controllers;
import java.io.InputStream;
import java.io.OutputStream;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;

public class ClusterController implements Receiver {

    private JChannel channel;

    public ClusterController(JChannel channel){
        this.channel = channel;
    }

    public void viewAccepted(View view){
        System.out.println("View ");
    }
    
    public void receive(Message msg){
        System.out.println("Mensage braba: " + msg.getSrc());
    }

    public void getState(OutputStream output){
        // synchronized(state){
            // Util.objectToStream(state, new DataOutputStream(output));
        // }
        System.out.println("Obteve o estado");
    }

    public void setState(InputStream input) throws Exception {
        // List<String> list;
        // list=(List<String>)Util.objectFromStream(new DataInputStream(input));
        // synchronized(state) {
        //     state.clear();
        //     state.addAll(list);
        // }
        // System.out.println(list.size() + " messages in chat history):");
        // for(String str: list) {
        //     System.out.println(str);
        // }
    }
    
}
