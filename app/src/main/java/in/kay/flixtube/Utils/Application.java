package in.kay.flixtube.Utils;

import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import in.dd4you.appsconfig.DD4YouConfig;

public class Application extends android.app.Application {
    DD4YouConfig dd4YouConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        dd4YouConfig=new DD4YouConfig(this);
        if (!dd4YouConfig.isInternetConnectivity())
        {
            Toast.makeText(this, "Please check your internet connection...", Toast.LENGTH_SHORT).show();
        }


    }

}
