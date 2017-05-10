package it.adonadoni.loginworkflow;

import it.adonadoni.loginworkflow.model.LoginResult;

public interface LoginTaskManager {

    public void onPreExecuteLogin();
    public void onPostExecuteLogin(LoginResult result);

}
