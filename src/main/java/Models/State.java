package Models;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class State implements java.io.Serializable {

    private byte[] users;
    private byte[] transferencias;
    private int versao;

    public State() throws FileNotFoundException, IOException {
        
        this.versao = consultarVersao();
    
        File file = new File("users.json");

        BufferedInputStream bfis = new BufferedInputStream(new FileInputStream(file));

        this.users = bfis.readAllBytes();
        bfis.close();

        file = new File("transferencias.json");
        bfis = new BufferedInputStream(new FileInputStream(file));

        this.transferencias = bfis.readAllBytes();
        bfis.close();
    }

    public static int consultarVersao(){
        int versao;
        try {
            versao = Integer.parseInt(new String(Files.readAllBytes(Paths.get("versao.txt"))));
        } catch (Exception e) { //primeira versao
            versao = 1;
        }
        return versao;
    }

    public static void atualizarVersao() throws Exception{
        int versao = consultarVersao();
        versao++;
        Files.write(Paths.get("versao.txt"), String.valueOf(versao).getBytes());
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

    public int getVersao() {
        return versao;
    }
}