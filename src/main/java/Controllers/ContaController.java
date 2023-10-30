package Controllers;

import Models.User;

public class ContaController {


    public static User verSaldo(User user){

        if(user.getUserDB() == null){
            user.setErro("Erro ao consultar saldo");
        }
        return user;
    }

    public static User transferirDinheiro(User origem,User destino,double valor){
        return origem;
    }

}