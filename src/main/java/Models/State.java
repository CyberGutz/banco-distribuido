package Models;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONTokener;

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

    public static void atualizarVersao() throws Exception{
        int versao;
        try {
            versao = Integer.parseInt(new String(Files.readAllBytes(Paths.get("versao.txt"))));
            System.out.println("catou a versão");
        } catch (Exception e) { //primeira versao
            System.out.println("nao catou a versao");
            versao = 1;
        }
        System.out.println("versao:" + versao);
        versao++;
        System.out.println("versao nova :" + versao);
        Files.write(Paths.get("versao.txt"), String.valueOf(versao).getBytes());
        System.out.println("Versão atualizada");
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
