package in.kay.flixtube;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttondsignup).setOnClickListener(this);
        findViewById(R.id.buttondlogin).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.buttondlogin:
                startActivity(new Intent(MainActivity.this,login.class));
                break;

            case R.id.buttondsignup:
                startActivity(new Intent(MainActivity.this,signup.class));
                break;


        }
    }
}