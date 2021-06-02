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

// Service/Utility class that contains methods for manipulating bitmaps
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

    /**
     * Adds text to a bitmap around the bottom quarter of the image
     *
     * @param image   Bitmap to be modified
     * @param caption Text to put in the image
     * @return A modified Bitmap with the text
     */
    public static Bitmap drawTextToBitmap(Bitmap image, String caption) {
        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable,
        // so we need to convert it to mutable one
        image = image.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(image);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(161, 161, 161));
        // Set the text size relative to picture height
        paint.setTextSize(canvas.getHeight() >> 4);
        // Set text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);
        // draw the text in the bottom quarter and starting from the left side of the image
        canvas.drawText(caption, canvas.getWidth() >> 3, (canvas.getHeight() >> 2) * 3, paint);
        return image;
    }
}
