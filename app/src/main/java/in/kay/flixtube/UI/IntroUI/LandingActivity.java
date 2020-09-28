package in.kay.flixtube.UI.IntroUI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
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
           this.finish();
       }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_login:
                Intent intent=new Intent(LandingActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                break;

            case R.id.btn_signup:
                Intent i=new Intent(LandingActivity.this, SignupActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "Gilroy-ExtraBold.ttf");
        new iOSDialogBuilder(this)
                .setTitle("Exit")
                .setSubtitle("Ohh no! You're leaving...\nAre you sure?")
                .setCancelable(false)
                .setFont(font)
                .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        CloseApp();
                    }
                })
                .setNegativeListener(getString(R.string.dismiss), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }
    private void CloseApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}