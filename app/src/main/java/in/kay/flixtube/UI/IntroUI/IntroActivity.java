package in.kay.flixtube.UI.IntroUI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

import in.kay.flixtube.R;

public class IntroActivity extends AhoyOnboarderActivity {
    AhoyOnboarderCard ahoyOnboarderCard1,ahoyOnboarderCard2,ahoyOnboarderCard3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sheet1();
        Sheet2();
        Sheet3();
        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        setOnboardPages(pages);
        setImageBackground(R.drawable.intro_back);
        Typeface face = Typeface.createFromAsset(getAssets(), "Brandon.ttf");
        setFont(face);
        setFinishButtonTitle("Let's Stream");
    }

    private void Sheet3() {
        ahoyOnboarderCard3 = new AhoyOnboarderCard("Amazing Content", "Watch thousands of hit movies and TV series for free. Flixtube is 100% legal unlimited streaming app.", R.drawable.ic_popcorn);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setTitleColor(R.color.white);
        ahoyOnboarderCard3.setDescriptionColor(R.color.white);
        ahoyOnboarderCard3.setTitleTextSize(dpToPixels(14, this));
        ahoyOnboarderCard3.setDescriptionTextSize(dpToPixels(6, this));
        ahoyOnboarderCard3.setIconLayoutParams(400, 400, 100, 10, 10, 10);
    }

    private void Sheet2() {
        ahoyOnboarderCard2 = new AhoyOnboarderCard("Security", "❝ Care to be aware! ❞\nWe provide top notch content with end to end encryption.", R.drawable.ic_secure);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setTitleColor(R.color.white);
        ahoyOnboarderCard2.setDescriptionColor(R.color.white);
        ahoyOnboarderCard2.setTitleTextSize(dpToPixels(14, this));
        ahoyOnboarderCard2.setDescriptionTextSize(dpToPixels(6, this));
        ahoyOnboarderCard2.setIconLayoutParams(400, 400, 100, 10, 10, 10);
    }

    private void Sheet1() {
        ahoyOnboarderCard1 = new AhoyOnboarderCard("\uD835\uDD71\uD835\uDD91\uD835\uDD8E\uD835\uDD9D\uD835\uDD99\uD835\uDD9A\uD835\uDD87\uD835\uDD8A", "Flixtube is one of the largest internet TV and Movies provider in the world.", R.drawable.ic_flixtube);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.white);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(14, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(6, this));
        ahoyOnboarderCard1.setIconLayoutParams(400, 400, 100, 10, 10, 10);
    }

    @Override
    public void onFinishButtonPressed() {
        startActivity(new Intent(IntroActivity.this,LandingActivity.class));
    }
}