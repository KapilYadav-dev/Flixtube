package in.kay.flixtube.Utils;

import android.content.ContextWrapper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import in.dd4you.appsconfig.DD4YouConfig;
import in.kay.flixtube.Model.UsersModel;

public class Application extends android.app.Application {
    DD4YouConfig dd4YouConfig;
    DatabaseReference rootRef;
    Helper helper;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseOffline();
        Initz();
        PicassoOffline();
        InternetCheck();
        PrefBuilder();
    }

    public void Initz() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        helper = new Helper();
        GetUserData(usersModel);
    }

    public UsersModel usersModel = new UsersModel();

    public UsersModel getUsersModel() {
        return usersModel;
    }

    public void GetUserData(final UsersModel usersModel) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersModel.setEmail(helper.decryptedMsg(snapshot.child("Name").getValue(String.class), snapshot.child("Email").getValue(String.class)));
                    usersModel.setMembership(snapshot.child("Membership").getValue(String.class));
                    usersModel.setMobileUid(helper.decryptedMsg(snapshot.child("Name").getValue(String.class), snapshot.child("MobileUid").getValue(String.class)));
                    usersModel.setName(snapshot.child("Name").getValue(String.class));
                    usersModel.setViolation(snapshot.child("Violation").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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
