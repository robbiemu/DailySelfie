package space.selfenrichment.dailyselfie.lib;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static space.selfenrichment.dailyselfie.lib.Defs.*;

/**
 * Created by roberto on 12/08/2017.
 */

public class ImagesFS {
    private static final String APP_EXTERNAL_ROOT = "DailySelfie";
    private static final String APP_IMAGES_DIRECTORY = "Pictures";
    private static final String IMAGES_DIR = "DailySelfie/Pictures";

    public static void init () {

        Log.i(TAG, "init - checking if external filesystem has DailySelfie directories");

        File f = new File(Environment.getExternalStorageDirectory(), APP_EXTERNAL_ROOT);
        if (!f.exists()) {
            Log.i(TAG, "init - checking that external filesystem has no DailySelfie directory");
            f.mkdirs();
        }
        f = new File(Environment.getExternalStorageDirectory().toString(), IMAGES_DIR);
        if (!f.exists()) {
            Log.i(TAG, "init - checking that external filesystem has no DailySelfie/Pictures directory");
            f.mkdirs();
        }
    }

    public static String[] list(Context context) {
        Log.i(TAG, "list - finding a list of files in the DailySelfie Pictures directory");

        ContentResolver cr = context.getContentResolver();

        File file = new File(Environment.getExternalStorageDirectory(), IMAGES_DIR);
        File[] ifs = file.listFiles();

        List<String> listOfFilenames = new ArrayList<>();
        if(ifs != null && ifs.length > 0) {
            for (File f : ifs) {
                listOfFilenames.add(f.getName());
            }
            return listOfFilenames.toArray(new String[ifs.length]);
        } else {
            return new String[]{};
        }
    }

    public static void loadImageToView(Context context, ImageView image, String filename) {
        Log.i(TAG, "loadImageToView filename: " + filename);

        Uri fileUri = Uri.fromFile(new File(
                Environment.getExternalStorageDirectory().toString() + "/" + IMAGES_DIR + "/" +
                filename));
        context.getContentResolver().notifyChange(fileUri, null);
        ContentResolver cr = context.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, fileUri);

            image.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e(Defs.TAG, e.toString());
        }

    }

    @NonNull
    public static String computeFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        return "Img" + formatter.format(now) + ".jpg";
    }

    public static Uri prepareFileForPhoto() {
        String fileName = ImagesFS.computeFileName();
        File photo = new File(Environment.getExternalStorageDirectory(), IMAGES_DIR + "/" +
                fileName);
        return Uri.fromFile(photo);
    }

    public static boolean freeUp(String filename) {
        File file = new File(filename);
        return file.delete();
    }

    public static String getName(String fileUriString) {
        Uri uri = Uri.parse(fileUriString);
        File fh = new File(uri.getPath());
        return fh.getName();
    }

    public static void freeAll() {
        File dir = new File(Environment.getExternalStorageDirectory(), IMAGES_DIR);
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            new File(dir, children[i]).delete();
        }
    }
}
