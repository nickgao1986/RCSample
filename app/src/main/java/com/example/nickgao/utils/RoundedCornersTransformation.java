package com.example.nickgao.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

/**
 * Created by Antonenko Viacheslav on 02/10/15.
 */
public class RoundedCornersTransformation implements Transformation<Bitmap> {

    private BitmapPool mBitmapPool;

    private int mRadius;
    private int mMargin;

    public RoundedCornersTransformation(Context context, int radius, int margin) {
        this(Glide.get(context).getBitmapPool(), radius, margin);
    }

    public RoundedCornersTransformation(BitmapPool pool, int radius, int margin) {
        mBitmapPool = pool;
        mRadius = radius;
        mMargin = margin;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        final Bitmap source = resource.get();

        final int width = source.getWidth();
        final int height = source.getHeight();

        Bitmap bitmap = mBitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(mMargin, mMargin, width - mMargin, height - mMargin), mRadius, mRadius, paint);

        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override
    public String getId() {
        return "RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin + ")";
    }

}
