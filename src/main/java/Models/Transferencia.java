package Models;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Transferencia implements java.io.Serializable {

    private User origem;
    private User destino;
    private double valor;
    private Date data;
    private String erro;


    public Transferencia(User origem, User destino, double valor, Date data) {
        this.origem = origem;
        this.destino = destino;
        this.valor = valor;
        this.data = data;
    }

    public Transferencia(User origem){
        this.origem = origem;
    }

    public Transferencia(User origem, User destino,double valor){
        this.origem = origem;
        this.destino = destino;
        this.valor = valor;
    }

    public void salvar() {
        
        JSONArray jsonArray;
        
        try (FileReader fileReader = new FileReader("transferencias.json")) {
            jsonArray = new JSONArray(new JSONTokener(fileReader));
        } catch (Exception e) {
            jsonArray = new JSONArray();
        }

        try {

            JSONObject transferencia = new JSONObject();
            transferencia.put("contaOrigem", this.origem.getConta());            
            transferencia.put("contaDestino", this.destino.getConta());
            transferencia.put("valor", this.valor);
            transferencia.put("data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(this.data));

            jsonArray.put(transferencia);

            FileWriter fileWriter = new FileWriter("transferencias.json");
            fileWriter.write(jsonArray.toString());
            fileWriter.close();
        } catch (Exception e) {
            this.setErro("Erro ao salvar transferência:" + e.getMessage());
        }
    }

    
    public User getUserOrigem() {
        return origem;
    }

    public void setUserOrigem(User origem) {
        this.origem = origem;
    }

    public User getUserDestino() {
        return destino;
    }

    public void setUserDestino(User destino) {
        this.destino = destino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public ArrayList<Transferencia> getTransferenciasUserOrigem(){
        try {
            
            JSONArray jsonArray = new JSONArray(new JSONTokener(new FileReader("transferencias.json")));
            ArrayList<Transferencia> transferencias = new ArrayList<Transferencia>();
            System.out.println(this.origem.getConta());
            for(int i=0; i < jsonArray.length();i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                if(jsonObj.getInt("contaOrigem") == this.origem.getConta() || jsonObj.getInt("contaDestino") == this.origem.getConta()){
                    
                    User origem = new User("", jsonObj.getInt("contaOrigem"));
                    origem.getUserDB(true);
                    User destino = new User("", jsonObj.getInt("contaDestino"));
                    destino.getUserDB(true);

                    transferencias.add(new Transferencia(
                                        origem, 
                                        destino, 
                                        jsonObj.getDouble("valor"),
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(jsonObj.getString("data"))
                                        ));
                }
            }

            return transferencias;
        } catch (Exception e) {
            this.erro = e.getMessage();
            return null;
        }
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

}
