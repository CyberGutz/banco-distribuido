package Models;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class State implements java.io.Serializable {

    private byte[] users;
    private byte[] transferencias;

    public State() throws FileNotFoundException, IOException {
        File file = new File("users.json");

        BufferedInputStream bfis = new BufferedInputStream(new FileInputStream(file));

        this.users = bfis.readAllBytes();
        bfis.close();

        file = new File("transferencias.json");
        bfis = new BufferedInputStream(new FileInputStream(file));

        this.transferencias = bfis.readAllBytes();
        bfis.close();
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
