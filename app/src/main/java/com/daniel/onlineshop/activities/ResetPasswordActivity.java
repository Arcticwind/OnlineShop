package com.daniel.onlineshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.daniel.onlineshop.R;
import com.daniel.onlineshop.utils.MyMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_reset_password)
    Toolbar toolbar;

    @BindView(R.id.et_reset_email)
    EditText etResetPasswordEmail;

    @OnClick(R.id.btn_reset_password)
    public void btnResetPassword() {
        resetPassword();
    }

    private FirebaseAuth firebaseAuth;
    private MyMethods mm = new MyMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void resetPassword() {
        String userEmail = etResetPasswordEmail.getText().toString().trim();

        if (userEmail.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_enter_email), Toast.LENGTH_SHORT).show();
        } else {
            mm.showProgressDialog(getString(R.string.progress_sending_in_progress), getString(R.string.progress_please_wait), this);
            firebaseAuth.sendPasswordResetEmail(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mm.dismissProgressDialog();
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_password_reset_link_sent), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mm.dismissProgressDialog();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
