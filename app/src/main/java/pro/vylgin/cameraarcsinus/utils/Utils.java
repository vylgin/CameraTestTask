package pro.vylgin.cameraarcsinus.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import pro.vylgin.cameraarcsinus.model.MediaContent;

public class Utils {

    public static final String MEDIA_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator +
            "CameraArcsinus" +
            File.separator;

    public static enum MediaType {AUDIO, VIDEO}

    public static final int ALL_MEDIAFILES_PISITION = 0;
    public static final int AUDIO_PISITION = 1;
    public static final int VIDEO_PISITION = 2;

    public static final String AUD = "AUD";
    public static final String VID = "VID";

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
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + AUD + "_" + timeStamp + ".mp3");
                break;
            case VIDEO:
                timeStamp = new SimpleDateFormat(dateMask).format(new Date());
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + VID + "_" + timeStamp + ".mp4");
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

    public static void updateMediaContent(int currentSpinnerPosition) {
        MediaContent.ITEMS.clear();
        MediaContent.ITEM_MAP.clear();

        File dir = new File(Utils.MEDIA_DIR);
        File[] filelist = dir.listFiles();
        switch (currentSpinnerPosition) {
            case ALL_MEDIAFILES_PISITION:
                for (int i = 0; i < filelist.length; i++) {
                    MediaContent.addItem(new MediaContent.MediaItem(i, filelist[i].getName()));
                }
                break;
            case AUDIO_PISITION:
                for (int i = 0; i < filelist.length; i++) {
                    if (filelist[i].getName().contains(AUD)) {
                        MediaContent.addItem(new MediaContent.MediaItem(i, filelist[i].getName()));
                    }
                }
                break;
            case VIDEO_PISITION:
                for (int i = 0; i < filelist.length; i++) {
                    if (filelist[i].getName().contains(VID)) {
                        MediaContent.addItem(new MediaContent.MediaItem(i, filelist[i].getName()));
                    }
                }
                break;
        }
    }

}
