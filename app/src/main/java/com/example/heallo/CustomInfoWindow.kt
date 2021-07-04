package com.example.heallo

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.markerinfo.view.*

class CustomInfoWindow(val context : Context, val imageUri: String) : GoogleMap.InfoWindowAdapter{

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.markerinfo,null)


    private fun rendWindow(marker: Marker, view: View){
        val tvTitle = mWindow?.place_name
        val tvSnippet = mWindow?.place_content

        tvTitle!!.text = marker.title
        tvSnippet!!.text = marker.snippet
    }

    override fun getInfoWindow(p0: Marker): View? {
        rendWindow(p0,mWindow)
        return null
    }

    override fun getInfoContents(p0: Marker): View? {
        rendWindow(p0,mWindow)
        Glide.with(context)
            .load(imageUri)
            .override(200,200)
            .listener(MarkerCallback(p0))
            .into(mWindow.place_image)
        return mWindow
    }

    class MarkerCallback internal constructor(marker: Marker?) :
        RequestListener<Drawable>{

        var marker: Marker? = null

        private fun onSuccess() {
            if (marker != null && marker!!.isInfoWindowShown) {
                Handler(Looper.getMainLooper()).post {
                    marker!!.showInfoWindow()
                }
            }
        }
        init {
            this.marker = marker
        }
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean,
        ): Boolean {
            Log.i("ERROR IMAGE","ERRROR")
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            onSuccess()
            return false
        }

    }

}