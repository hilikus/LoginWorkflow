package it.adonadoni.loginworkflow.server;

import java.util.HashMap;
import java.util.Map;

import it.adonadoni.loginworkflow.model.LoginResult;

public class FakeLoginServer {

    private Map<String, String> credentials;

    public FakeLoginServer()  {
        credentials = new HashMap<String, String>();
        credentials.put("alberto", "albertopw");
        credentials.put("antonio", "antoniopw");
        credentials.put("andrea", "andreapw");
        credentials.put("alessio", "alessiopw");
    }

    public LoginResult login(String username, String password) {

        LoginResult result = new LoginResult();

        if (username != null && !username.isEmpty()) {

            // Username not present inside the HashMap...so it's a wrong username!
            if (!credentials.containsKey(username)) {
                result.setSuccess(false);
                result.setErrorMessage("Wrong username!");
                return result;
            }

            String pwValue = credentials.get(username);
            if (pwValue.equals(password)) {
                result.setSuccess(true);
                result.setErrorMessage("No error...successful login!");
                return result;
            }
            // Wrong password
            else {
                result.setSuccess(false);
                result.setErrorMessage("Wrong password!");
                return result;
            }

        } else {
            result.setSuccess(false);
            result.setErrorMessage("Generic error!");
            return result;
        }
    }

    public void logout() {
        return;
    }
}
