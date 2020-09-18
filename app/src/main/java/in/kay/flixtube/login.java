package in.kay.flixtube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    EditText etpassword, etemail;
    String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etemail = findViewById(R.id.email);
        etpassword = findViewById(R.id.password);
        mAuth= FirebaseAuth.getInstance();
        findViewById(R.id.textsignup).setOnClickListener(this);
        findViewById(R.id.buttonlogin).setOnClickListener(this);
    }

    private void login(){

        email = etemail.getText().toString();
        password = etpassword.getText().toString();

        if (email.isEmpty()) {
            etemail.setError("Email cannot be empty");
            etemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("Please enter a valid email id");
            etemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etpassword.setError("Password cannot be empty");
            etpassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etpassword.setError("Password should have minimum 6 characters");
            etpassword.requestFocus();
            return;
        }
        else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(login.this, "Successful sign-in", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.textsignup:
                startActivity(new Intent(login.this,signup.class));
                break;

            case R.id.buttonlogin:
                login();
                break;

        }
    }
}



