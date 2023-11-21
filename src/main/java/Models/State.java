package Models;

public class State implements java.io.Serializable {
    
    private byte[] users;
    private byte[] transferencias;

    public State(){
    }

    public byte[] getUsers() {
        return users;
    }

    public void setUsers(byte[] users) {
        this.users = users;
    }

    public byte[] getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(byte[] transferencias) {
        this.transferencias = transferencias;
    }
}
