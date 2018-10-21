package com.garoto.esrihackgtmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

public class SignIn extends AppCompatActivity {

    Button btnLogin;
    private final static int LOGIN_PERMISSION = 1000;


    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.sign_in);
        btnLogin = (Button) findViewById(R.id.btnSignIn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .setAllowNewEmailAccounts(true).build(), LOGIN_PERMISSION
                );
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_PERMISSION){
            startNewActivity(resultCode,data);
        }
    }

    private void startNewActivity(int resultCode, Intent data) {

        if (resultCode == RESULT_OK){
            Intent intent = new Intent(SignIn.this,ListOnline.class);
            startActivity(intent);
            finish();
        }

        else{
            Toast.makeText(this, "Login Failed!!!", Toast.LENGTH_SHORT).show();
        }

    }
}
