package com.daniel.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.utils.MyMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_registration)
    Toolbar toolbar;

    @BindView(R.id.et_registration_email)
    EditText etEmail;

    @BindView(R.id.et_registration_password)
    EditText etPassword;

    @BindView(R.id.et_registration_password_repeat)
    EditText etPasswordRepeat;

    @OnClick(R.id.btn_registration_register)
    public void btnRegister() {
        registerUser();
    }

    private FirebaseAuth firebaseAuth;
    private MyMethods mm = new MyMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void registerUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordRepeat = etPasswordRepeat.getText().toString();

        if (email.isEmpty() && (password.isEmpty() || passwordRepeat.isEmpty())) {
            Toast.makeText(this, getString(R.string.toast_enter_all_login_info), Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            etEmail.setError(getString(R.string.error_enter_email));
            etEmail.requestFocus();
        } else if (password.isEmpty()) {
            etPassword.setError(getString(R.string.error_enter_password));
            etPassword.requestFocus();
        } else if (passwordRepeat.isEmpty()) {
            etPasswordRepeat.setError(getString(R.string.error_enter_repeat_password));
            etPasswordRepeat.requestFocus();
        } else if (!(email.isEmpty() && password.isEmpty() && passwordRepeat.isEmpty())) {

            if (password.trim().equals(passwordRepeat.trim())) {

                mm.showProgressDialog(getString(R.string.progress_registering), getString(R.string.progress_please_wait), this);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        mm.dismissProgressDialog();
                        actionToMainMenu();
                        Toast.makeText(RegistrationActivity.this, getString(R.string.toast_welcome) + " " + authResult.getUser().getEmail(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mm.dismissProgressDialog();
                                Toast.makeText(RegistrationActivity.this, getString(R.string.toast_error) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else { Toast.makeText(this, getString(R.string.toast_error_passwords_not_matching), Toast.LENGTH_SHORT).show(); }
        } else { Toast.makeText(this, getString(R.string.toast_enter_all_login_info), Toast.LENGTH_SHORT).show(); }
    }

    private void actionToMainMenu() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
