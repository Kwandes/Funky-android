package dev.hotdeals.snapshat.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

// Service class that contains methods for manipulating bitmaps
public class BitmapService {

    // takes in a bitmap and blurs it
    public static Bitmap blur(Context context, Bitmap bitmap, float blurRadius) {
        if (bitmap != null) {
            Bitmap copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap outputBitmap = Bitmap.createBitmap(copyBitmap);

            RenderScript renderScript = RenderScript.create(context);
            ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

            Allocation allocationIn = Allocation.createFromBitmap(renderScript, bitmap);
            Allocation allocationOut = Allocation.createFromBitmap(renderScript, outputBitmap);

            scriptIntrinsicBlur.setRadius(blurRadius);
            scriptIntrinsicBlur.setInput(allocationIn);
            scriptIntrinsicBlur.forEach(allocationOut);

            allocationOut.copyTo(outputBitmap);

            return outputBitmap;
        } else {
            return null;
        }
    }

    // adds text to a bitmap
    public static Bitmap drawTextToBitmap(Bitmap image, String gText) {
        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable,
        // so we need to convert it to mutable one
        image = image.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// new antialised Paint
        paint.setColor(Color.rgb(161, 161, 161));
        paint.setTextSize(canvas.getHeight() >> 4); // text size in pixels
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK); // text shadow
        // draw the text in the bottom quarter and starting from the left side of the image
        canvas.drawText(gText, canvas.getWidth() >> 3, (canvas.getHeight() >> 2) * 3, paint);
        return image;
    }
}
