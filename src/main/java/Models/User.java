package Models;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class User implements java.io.Serializable {

    // atributos acessíveis
    private String nome;
    private int conta;
    private double creditos;

    // atributos auxiliares
    private String erro;
    private String senha;

    /**
     * Construtor usado na instância do cliente
     */
    public User(String nome, int conta) {
        this.nome = nome;
        this.conta = conta;
    }

    /**
     * Construtor a ser usado no login
     */
    public User(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

    public JSONObject getUserDB(boolean refresh) {

        try {
            JSONArray jsonArray = new JSONArray(new JSONTokener(new FileReader("users.json")));
            for (Object user : jsonArray) { // verificando se o usuario existe
                JSONObject jsonUser = new JSONObject(user.toString());
                if (jsonUser.getString("usuario").equals(this.nome) || jsonUser.getInt("conta") == this.conta) {
                    if(refresh){
                        this.setNome(jsonUser.getString("usuario"));
                        this.setConta(jsonUser.getInt("conta"));
                        this.setCreditos(jsonUser.getDouble("creditos"));
                    }
                    return jsonUser;
                }
            }
        } catch (JSONException e) {
            this.setErro("Erro ao checar verificacao de existencia user:" + e.getMessage());
        } catch (FileNotFoundException e) {
            return null;
        }
        return null;
    }

    public void salvar() throws IOException {

        JSONArray jsonArray;
        int contaNum = 1;

        try (FileReader fileReader = new FileReader("users.json")) {
            jsonArray = new JSONArray(new JSONTokener(fileReader));
            contaNum = jsonArray.length() + 1;
        } catch (FileNotFoundException e) {
            jsonArray = new JSONArray();
        }

        JSONObject user = this.getUserDB(false);

        if (user == null) { // cria novo usuario
            
            try {
                user = new JSONObject();
                user.put("usuario", this.nome);
                user.put("senha", this.senha);
                String token = User.criarToken(user.toString());
                user.put("creditos", 1000.00); // começa com 1000 reais
                user.put("senha", token); // substitui a senha pelo token
                user.put("conta", contaNum);
                jsonArray.put(user);

                FileWriter fileWriter = new FileWriter("users.json");
                fileWriter.write(jsonArray.toString());
                fileWriter.close();
                this.setConta(contaNum);
                this.setCreditos(1000.00);
            } catch (Exception e) {
                this.setErro("Erro ao salvar usuário:" + e.getMessage());
            }
        } else {// atualiza novo usuario
            try {     
                
                System.out.println("Atualizando usuario " + this.getNome());
                user.put("creditos", this.creditos);
                user.put("usuario", this.nome);           
                
                for(int i=0;i < jsonArray.length();i++){
                    JSONObject userJson = new JSONObject(jsonArray.get(i).toString());
                    if(userJson.getInt("conta") == this.conta){
                        jsonArray.put(i,user);
                        break;
                    }
                }

                FileWriter fileWriter = new FileWriter("users.json");
                fileWriter.write(jsonArray.toString());
                fileWriter.close();
            } catch (Exception e) {
                this.setErro("Erro ao alterar usuário:" + e.getMessage());
            }

        }

    }

    public void realizarLogin() {

        JSONObject userEncontrado = this.getUserDB(false);

        if (userEncontrado != null) {

            int conta = userEncontrado.getInt("conta");
            double creditos = userEncontrado.getDouble("creditos");

            // removendo atributos, deve ser comparado o token gerado apenas com as chaves
            // "usuario" e "senha " do JSON
            userEncontrado.remove("conta");
            userEncontrado.remove("creditos");
            userEncontrado.put("senha", this.senha);

            JSONObject userLogin = new JSONObject();
            userLogin.put("usuario", this.nome);
            userLogin.put("senha", this.senha);

            //verifica se a combinação usuario e senha inserida pelo usuario gera o token de autenticação criado para o mesmo
            if (User.criarToken(userLogin.toString()).equals(User.criarToken(userEncontrado.toString()))) {
                // retorno.put("token", jsonUser.getString("senha"));
                this.setConta(conta);
                this.setCreditos(creditos);
            } else {
                this.setErro("Senha inválida!");
            }

        } else {
            this.setErro("Usuário inexistente.Faça seu cadastro!");
        }

    }

    /**
     * Recebe uma string e retorna um token representando-a
     * Neste caso a string será um pequeno json representando um usuario e uma senha
     * 
     * @param data
     * @return token
     */
    private static String criarToken(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(data.getBytes());
            byte[] hashBytes = digest.digest();

            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte b : hashBytes) { // constroi o array de bytes como uma string em hexadecimal
                hashStringBuilder.append(String.format("%02x", b));
            }

            return hashStringBuilder.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setConta(int conta) {
        this.conta = conta;
    }

    public int getConta() {
        return this.conta;
    }

    public String getNome() {
        return this.nome;
    }

    public double getCreditos() {
        return creditos;
    }

    public void setCreditos(double creditos) {
        this.creditos = creditos;
    }

    public ArrayList<Transferencia> getExtrato(){
        Transferencia transf = new Transferencia(this);
        return transf.getTransferenciasUserOrigem();
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

}
