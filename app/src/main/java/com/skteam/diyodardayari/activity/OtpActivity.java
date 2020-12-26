package com.skteam.diyodardayari.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.skteam.diyodardayari.R;
import com.skteam.diyodardayari.api.ApiCallsSingleton;
import com.skteam.diyodardayari.api.RetrofitApi;
import com.skteam.diyodardayari.api.RetrofitClient;
import com.skteam.diyodardayari.databinding.ActivityLoginBinding;
import com.skteam.diyodardayari.databinding.ActivityOtpBinding;
import com.skteam.diyodardayari.simpleclasses.Constants;
import com.skteam.diyodardayari.simpleclasses.Functions;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;

public class OtpActivity extends AppCompatActivity {
    private static final String TAG = "OtpActivityTest";
    private Activity activity;
    private ActivityOtpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RetrofitApi mService;
    private String user_phone = "", country_code = "", user_id = "", user_email = "", user_name = "";

    //timer
    private CountDownTimer mCountDownTimer;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken token;
    private String otp;

    private ApiCallsSingleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        activity = this;
        mService = RetrofitClient.getAPI();
        mAuth = FirebaseAuth.getInstance();
        singleton = ApiCallsSingleton.getInstance(activity);

        initViewsClicks();
        initBundleDataViews();
    }

    @SuppressLint("SetTextI18n")
    private void initBundleDataViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user_phone = bundle.getString(Constants.PHONE);
            country_code = bundle.getString(Constants.COUNTRY_CODE);
            binding.tvPhone.setText(getString(R.string.enter_the_otp_sent_to) + " " + country_code + "-" + user_phone);
            requestOTP(country_code + user_phone);
            mTimeLeftInMillis = 60000;
            startTimer();
        }
    }

    private void initViewsClicks() {
        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                Toast.makeText(activity, "" + otp, Toast.LENGTH_SHORT).show();
            }
        });
        binding.cvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOtpEntered()) {
                    if (verificationId != null || !TextUtils.isEmpty(verificationId)) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                        verifyOtp(credential);
                    }

                }
            }
        });
        binding.tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTimeLeftInMillis = 60000;
                requestOTP(country_code + user_phone);
                startTimer();
                binding.tvResendOtp.setEnabled(false);
                binding.tvResendOtp.setTextColor(getResources().getColor(R.color.colorRedLight));
                Functions.ShowToast(activity, "Code resent");
            }
        });
    }


    /**
     * OTP methods
     */
    //====================== OTP request has sent=================//
    private void requestOTP(String phoneNumber) {
        binding.cvVerify.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(activity, "Otp Expired, re-request otp", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifyOtp(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


    // ============================ Verify Received Otp=================//
    @SuppressLint("SetTextI18n")
    private void verifyOtp(final PhoneAuthCredential phoneAuthCredential) {
        Log.d(TAG, "verifyOtp: " + phoneAuthCredential.getSmsCode());
        mCountDownTimer.cancel();
        binding.otpView.setOTP(phoneAuthCredential.getSmsCode());
        binding.tvDetectingOtpTime.setText("checking otp...");
        binding.tvDetectingOtpTime.setTextColor(Color.RED);
        binding.cvVerify.setVisibility(View.GONE);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mUser = mAuth.getCurrentUser();
                    if (mUser != null)
                        user_id = mUser.getUid();

                    binding.tvDetectingOtpTime.setText("OTP verified");
                    binding.tvDetectingOtpTime.setTextColor(getResources().getColor(R.color.colorText));
                    mCountDownTimer.cancel();



                        singleton.register(user_id, user_email, user_name, user_phone, Constants.PHONE,activity);


                } else {
                    Functions.ShowToast(activity, "Failed");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.tvResendOtp.setEnabled(true);
                binding.tvResendOtp.setTextColor(getResources().getColor(R.color.colorRed));
                binding.tvDetectingOtpTime.setText("failed to detect otp");
                binding.cvVerify.setVisibility(View.VISIBLE);
                Toast.makeText(activity, "failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Timer methods
     */
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                binding.tvResendOtp.setEnabled(true);
                binding.tvResendOtp.setTextColor(getResources().getColor(R.color.colorRed));
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void updateCountDownText() {
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted;

        timeLeftFormatted = String.format(Locale.getDefault(),
                "%02d", seconds);

        binding.tvDetectingOtpTime.setText("Detecting OTP : " + timeLeftFormatted);
    }

    //check if pin view is empty
    private boolean isOtpEntered() {
        try {
            otp = binding.otpView.getOTP();
            if (TextUtils.isEmpty(otp)) {
                Functions.ShowToast(activity, "Otp required");
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "isOtpEntered: exception" + e.getMessage());
        }

        return false;
    }
}