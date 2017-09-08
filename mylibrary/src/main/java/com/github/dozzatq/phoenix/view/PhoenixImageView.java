package com.github.dozzatq.phoenix.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        setImageBitmap(resource);
                    }

                });
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) FadingDrawable.setBitmap(this, getContext(), bitmap);
    }

    public void setImageBitmapWithoutAnimation(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
    }
}