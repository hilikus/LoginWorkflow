package it.adonadoni.loginworkflow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import it.adonadoni.loginworkflow.utilities.Utils;

import static it.adonadoni.loginworkflow.R.id.et_password;

public class LoginDialogFragment extends DialogFragment {

    // Interface that must be implemented by the activity which hosts the DialogFragment
    // to receive the positive click callback
    public interface LoginDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String userStr, String pwStr);
    }

    // Reference for delivering action events
    private LoginDialogListener dialogListener;

    // Reference to the created dialog
    private AlertDialog dialog;

    // EditText references for strings validation
    private EditText usernameEditText;
    private EditText passwordEditText;

    // Flag for inline string validation
    private boolean usernameValidated;
    private boolean passwordValidated;

    public LoginDialogFragment() {
        // Empty constructor required
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity hostActivity;
        if(! (context instanceof Activity)) {
            return;
        }
        hostActivity = (Activity) context;
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LoginDialogListener so we can send events to the host
            dialogListener = (LoginDialogListener) hostActivity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(hostActivity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater and inflate the dialog fragment layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.login_dialog, null);

        // Get references to the EditText views
        usernameEditText = (EditText) view.findViewById(R.id.et_username);
        passwordEditText = (EditText) view.findViewById(et_password);

        // Init flag for inline string validation
        usernameValidated = false;
        passwordValidated = false;

        // Add TextChangeListener for inline validation
        usernameEditText.addTextChangedListener(new LoginFragmentTextWatcher(usernameEditText));
        passwordEditText.addTextChangedListener(new LoginFragmentTextWatcher(passwordEditText));

        // Inflate and set the layout for the dialog
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.dialog_login_label_btn,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String userNameStr = usernameEditText.getText().toString();
                        String passwordStr = passwordEditText.getText().toString();
                        // send the event and data back to the host activity to attempt login step
                        dialogListener.onDialogPositiveClick(
                                LoginDialogFragment.this, userNameStr, passwordStr);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel_label_btn,
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                });

        dialog = builder.create();
        dialog.show();
        // By default disable submit button at first opening
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        return dialog;
    }

    private class LoginFragmentTextWatcher implements TextWatcher {

        private View view;

        private LoginFragmentTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {

            // Username EditText
            if (view.getId() == R.id.et_username) {
                EditText editText = (EditText) getDialog().findViewById(R.id.et_username);
                String str = editText.getText().toString();
                if (! Utils.validateInputString(str)) {
                    editText.setError("It must not be empty and not contain { \\ , . }");
                    usernameValidated = false;
                } else {
                    usernameValidated = true;
                }
            }
            // Password EditText
            else if (view.getId() == R.id.et_password) {
                EditText editText = (EditText) getDialog().findViewById(R.id.et_password);
                String str = editText.getText().toString();
                if (! Utils.validateInputString(str)) {
                    editText.setError("It must not be empty and not contain { \\ , . }");
                    passwordValidated = false;
                } else {
                    passwordValidated = true;
                }
            }

            // Enable submit button if both EditText fields have been validated
            if (usernameValidated && passwordValidated) {
                dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
            } else {
                dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
            }
        }
    }
}
