package pro.vylgin.cameraarcsinus.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final String MEDIA_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator +
            "CameraArcsinus" +
            File.separator;

    public static enum MediaType {AUDIO, VIDEO}

    public static File getOutputMediaFile(MediaType mediaType) {
        File mediaStorageDir = new File(Utils.MEDIA_DIR);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        final String dateMask = "yyyyMMdd_HHmmss";
        File mediaFile = null;

        switch (mediaType) {
            case AUDIO:
                String timeStamp = new SimpleDateFormat(dateMask).format(new Date());
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "AUD_" + timeStamp + ".mp3");
                break;
            case VIDEO:
                timeStamp = new SimpleDateFormat(dateMask).format(new Date());
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
                break;
        }

        return mediaFile;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);

        if(cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }

        cursor.close();

        return res;
    }

}
