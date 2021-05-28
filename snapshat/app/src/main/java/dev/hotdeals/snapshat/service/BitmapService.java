package dev.hotdeals.snapshat.service;

import android.content.Context;
import android.graphics.Bitmap;
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
}
