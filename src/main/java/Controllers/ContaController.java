package Controllers;

import java.util.ArrayList;
import java.util.Calendar;

import Models.Transferencia;
import Models.User;

public class ContaController {

    private ClusterController cluster;

    public ContaController(ClusterController cluster){
        this.cluster = cluster;
    }

    public static User verSaldo(User user){

        if(user.getUserDB(true) == null){
            user.setErro("Erro ao consultar saldo");
        }
        return user;
    }

    public static User transferirDinheiro(User origem,User destino,double valor){
        try {
            
            origem.setCreditos(origem.getCreditos() - valor);
            origem.salvar();
            if(origem.getErro() != null) throw new Exception(origem.getErro());
            
            destino.setCreditos(destino.getCreditos() + valor);
            destino.salvar();
            if(destino.getErro() != null) throw new Exception(destino.getErro());

            Transferencia transf = new Transferencia(origem,destino,valor,Calendar.getInstance().getTime());
            transf.salvar();
        } catch (Exception e) {
           origem.setErro("Erro ao realizar transferência: " + e.getMessage());;
        }
        return origem;
    }

    public static ArrayList<Transferencia> obterExtrato(User user) {
        try {
            return user.getExtrato();
        } catch (Exception e) {
            return null;
        }
    };

}