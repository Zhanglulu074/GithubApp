package com.example.githubapplication

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class BindUtil {

    companion object {
        @JvmStatic
        @BindingAdapter("iconUri")
        public fun setIconUri(img: ImageView, iconUri: String) {
            val options: RequestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(RoundedCornersTransformation(30, 0))
            Glide.with(img.context).load(Uri.parse(iconUri)).apply(options).into(img)
        }
    }
}