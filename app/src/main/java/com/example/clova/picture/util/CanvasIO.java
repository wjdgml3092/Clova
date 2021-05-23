package com.example.clova.picture.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CanvasIO {
    public static void saveBitmapToJpg(Bitmap bitmap, Context context) {
        Log.d("context2",context.toString());
        Log.d("saveBitmap",bitmap.toString());
        String root = Environment.getExternalStorageDirectory().toString();
        Log.d("context_root",root);
        File myDir = new File(root + "/clova");
        myDir.mkdir();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "clova" + timeStamp + ".jpg";

        File file = new File(myDir, fname);
        if(file.exists())file.delete();

        try{
            Log.d("여기는?", "들어오나?");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            Log.d("여기는2?", "compress는? 들어오나?");
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            out.flush();
            out.close();
        }catch(Exception e) {
            Log.e("MyTag","FileNotFoundException : " + e.getMessage());
        }
    }
    /*
    private final static String TAG = Canvas.class.getName();
    private final static String FILE_NAME = "draw_file.jpg";

    public static void saveBitmap(Context context, Bitmap saveFile) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            saveFile.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Don't save canvas");
            e.printStackTrace();
        }
    }
/*
    public static Bitmap openBitmap(Context context) {
        Bitmap result = null;

        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            result = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (IOException e) {
            Log.e(TAG, "Don't open canvas");
            e.printStackTrace();
        }

        return result;
    }

 */
}
