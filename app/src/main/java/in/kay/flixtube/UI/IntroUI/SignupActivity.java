package in.kay.flixtube.UI.IntroUI;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

import in.kay.flixtube.R;
import in.kay.flixtube.Utils.Helper;

public class SignupActivity extends AppCompatActivity {

    EditText etusername, etpassword, etemail;
    private FirebaseAuth mAuth;
    String name, email, password;
    DatabaseReference rootRef;
    Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        helper=new Helper();
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        etemail = findViewById(R.id.et_email);
        etusername = findViewById(R.id.et_firstname);
        etpassword = findViewById(R.id.et_password);
        findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             GotoLoginActivity();
            }
        });
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        name = etusername.getText().toString();
        email = etemail.getText().toString();
        password = etpassword.getText().toString();

        if (name.isEmpty()) {
            etusername.setError("Username is required");
            etusername.requestFocus();
            return;
        }
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
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().sendEmailVerification();
                        TastyToast.makeText(SignupActivity.this,"Welcome "+name+ " to Flixtube. Please check your mail for further process",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        Createdatabase();
                        GotoLoginActivity();
                    } else
                        TastyToast.makeText(SignupActivity.this,task.getException().getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);

                }
            });
        }

    }

    private void GotoLoginActivity() {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }

    private void Createdatabase() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Email", helper.encryptString(name,email));
        map.put("Name", name);
        map.put("MobileUid",helper.encryptString(name,helper.deviceId(getBaseContext())));
        map.put("Membership", "Normal");
        map.put("Violation", "No");
        rootRef.child("User").child(mAuth.getCurrentUser().getUid()).setValue(map);
    }

}
