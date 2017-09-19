package com.github.dozzatq.phoenix.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.dozzatq.phoenix.drawable.FadingDrawable;

public class PhoenixImageView extends AppCompatImageView {
    public PhoenixImageView(Context context) {
        this(context, null);
    }

    public PhoenixImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhoenixImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Drawable placeholder = getDrawable();
        if (placeholder instanceof AnimationDrawable) {
            ((AnimationDrawable) placeholder).stop();
        }
    }

    public void loadBitmap(String url)
    {
        Glide.with(this.getContext())
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().fitCenter())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (dataSource == DataSource.LOCAL || dataSource == DataSource.DATA_DISK_CACHE
                                || dataSource == DataSource.MEMORY_CACHE || dataSource == DataSource.RESOURCE_DISK_CACHE) {
                            setImageBitmapWithoutAnimation(resource);
                        } else {
                            setImageBitmap(resource);
                        }
                        return true;
                    }
                })
                .into(this);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) FadingDrawable.setBitmap(this, getContext(), bitmap);
    }

    public void setImageBitmapWithoutAnimation(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
    }
}