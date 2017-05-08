package it.adonadoni.loginworkflow;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.adonadoni.loginworkflow.helper.LoginSessionManager;
import it.adonadoni.loginworkflow.model.LoginResult;
import it.adonadoni.loginworkflow.server.FakeLoginServer;

public class MainActivity extends AppCompatActivity implements LoginDialogFragment.LoginDialogListener {

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // LoginSessionManager reference
    private LoginSessionManager loginSessionManager;

    // References to menu controls
    private MenuItem menuItemLogin;
    private MenuItem menuItemLogout;

    // References to views
    private TextView loginMessageTextView;
    private ProgressBar loadingProgressBar;
    private TextView errorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginMessageTextView = (TextView) findViewById(R.id.tv_login_message);
        loadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        errorMessageTextView = (TextView) findViewById(R.id.tv_error_message_display);

        // Initialize LoginSessionManager
        loginSessionManager = new LoginSessionManager(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemLogin = menu.findItem(R.id.action_login);
        menuItemLogout = menu.findItem(R.id.action_logout);

        // User logged in
        if (loginSessionManager.isLogged()) {
            updateViewsIfLoggedIn();
        }
        // User NOT logged in
        else {
            updateViewsIfLoggedOut();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            doLogin();
            return true;
        } else if (id == R.id.action_logout) {
            doLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLogin() {
        // Open the LoginDialogFragment
        LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(getSupportFragmentManager(), "login_fragment");
    }

    private void doLogout() {
        // Run AsyncTask to perform logout
        new LogoutTask().execute();
    }

    private void updateViewsIfLoggedIn() {
        // Update menu controls visibility
        menuItemLogin.setVisible(false);
        menuItemLogout.setVisible(true);

        if(!menuItemLogout.isEnabled()) {
            menuItemLogout.setEnabled(true);
        }

        // Update textview
        loginMessageTextView.setText(getString(R.string.login_label_msg));
    }

    private void updateViewsIfLoggedOut() {
        // Update menu controls visibility
        menuItemLogin.setVisible(true);
        menuItemLogout.setVisible(false);

        if(!menuItemLogin.isEnabled()) {
            menuItemLogin.setEnabled(true);
        }

        // Update textview
        loginMessageTextView.setText(getString(R.string.logout_label_msg));
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String usernameStr, String passwordStr) {
        //Toast.makeText(this, usernameStr + " --- " + passwordStr, Toast.LENGTH_SHORT).show();

        // Run AsyncTask to attempt login
        new LoginTask().execute(usernameStr, passwordStr);
    }

    /**
     * AsyncTask class to perform logout in background
     */
    public class LogoutTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Disable logout control in the toolbar
            menuItemLogout.setEnabled(false);
            // Hide TextView with error message
            errorMessageTextView.setVisibility(View.INVISIBLE);
            // Show ProgressBar
            loadingProgressBar.setVisibility(View.VISIBLE);
            // Update TextView label
            loginMessageTextView.setText(getString(R.string.logout_in_progress_label_msg));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FakeLoginServer loginServer = new FakeLoginServer();
            loginServer.logout();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Hide ProgressBar
            loadingProgressBar.setVisibility(View.INVISIBLE);
            // Set login status FALSE into the LoginSessionManager
            loginSessionManager.setLogin(false);
            // Update TextView label and controls visibility in the toolbar
            updateViewsIfLoggedOut();
        }
    }

    /**
     * AsyncTask class to attempt login in background
     */
    public class LoginTask extends AsyncTask<String, Void, LoginResult> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Disable login control in the toolbar
            menuItemLogin.setEnabled(false);
            // Hide TextView with error message
            errorMessageTextView.setVisibility(View.INVISIBLE);
            // Show ProgressBar
            loadingProgressBar.setVisibility(View.VISIBLE);
            // Update TextView label
            loginMessageTextView.setText(getString(R.string.login_in_progress_label_msg));
        }

        @Override
        protected LoginResult doInBackground(String... params) {

            // Something gone wrong!
            if(params.length == 0) {
                return null;
            }

            // Retrieve parameters to use in the server request
            String usernameParam = params[0];
            String passwordParam = params[1];

            FakeLoginServer loginServer = new FakeLoginServer();
            LoginResult loginResult = loginServer.login(usernameParam, passwordParam);
            return loginResult;
        }

        @Override
        protected void onPostExecute(LoginResult loginResult) {
            // Hide ProgressBar
            loadingProgressBar.setVisibility(View.INVISIBLE);

            // Login action successful
            if(loginResult != null && loginResult.isSuccess()) {
                // Set login status TRUE into the LoginSessionManager
                loginSessionManager.setLogin(true);
                // Update TextView label and controls visibility in the toolbar
                updateViewsIfLoggedIn();
            }
            // Login action failed
            else {
                // Enable login control in the toolbar
                menuItemLogin.setEnabled(true);
                // Update TextView label
                loginMessageTextView.setText(getString(R.string.logout_label_msg));
                // Set and show the TextView with error message
                errorMessageTextView.setVisibility(View.VISIBLE);
                errorMessageTextView.setText(loginResult.getErrorMessage());
            }
        }
    }
}
