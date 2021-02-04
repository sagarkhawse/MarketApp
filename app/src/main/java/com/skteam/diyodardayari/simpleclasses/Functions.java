package com.skteam.diyodardayari.simpleclasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.skteam.diyodardayari.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Functions {

    private static final String TAG = "FunctionsTest";
    public static void startEmailIntent(Activity context) {

        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",context.getString(R.string.contact_email), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }

    private static final DateFormat mDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static Date toDate(String input) {
        try {
            return mDateFormat.parse(input);
        } catch (ParseException ignore) {
        }

        return null;
    }

    public static void ShowToast(Context context, String toast) {
        Toast.makeText(context, "" + toast, Toast.LENGTH_SHORT).show();
    }

    public static String getAppVersion(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packageInfo != null;
        return packageInfo.versionName;
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


    public static long getDurationInt(String filePath) {
        MediaMetadataRetriever metaRetriever_int = new MediaMetadataRetriever();
        metaRetriever_int.setDataSource(filePath);
        String songDuration = metaRetriever_int.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        assert songDuration != null;
        long duration = Long.parseLong(songDuration);
        //int time = (int) (duration % 60000) / 1000;
        // close object
        long time = (int) duration / 1000;
        metaRetriever_int.release();
        return time;
    }

    public static int getFileSize(String file_path) {
        File file = new File(file_path);
        return (int) file.length() / (1024 * 1024);
    }


    public static void make_directory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);
    }

    @NonNull
    public static MultipartBody.Part prepareFilePart(Context context, String partName, String file_path) {
        File file;
//         use the FileUtils to get the actual file by uri
        try {
            String extension = file_path.substring(file_path.lastIndexOf("."));
            if (extension.equals(".mp4")) {
                file = new File(file_path);
            } else {

                file = FileUtils.getFile(context, Uri.parse(file_path));

                if (file == null) {
                    file = new File(file_path);
                }

            }

        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            file = FileUtils.getFile(context, Uri.parse(file_path));
        }


        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("*/*"),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public static Dialog determinant_dialog;
    public static ProgressBar determinant_progress;



    public static void Show_loading_progress(int progress) {
        if (determinant_progress != null) {
            determinant_progress.setProgress(progress);

        }
    }


    public static void cancel_determinent_loader() {
        if (determinant_dialog != null) {
            determinant_progress = null;
            determinant_dialog.cancel();
        }
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

    public static String dateFormaty(String date) {

        Calendar current_cal = Calendar.getInstance();

        Calendar date_cal = Calendar.getInstance();

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date d = null;
        try {
            d = f.parse(date);
            date_cal.setTime(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;

        if (difference < 86400) {
            if (current_cal.get(Calendar.DAY_OF_YEAR) - date_cal.get(Calendar.DAY_OF_YEAR) == 0) {

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return sdf.format(d);
            } else
                return "yesterday";
        } else if (difference < 172800) {
            return "yesterday";
        } else
            return (difference / 86400) + " day ago";


    }

    public static String GetSuffix(String value) {
        try {

            int count = Integer.parseInt(value);
            if (count < 1000) return "" + count;
            int exp = (int) (Math.log(count) / Math.log(1000));
            return String.format("%.1f %c",
                    count / Math.pow(1000, exp),
                    "kMGTPE".charAt(exp - 1));
        } catch (Exception e) {
            return value;
        }

    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String getAtRateTag(String str) {
        Pattern MY_PATTERN = Pattern.compile("(@[a-zA-Z0-9ğüşöçıİĞÜŞÖÇ]{2,50}\\b)");
        Matcher mat = MY_PATTERN.matcher(str);
        List<String> strs = new ArrayList<>();
        while (mat.find()) {
            strs.add(mat.group(1));
        }
        return strs.get(0);
    }

    public static List<String> getAtRateTags(String str) {
        Pattern MY_PATTERN = Pattern.compile("(@[a-zA-Z0-9ğüşöçıİĞÜŞÖÇ]{2,50}\\b)");
        Matcher mat = MY_PATTERN.matcher(str);
        List<String> strs = new ArrayList<>();
        while (mat.find()) {
            strs.add(mat.group(1));
        }
        return strs;
    }


    public static String toMMSS(long millis) {
        long mm = TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long ss = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        return String.format(Locale.US, "%02d:%02d", mm, ss);
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


    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }


    public static long getfileduration(Context context, Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            return file_duration;
        } catch (Exception e) {

        }
        return 0;
    }


}
