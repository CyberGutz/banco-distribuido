package Controllers;

import Models.User;

public class AuthController {
    
    public static User criarConta(String usuario,String senha){
    
        User user = new User(usuario,senha);
		try {
            if(user.userJaCriado() == null) user.salvar();
		} catch (Exception e) {
			user.setErro(e.getMessage());
		}
		return user;
        
    }

    public static User fazerLogin(String usuario,String senha){
        User user = new User(usuario,senha);
		try {
            user.realizarLogin();
		} catch (Exception e) {
			user.setErro(e.getMessage());
		}
		return user;
    }

	

}
