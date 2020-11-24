package com.caryatri.caryatri.Common;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.caryatri.caryatri.Database.CurrentDriver.CurrentDriverRepository;
import com.caryatri.caryatri.Database.DataSource.RoundWay.CabRoundWayRepository;
import com.caryatri.caryatri.Database.DataSource.RoundWay.UserData.UserDataRoundWayRepository;
import com.caryatri.caryatri.Database.Local.SNRoomDatabase;
import com.caryatri.caryatri.Database.Notification.NotificationRepository;
import com.caryatri.caryatri.Database.OneWay.CabOneWayRepository;
import com.caryatri.caryatri.Database.OneWay.UserData.UserDataOneWayRepository;
import com.caryatri.caryatri.ForgotActivity;
import com.caryatri.caryatri.OTPActivity;
import com.caryatri.caryatri.R;
import com.caryatri.caryatri.RegisterActivity;
import com.caryatri.caryatri.model.TripDetailOneWay;
import com.caryatri.caryatri.model.TripDetailRoundWay;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.retrofit.FCMClient;
import com.caryatri.caryatri.retrofit.ICarYatri;
import com.caryatri.caryatri.retrofit.IFCMService;
import com.caryatri.caryatri.retrofit.IGoogleAPI;
import com.caryatri.caryatri.retrofit.RetrofitClient;
import com.caryatri.caryatri.retrofit.TwilioApi;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Common {

    //new added
    public static String CabType;
    //old
    public static final String OK = "OK";
    public static SNRoomDatabase snRoomDatabase;
    public static CabRoundWayRepository cabRoundWayRepository;
    public static CabOneWayRepository cabOneWayRepository;
    public static NotificationRepository notificationRepository;
    public static CurrentDriverRepository currentDriverRepository;
    public static UserDataOneWayRepository userDataOneWayRepository;
    public static UserDataRoundWayRepository userDataRoundWayRepository;
    public static RelativeLayout rootLayout;
    public static double distanceValue;
    public static String fromActivity, AuthPhone;
    public static String WayActivity;
    public static String Date, Time;
    public static String Date1, Date2, Time1;
    public static String name, phone, email;
    public static List<String> cabImg;
    public static String status = "ok";
    public static String BASE_URL = "https://myinvented.com/urdriver/";
    public static User currentUser;
    public static User register;
    public static TripDetailOneWay tripDetailOneWay;
    public static TripDetailRoundWay tripDetailRoundWay;
    public static CharSequence SearchedTextFrom = null, SearchedTextTo = null;
//    public static String BookNow;

    private static final String FCM_API = "https://fcm.googleapis.com/";
    public static String id;
    public static String image;

    public static IFCMService getGetFCMService() {
        return FCMClient.getClient(FCM_API).create(IFCMService.class);
    }

    public static void setBack(Activity context) {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setTop(Activity context) {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(context, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            context.getWindow().setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static ICarYatri getAPI() {
        return RetrofitClient.getClient(BASE_URL).create(ICarYatri.class);
    }

    public static String verificationCode;
    public static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    public static void sendOTP(Activity context, String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber("+91" + phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(context)                 // Activity (for callback binding)
                        .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public static void StartFirebaseLogin(final Context context) {

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                Toast.makeText(context, "verification completed", Toast.LENGTH_SHORT).show();
//                SigninWithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("ERROR", e.getMessage());
                Toast.makeText(context, "verification failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(context, "OTP sent ", Toast.LENGTH_SHORT).show();
                if (fromActivity.equals("reg"))
                    start_Activity(context);
                else if (fromActivity.equals("fog"))
                    start_OTPActivity(context);
                else if (fromActivity.equals("pro"))
                    start_OTP(context);
            }
        };
    }

    public static void start_Activity(Context context) {
        Intent intent = new Intent(context, OTPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(RegisterActivity.logo, "imageTransition");
        ActivityOptions options;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
            context.startActivity(intent, options.toBundle());
        } else
            context.startActivity(intent);
    }

    public static void start_OTPActivity(Context context) {
        Intent intent = new Intent(context, OTPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(ForgotActivity.logo, "imageTransition");
        ActivityOptions options;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
            context.startActivity(intent, options.toBundle());
        } else
            context.startActivity(intent);
    }

    public static void start_OTP(Context context) {
        Intent intent = new Intent(context, OTPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    public static final String baseURL = "https://maps.googleapis.com";

    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    //sendnig sms
    public static final String ACCOUNT_SID = "AC77604927a74092a058b4a0cc73cfc3bd";
    public static final String AUTH_TOKEN = "88d03b831fa871cdf752eecdb26cd247";

    public static void sendMessage(String Message, String Phone) {
        String body = Message;
        String from = "+19143420044";
        String to = "+19142788368";

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
        );

        Map<String, String> smsData = new HashMap<>();
        smsData.put("From", from);
        smsData.put("To", to);
        smsData.put("Body", body);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twilio.com/2010-04-01/")
                .build();
        TwilioApi api = retrofit.create(TwilioApi.class);

        api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, smsData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) Log.d("TAG", response.message());
                else Log.d("TAG", response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TAG", "onFailure");
            }
        });
    }

    public static int getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        java.util.Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ((int) dayCount + 1);
    }

}
