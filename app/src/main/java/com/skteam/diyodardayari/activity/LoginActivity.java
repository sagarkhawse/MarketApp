package com.skteam.diyodardayari.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.api.ApiCallsSingleton;
import com.skteam.diyodardayari.databinding.ActivityLoginBinding;
import com.skteam.diyodardayari.simpleclasses.Constants;
import com.skteam.diyodardayari.simpleclasses.Functions;
import com.skteam.diyodardayari.simpleclasses.Variables;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivityTest";
    private Activity activity;
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private ApiCallsSingleton singleton;
    private FirebaseUser mUser;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        activity = this;
        FirebaseApp.initializeApp(activity);
        mAuth = FirebaseAuth.getInstance();
        singleton = ApiCallsSingleton.getInstance(activity);
        Variables.sharedPreferences= getSharedPreferences("SKIP",MODE_PRIVATE);
        mUser = mAuth.getCurrentUser();
        if (mUser != null || Variables.sharedPreferences.getInt(Variables.skipped,0)==1) {
            startActivity(new Intent(activity, HomeActivity.class));
            finish();
        }
        initGoogleLogin();
        initViewsClicks();
    }

    private void initViewsClicks() {
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    Functions.hideSoftKeyboard(activity);
                    openOtpActivity();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.google.setOnClickListener(view -> signIn());
        binding.tvSkip.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,HomeActivity.class);

                Variables.editor = Variables.sharedPreferences.edit();
                Variables.editor.putInt(Variables.skipped,1);
                Variables.editor.apply();
                startActivity(intent);
                finish();
            }
        });
    }

    private void openOtpActivity() {
        Intent intent = new Intent(activity, OtpActivity.class);
        intent.putExtra(Constants.COUNTRY_CODE, "+91");
        intent.putExtra(Constants.PHONE, binding.etPhone.getText().toString());
        startActivity(intent);
    }


    private void initGoogleLogin() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        //**************Facebook SignIn************* //
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        showProgressGoogleLogin();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "onActivityResult: " + e.getMessage());
                Functions.ShowToast(activity, "something went wrong " + e.getMessage());
                hideProgressGoogleLogin();


                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    hideProgressGoogleLogin();
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        mUser = mAuth.getCurrentUser();
                        assert mUser != null;

                            singleton.register(mUser.getUid(), mUser.getEmail(), mUser.getDisplayName(), mUser.getPhoneNumber(), Constants.PHONE,activity);


                    } else {
                        // If sign in fails, display a message to the user.
                        Functions.ShowToast(activity, "Failed to sign in");

                    }

                    // ...
                }).addOnFailureListener(e -> {
            hideProgressGoogleLogin();
            Functions.ShowToast(activity, "failed to sign in " + e.getMessage());

        });
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void showProgressGoogleLogin() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.google.setVisibility(View.GONE);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void hideProgressGoogleLogin() {
        binding.progressBar.setVisibility(View.GONE);
        binding.google.setVisibility(View.VISIBLE);
    }

}