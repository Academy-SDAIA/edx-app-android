package org.edx.mobile.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.databinding.DataBindingUtil;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragmentActivity;
import org.edx.mobile.databinding.ActivityLaunchBinding;
import org.edx.mobile.module.analytics.Analytics;
import org.edx.mobile.module.prefs.LoginPrefs;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LaunchActivity extends BaseFragmentActivity {

    @Inject
    LoginPrefs loginPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityLaunchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_launch);
        binding.signInTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(environment.getRouter().getLogInIntent());
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(environment.getRouter().getLogInIntent());
                finish();
            }
        }, 3000);
//        binding.signUpBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                environment.getAnalyticsRegistry().trackUserSignUpForAccount();
//                startActivity(environment.getRouter().getRegisterIntent());
//            }
//        });
     //   environment.getAnalyticsRegistry().trackScreenView(Analytics.Screens.LAUNCH_ACTIVITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (environment.getLoginPrefs().isUserLoggedIn()) {
            finish();
            environment.getRouter().showMainDashboard(this);
        }
    }
}
