package com.daniel.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.utils.MyConstants;
import com.daniel.onlineshop.utils.MyMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_login)
    Toolbar toolbar;

    @BindView(R.id.et_login_email)
    EditText etEmail;

    @BindView(R.id.et_login_password)
    EditText etPassword;

    @OnClick(R.id.btn_login)
    public void btnLogin() { loginUser(); }

    @OnClick(R.id.btn_registration)
    public void btnToRegister() { actionToRegistration(); }

    @OnClick(R.id.tv_login_forgot_password)
    public void tvToResetPassword() { actionToResetPassword(); }

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private MyMethods mm = new MyMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    login(firebaseAuth.getCurrentUser());
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void onStop() {
        super.onStop();
        if (authStateListener != null) { firebaseAuth.removeAuthStateListener(authStateListener); }
    }

    private void login(FirebaseUser currentUser) {
        if (currentUser.getEmail().equals(MyConstants.ADMIN_EMAIL) || currentUser.getEmail().equals(MyConstants.ADMIN_EMAIL_TWO)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), UserStoreActivity.class));
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_enter_all_login_info), Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            etEmail.setError(getString(R.string.error_enter_email));
            etEmail.requestFocus();
        } else if (password.isEmpty()) {
            etPassword.setError(getString(R.string.error_enter_password));
            etPassword.requestFocus();
        } else if (!(email.isEmpty() && password.isEmpty())) {

            mm.showProgressDialog(getString(R.string.progress_logging_in), getString(R.string.progress_please_wait), this);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            mm.dismissProgressDialog();
                            login(firebaseAuth.getCurrentUser());
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_welcome) + " " + authResult.getUser().getEmail(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mm.dismissProgressDialog();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void actionToRegistration() { startActivity(new Intent(getApplicationContext(), RegistrationActivity.class)); }
    private void actionToResetPassword() { startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class)); }
}
