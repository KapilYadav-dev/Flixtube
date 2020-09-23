package in.kay.flixtube.UI.HomeUI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import in.kay.flixtube.R;
import in.kay.flixtube.Utils.Helper;

public class MembershipActivity extends AppCompatActivity implements PaymentResultListener {

    DatabaseReference rootRef;
    Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new Helper();
        rootRef = FirebaseDatabase.getInstance().getReference();
        CheckInternet();

    }

    private void CheckInternet() {
        if (helper.isNetwork(this)) {
            InitAll();
            BuyAccount();
        } else {
            Typeface font = Typeface.createFromAsset(this.getAssets(), "Gilroy-ExtraBold.ttf");
            new iOSDialogBuilder(this)
                    .setTitle("Oh shucks!")
                    .setSubtitle("Slow or no internet connection.\nPlease check your internet settings")
                    .setCancelable(false)
                    .setFont(font)
                    .setPositiveListener(getString(R.string.ok), new iOSDialogClickListener() {
                        @Override
                        public void onClick(iOSDialog dialog) {
                            CheckInternet();
                            dialog.dismiss();
                        }
                    })
                    .build().show();
        }
    }

    private void InitAll() {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String membership = snapshot.child("Membership").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void BuyAccount() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_sKxf90ARlhoVdi");
        final MembershipActivity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Flixtube");
            options.put("description", "Purchase premium Flixtube account");
            options.put("currency", "INR");
            String paisee = Integer.toString(Integer.parseInt("200") * 100);
            options.put("amount", paisee);
            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(this, "Payment error please try again" + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        rootRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Membership").setValue("VIP").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                TastyToast.makeText(MembershipActivity.this, "Welcome to Flixtube VIP club...", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                Intent intent=new Intent(MembershipActivity.this,MainActivity.class);
                startActivity(intent);          }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                TastyToast.makeText(MembershipActivity.this, "Server down. Error : " + e, TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });
    }


    @Override
    public void onPaymentError(int i, String s) {
        TastyToast.makeText(this, "Payment cancelled.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
       Intent intent=new Intent(this,MainActivity.class);
       startActivity(intent);
    }
}
