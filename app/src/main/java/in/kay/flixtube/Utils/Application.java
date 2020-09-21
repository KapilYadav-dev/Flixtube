package in.kay.flixtube.Utils;

import android.content.ContextWrapper;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import in.dd4you.appsconfig.DD4YouConfig;

public class Application extends android.app.Application {
    DD4YouConfig dd4YouConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseOffline();
        PicassoOffline();
        InternetCheck();
        PrefBuilder();
    }


    private void PrefBuilder() {
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    private void InternetCheck() {
        dd4YouConfig = new DD4YouConfig(this);
        if (!dd4YouConfig.isInternetConnectivity()) {
            Toast.makeText(this, "Please check your internet connection...", Toast.LENGTH_SHORT).show();
        }
    }

    private void FirebaseOffline() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private void PicassoOffline() {
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

}
