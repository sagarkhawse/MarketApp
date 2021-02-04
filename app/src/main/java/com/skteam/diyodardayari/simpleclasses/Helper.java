package com.skteam.diyodardayari.simpleclasses;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skteam.diyodardayari.models.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * A class that contains various functions for help in progaramming
 */
public class Helper {

    private static String lastFileName = "";
    private static int lastFileNameIndex;

    /**
     * A function that takes {@link Activity} as a parameter and closes the keyboard
     *
     * @param activity The current activity
     */
    public static void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void closeKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * A function that takes {@link Activity} as a parameter and opens the keyboard
     *
     * @param activity The current activity
     */
    public static void openKeyboard(final Activity activity) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                View view = activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(view, 0);
                }
            }
        });
    }

    /**
     * Removes the toolbar scroll flags
     *
     * @param activity The {@link Activity} comprising the toolbar
     * @param toolbar  The {@link Toolbar} for which scroll flags have to be removed
     */
    public static void removeToolbarFlags(Activity activity, Toolbar toolbar) {
        if (toolbar != null) {
            final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);
            toolbar.setLayoutParams(params);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        }
    }

    /**
     * Sets the toolbar scroll flags
     *
     * @param activity The {@link Activity} comprising the toolbar
     * @param toolbar  The {@link Toolbar} for which scroll flags have to be set
     */
    public static void setToolbarFlags(Activity activity, Toolbar toolbar) {
        if (toolbar != null) {
            final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        }
    }

    /**
     * A function to convert dp dimension to px
     *
     * @param context
     * @param dp      The value in dp
     * @return int The value in px
     */
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * A function to convert px dimension to dp
     *
     * @param context
     * @param px      The value in px
     * @return int The value in dp
     */
    public static int pxToDp(Context context, int px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    /**
     * Creates and returns the Facebook {@link Intent} object
     *
     * @param pm  The {@link PackageManager}
     * @param url The facebook url
     * @return Facebook {@link Intent}
     */
    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }



    /**
     * Creates bitmap from the view supplied
     *
     * @param v View
     * @return {@link Bitmap}
     */
    private static Bitmap loadBitmapFromView(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            v.setDrawingCacheEnabled(true);
            return v.getDrawingCache();
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    /**
     * Opens the play store for this app
     *
     * @param context
     */
    public static void openPlayStoreIntent(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    /**
     * This function copies file from source to destination
     *
     * @param src Source file
     * @param dst Destination file
     * @throws IOException
     */
    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    /**
     * Saves the bitmap to a file with the supplied filename if no such file exists or creates a new file by modifying the filename
     *
     * @param context
     * @param bitmap   The {@link Bitmap} to be saved
     * @param fileName The name of the file
     * @throws IOException
     */
    public static void saveBitmap(Context context, @NonNull Bitmap bitmap, @NonNull String fileName) throws IOException {
        if (lastFileName.equals(fileName)) {
            fileName = lastFileNameIndex++ + fileName;
        } else {
            lastFileName = fileName;
        }

        File pictureFile = new File(getRootDir(), fileName);
        saveBitmapToFile(bitmap, pictureFile);
        MediaScannerConnection.scanFile(context, new String[]{pictureFile.getAbsolutePath()}, new String[]{"icon_picture/jpeg"}, null);
    }

    /**
     * Saves the bitmap to a temporary file in private directory with the supplied filename
     *
     * @param activity
     * @param bitmap   The {@link Bitmap} to be saved
     * @param fileName The name of the file
     * @throws IOException
     */
    public static void saveBitmapTemporary(Activity activity, Bitmap bitmap, String fileName) throws IOException {
        File pictureFile = new File(activity.getExternalFilesDir(null), fileName);
        saveBitmapToFile(bitmap, pictureFile);
        pictureFile.deleteOnExit();
    }

    /**
     * Saves the bitmap to the filename supplied
     *
     * @param bitmap      {@link Bitmap} to be saved
     * @param pictureFile The name of the file
     * @throws IOException
     */
    public static void saveBitmapToFile(Bitmap bitmap, File pictureFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        fos.close();
    }

    public static String timeFormater(float time) {
        Long secs = (long) (time / 1000);
        Long mins = (long) ((time / 1000) / 60);
        Long hrs = (long) (((time / 1000) / 60) / 60); /* Convert the seconds to String * and format to ensure it has * a leading zero when required */
        secs = secs % 60;
        String seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        } /* Convert the minutes to String and format the String */
        mins = mins % 60;
        String minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        } /* Convert the hours to String and format the String */
        String hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

        return hours + ":" + minutes + ":" + seconds;
//        String milliseconds = String.valueOf((long) time);
//        if (milliseconds.length() == 2) {
//            milliseconds = "0" + milliseconds;
//        }
//        if (milliseconds.length() <= 1) {
//            milliseconds = "00";
//        }
//        milliseconds = milliseconds.substring(milliseconds.length() - 3, milliseconds.length() - 2); /* Setting the timer text to the elapsed time */
    }

    /**
     * Returns the date in relative time like "5 mins ago"
     *
     * @param date The date
     * @return {@link CharSequence} relative time
     */
    public static CharSequence timeDiff(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date startDate = new Date();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            startDate = simpleDateFormat.parse(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return DateUtils.getRelativeTimeSpanString(startDate.getTime(), calendar.getTimeInMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public static CharSequence timeDiff(Long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return DateUtils.getRelativeTimeSpanString(new Date(milliseconds).getTime(), calendar.getTimeInMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    /**
     * Gets the root external public directory
     *
     * @return root directory
     */
    public static File getRootDir() {
        File root = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Foxy");
        root.mkdirs();
        return root;
    }

    /**
     * A function used to enable {@link android.content.BroadcastReceiver}
     *
     * @param context
     * @param mClass  BroadcastReceiver class
     */
    public static void enableBroadcastReceiver(Context context, Class mClass) {
        Log.d("receiver", "enable");
        ComponentName receiver = new ComponentName(context, mClass);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * A function used to disable {@link android.content.BroadcastReceiver}
     *
     * @param context
     * @param mClass  BroadcastReceiver class
     */
    public static void disableBroadcastReceiver(Context context, Class mClass) {
        Log.d("receiver", "disable");
        ComponentName receiver = new ComponentName(context, mClass);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Returns the current fragment from the {@link FragmentManager}
     *
     * @param activity The current {@link Activity}
     * @return Current {@link Fragment}
     */
    public static Fragment getCurrentFragment(AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        return fragmentManager.findFragmentByTag(tag);
    }

//    public static ArrayList<Post> getBookmarkedPosts(SharedPreferenceUtil sharedPreferenceUtil) {
//        ArrayList<Post> toReturn = new ArrayList<>();
//        UserResponse user = getLoggedInUser(sharedPreferenceUtil);
//        String savedBookmarkedPostsString = sharedPreferenceUtil.getStringPreference((user != null ? user.getId() : "-1") + Constants.KEY_BOOKMARK, null);
//        if (savedBookmarkedPostsString != null) {
//            toReturn = new Gson().fromJson(savedBookmarkedPostsString, new TypeToken<ArrayList<Post>>() {
//            }.getType());
//        }
//        return toReturn;
//    }
//
//    public static void setBookmarkedPosts(SharedPreferenceUtil sharedPreferenceUtil, ArrayList<Post> bookmarkedPosts) {
//        UserResponse user = getLoggedInUser(sharedPreferenceUtil);
//        sharedPreferenceUtil.setStringPreference((user != null ? user.getId() : "-1") + Constants.KEY_BOOKMARK, new Gson().toJson(bookmarkedPosts, new TypeToken<ArrayList<Post>>() {
//        }.getType()));
//    }

    public static void saveProfileDetails(SharedPreferenceUtil sharedPreferenceUtil, User userMe) {
        sharedPreferenceUtil.setStringPreference(Constants.KEY_USER_PROFILE, new Gson().toJson(userMe, new TypeToken<User>() {
        }.getType()));
    }

    public static User getProfileDetails(SharedPreferenceUtil sharedPreferenceUtil) {
        User toReturn = null;
        String savedUserString = sharedPreferenceUtil.getStringPreference(Constants.KEY_USER_PROFILE, null);
        if (savedUserString != null)
            toReturn = new Gson().fromJson(savedUserString, new TypeToken<User>() {
            }.getType());
        return toReturn;
    }

    static public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public static String getAudioLength(Context context, Uri uri) {
        String length = "";
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int millis = Integer.parseInt(durationStr);
            length = TimeUnit.MILLISECONDS.toMinutes(millis) + ":" + TimeUnit.MILLISECONDS.toSeconds(millis);
            mmr.release();
        } catch (Exception e) {
        }
        return length;
    }


    public static void setLoggedInUser(SharedPreferenceUtil sharedPreferenceUtil, User user) {
        sharedPreferenceUtil.setStringPreference(Constants.USER, new Gson().toJson(user, new TypeToken<User>() {
        }.getType()));
    }

    public static User getLoggedInUser(SharedPreferenceUtil sharedPreferenceUtil) {
        String savedUserPref = sharedPreferenceUtil.getStringPreference(Constants.USER, null);
        if (savedUserPref != null)
            return new Gson().fromJson(savedUserPref, new TypeToken<User>() {
            }.getType());
        else return null;
    }



    public static int getDisplayWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static String getTime(long milliseconds) {
        return new SimpleDateFormat("dd MMM kk:mm", Locale.getDefault()).format(new Date(milliseconds));
    }



    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

}
