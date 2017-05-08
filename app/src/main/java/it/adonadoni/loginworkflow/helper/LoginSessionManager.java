package it.adonadoni.loginworkflow.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import it.adonadoni.loginworkflow.R;

public class LoginSessionManager {

    // Log tag
    private static final String TAG = LoginSessionManager.class.getSimpleName();

    // Shared Preferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static String sharedPrefFileKey;
    private static String sharedPrefIsLoggedKey;

    public LoginSessionManager(Context context) {
        sharedPrefFileKey = context.getString(R.string.shared_pref_file_key);
        sharedPrefIsLoggedKey = context.getString(R.string.shared_pref_islogged_key);

        sharedPreferences = context.getSharedPreferences(sharedPrefFileKey, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLogin(boolean isLogged) {
        editor.putBoolean(sharedPrefFileKey, isLogged);
        editor.commit();

        Log.d(TAG, "Login status change - setLogin() -> " + isLogged);
    }

    public boolean isLogged() {
        boolean loginStatus = sharedPreferences.getBoolean(sharedPrefFileKey, false);

        Log.d(TAG, "Login status check - isLogged() -> " + loginStatus);
        return loginStatus;
    }

}
