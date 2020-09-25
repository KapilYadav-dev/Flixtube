package in.kay.flixtube.UI.IntroUI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.pixplicity.easyprefs.library.Prefs;

import in.kay.flixtube.R;
import in.kay.flixtube.UI.HomeUI.MainActivity;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        mAuth=FirebaseAuth.getInstance();
        CheckIsFirst();
        findViewById(R.id.btn_signup).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    private void CheckIsFirst() {
       if (mAuth.getCurrentUser()!=null)
       {
           startActivity(new Intent(this, MainActivity.class));
       }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                break;

            case R.id.btn_signup:
                startActivity(new Intent(LandingActivity.this, SignupActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //Do nothing
    }
}