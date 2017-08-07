package com.trustcircle.internetmodule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by tripham on 8/4/17.
 */
class ThumbnailDownloader<T>(private val mResponseHandler: Handler) : HandlerThread(TAG) {

    private var mRequestHandler: Handler? = null
    private val mRequestMap = ConcurrentHashMap<T, String>()
    private var mThumbnailDownloadListener: ThumbnailDownloadListener<T>? = null


    interface ThumbnailDownloadListener<T> {
        fun onThumbnailDownloaded(target: T, thumbnail: Bitmap)
    }

    fun setThumbnailDownloadListener(listener: ThumbnailDownloadListener<T>) {
        mThumbnailDownloadListener = listener
    }

    fun queueThumbnail(target: T, url: String?) {
        Log.i(TAG, "Got a URL: " + url!!)

        if (url == null) {
            mRequestMap.remove(target)
        } else {
            mRequestMap.put(target, url)
            mRequestHandler!!.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget()
        }
    }

    override fun onLooperPrepared() {
        mRequestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: " + mRequestMap[target])
                    handleRequest(target)
                }
            }
        }
    }

    fun clearQueue() {
        mRequestHandler!!.removeMessages(MESSAGE_DOWNLOAD)
    }


    private fun handleRequest(target: T) {
        try {
            val url = mRequestMap[target] ?: return
            val bitmapBytes = FlickrFetchr().getUrlBytes(url)
            val bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
            Log.i(TAG, "Bitmap created")

            mResponseHandler.post(Runnable {
                if (mRequestMap[target] !== url) {
                    return@Runnable
                }
                mRequestMap.remove(target)
                mThumbnailDownloadListener!!.onThumbnailDownloaded(target, bitmap)
            })

        } catch (ioe: IOException) {
            Log.e(TAG, "Error downloading image", ioe)
        }

    }

    companion object {
        private val TAG = "ThumbnailDownloader"
        private val MESSAGE_DOWNLOAD = 0
    }
}