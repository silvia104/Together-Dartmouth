package edu.dartmouth.cs.together.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Helper {
    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public static SpannableString getUnderlinedString(String str){
        SpannableString content = new SpannableString(str);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    public static Bitmap DownloadImage(String urlImage){
        Bitmap bitmap;

        try {
            // Download the image
            URL url = new URL(urlImage);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            // Decode image to get smaller image to save memory
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize=2;
            bitmap = BitmapFactory.decodeStream(is,null, options);
            is.close();
        }
        catch(IOException e){
            return null;
        }
        return bitmap;
    }
}
