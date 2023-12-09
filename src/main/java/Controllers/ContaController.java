package Controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONObject;

import Models.Transferencia;
import Models.User;

public class ContaController {

    
    public static User verSaldo(User user){

        if(user.getUserDB(true) == null){
            user.setErro("Erro ao consultar saldo");
        }
        return user;
    }

    public static User transferirDinheiro(Transferencia transferencia){
        User origem = transferencia.getUserOrigem();
        User destino = transferencia.getUserDestino();
        double valor =  transferencia.getValor();
        try {
            //atualiza saldo das contas antes de transferir
            origem.getUserDB(true);
            destino.getUserDB(true);
            if(origem.getCreditos() - valor < 0.0 || origem.getCreditos() == 0.0){ //nao tem dinheiro suficiente pra transferencia
                System.out.println(origem.getNome() + " com saldo insuficiente");
                throw new Exception("Saldo insuficiente");
            }

            origem.setCreditos(origem.getCreditos() - valor);
            origem.salvar();
            if(origem.getErro() != null) throw new Exception(origem.getErro());
            
            destino.setCreditos(destino.getCreditos() + valor);
            destino.salvar();
            if(destino.getErro() != null) throw new Exception(destino.getErro());

            transferencia.setData(Calendar.getInstance().getTime());
            transferencia.salvar();
            
        } catch (Exception e) {
           origem.setErro("Erro ao realizar transferência: " + e.getMessage());
        }
        return origem;
    }

    public static ArrayList<Transferencia> obterExtrato(User user) {
        try {
            return user.getExtrato();
        } catch (Exception e) {
            return null;
        }
    }

    public static Double obterMontante(){
        String jsonString;
        Double montante = 0.0;
        byte[] users;
        try {
            users = Files.readAllBytes(Paths.get("users.json"));
            jsonString = new String(users);
            JSONObject jsonObject = new JSONObject(jsonString);

            for (String key : jsonObject.keySet()) {
                if(key.equals("creditos")){
                    montante += (double)jsonObject.get(key);
                }
            }
            return montante;
        } catch (IOException e) {
            System.out.println("Erro ao consultar montante: " + e);
            return -1.0;
        }
    }

}