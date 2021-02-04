package com.skteam.diyodardayari.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.skteam.diyodardayari.BuildConfig;
import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.databinding.ActivityHomeBinding;
import com.skteam.diyodardayari.fragments.AllServicesFragment;
import com.skteam.diyodardayari.fragments.BaseFragment;
import com.skteam.diyodardayari.fragments.HomeFragment;
import com.skteam.diyodardayari.fragments.ServiceFragment;
import com.skteam.diyodardayari.fragments.ProfileFragment;
import com.skteam.diyodardayari.fragments.ShopsFragment;
import com.skteam.diyodardayari.fragments.UserProfileFragment;
import com.skteam.diyodardayari.simpleclasses.Functions;
import com.skteam.diyodardayari.simpleclasses.Helper;
import com.skteam.diyodardayari.simpleclasses.SharedPreferenceUtil;
import com.skteam.diyodardayari.simpleclasses.Variables;

import java.util.Objects;

public class HomeActivity extends BaseActivity {
    public ActivityHomeBinding binding;
    private String tag;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initUserData();

        initViewsClicks();
        loadBannerAd();
        check_if_registered_user_require_profile_update();

    }

    private void loadBannerAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
    }

    private void check_if_registered_user_require_profile_update() {
        //check if user has successfully registered or not , if category id is null then not registered
        if (userData.category_id != null) {
            setFragment(new HomeFragment(), "home_fragment");
        } else {
            setFragment(new ProfileFragment(), "profile_fragment");
            binding.bottomNav.setVisibility(View.GONE);
            binding.adView.setVisibility(View.GONE);
        }
    }

    private void initViewsClicks() {
        binding.bottomNav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    public void setFragment(Fragment fragment, String name) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft = ft.replace(R.id.fragment_container, fragment, name);
        ft.commit();
    }


    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        setFragment(new HomeFragment(), "home_fragment");
                        break;
                    case R.id.nav_my_services:
                        setFragment(new AllServicesFragment(), "all_services_fragment");
                        break;
                    case R.id.nav_profile:
                        setFragment(new ProfileFragment(), "profile_fragment");
                        break;
                    case R.id.nav_more:
                        showMenuOptions(binding.bottomNav.findViewById(R.id.nav_more));
                        break;

                }
                return true;
            };

    @SuppressLint("RestrictedApi")
    private void showMenuOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        log_out();
                        return true;
                    case R.id.contact_us:
                        new MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme)
                                .setMessage("નમસ્તે..!\n" +
                                        "દિયોદર ડાયરી એપ્લિકેશનમાં તમારું સ્વાગત છે.\n" +
                                        "એપને લાગતા કોઈપણ સવાલ - જવાબ માટે નીચે આપેલ ઈમેલ આઈડી પર મેઈલ કરી શકો છો. Email id:- bookwormhelpdesk@gmail.com")

                                .setPositiveButton("Contact Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Functions.startEmailIntent(activity);
                                    }
                                })
                                .show();
                        return true;

                    case R.id.privacy_policy:
                        Intent intent4 = new Intent(activity, PrivacyPolicyActivity.class);
                        intent4.putExtra("work", "privacy_policy");
                        startActivity(intent4);
                        return true;

                    case R.id.about_us:
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage("નમસ્તે...!\n" +
                                        "દિયોદર ડાયરી એપ્લિકેશનમાં તમારું સ્વાગત છે.\n" +
                                        "આ એપ્લિકેશન દિયોદરના વેપારી - વેપારી અને વેપારી ને ગ્રાહક સાથે જોડવા માટે બનાવવામા આવેલ છે.")
                                .show();
                        return true;


                    case R.id.share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "નમસ્તે..!\n" +
                                        "⭕દિયોદર ડાયરી⭕ એપ્લિકેશન દ્વારા એકબીજા વેપારી અને ગ્રાહકને સંપર્ક કરવામાં સરળતા રહે તે માટે બનાવવામાં આવી  છે તો એપ ડાઉનલોડ કરીને પોતાનું ખાતું બનાવો અને પોતાના ગ્રાહકોના સંપર્કમાં રહો. : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        return true;

                    case R.id.rate:
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                        startActivity(intent);
                        return true;


                    default:
                        return false;
                }

            }
        });

        @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(view.getContext(), (MenuBuilder) popupMenu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    private void log_out() {
        mAuth.signOut();
        Helper.setLoggedInUser(sharedPreferenceUtil, null);
        startActivity(new Intent(activity, LoginActivity.class));
        finish();
    }


    public void showUserDetailsPage() {
        tag = "user_profile_fragment";
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out);

        if (Variables.category_id.equals("")) {
            ft.hide(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("home_fragment")));
        } else {
            ft.hide(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("shops_fragment")));

        }

        ft.add(R.id.fragment_container, new UserProfileFragment(), tag)
                .addToBackStack(tag)
                .commit();
        binding.bottomNav.setVisibility(View.GONE);
        binding.adView.setVisibility(View.GONE);
    }


    public void showShopListPage() {
        tag = "shops_fragment";
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                .hide(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("home_fragment")))
                .add(R.id.fragment_container, new ShopsFragment(), tag)
                .addToBackStack(tag)
                .commit();
        binding.adView.setVisibility(View.GONE);
        binding.bottomNav.setVisibility(View.GONE);
    }


    public void showAllServicePage(String service_cat_id) {
        tag = "services_fragment";
        Bundle bundle = new Bundle();
        bundle.putString("service_cat_id", service_cat_id);
        AllServicesFragment fragment = new AllServicesFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                .hide(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("service_category_fragment")))
                .add(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();

        binding.bottomNav.setVisibility(View.GONE);
        binding.adView.setVisibility(View.GONE);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getActiveFragment() instanceof ShopsFragment) {
            Log.d(TAG, "onBackPressed: ");
        } else if (getActiveFragment() instanceof ProfileFragment) {
            Log.d(TAG, "onBackPressed: ");
        } else {
            Variables.category_id = "";
        }

        if (binding.bottomNav.getVisibility() == View.GONE && Variables.category_id.equals("")) {
            binding.bottomNav.setVisibility(View.VISIBLE);
            binding.adView.setVisibility(View.VISIBLE);

        }

    }


    public BaseFragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("profile_fragment");
        if (fragment != null) {
            ((ProfileFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }

    }


}