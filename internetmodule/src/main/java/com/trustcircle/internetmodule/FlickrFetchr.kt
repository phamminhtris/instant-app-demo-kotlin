package com.trustcircle.internetmodule

import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Created by tripham on 8/4/17.
 */
/**
 * Created by tripham on 7/27/17.
 */

class FlickrFetchr {

    @Throws(IOException::class)
    fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection

        try {
            val out = ByteArrayOutputStream()
            val inStream = connection.inputStream

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException(connection.responseMessage + ": with " + urlSpec)
            }

            val buffer = ByteArray(1024)
            while(true) {
                val length = inStream.read(buffer)
                if(length <= 0) {
                    break
                }
                out.write(buffer, 0, length)
            }
            out.close()
            return out.toByteArray()
        } finally {
            connection.disconnect()
        }


    }

    @Throws(IOException::class)
    fun getUrlString(urlSpec: String): String {
        return String(getUrlBytes(urlSpec))
    }

    fun fetchRecentPhotos(): List<GalleryItem> {
        val url = buildUrl(FETCH_RECENTS_METHOD, null)
        return downloadGalleryItems(url)
    }

    fun searchPhotos(query: String): List<GalleryItem> {
        val url = buildUrl(SEARCH_METHOD, query)
        return downloadGalleryItems(url)
    }

    private fun downloadGalleryItems(url: String): List<GalleryItem> {

        val items = ArrayList<GalleryItem>()

        try {

            val jsonString = getUrlString(url)
            Log.i(TAG, "Received JSON: " + jsonString)
            val jsonBody = JSONObject(jsonString)

            parseItems(items, jsonBody)

        } catch (ioe: IOException) {
            Log.e(TAG, "Failed to fetch items", ioe)
        } catch (je: JSONException) {
            Log.e(TAG, "Failed to parse JSON", je)
        }

        return items
    }

    private fun buildUrl(method: String, query: String?): String {
        val uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method)
        if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text", query)
        }
        Log.i("URL", "URL: " + uriBuilder.build().toString())
        return uriBuilder.build().toString()
    }

    @Throws(IOException::class, JSONException::class)
    private fun parseItems(items: MutableList<GalleryItem>, jsonBody: JSONObject) {

        val photosJsonObject = jsonBody.getJSONObject("photos")
        val photoJsonArray = photosJsonObject.getJSONArray("photo")
        for (i in 0..photoJsonArray.length() - 1) {
            val photoJsonObject = photoJsonArray.getJSONObject(i)
            val item = GalleryItem()
            item.id = photoJsonObject.getString("id")
            item.caption = photoJsonObject.getString("title")
            if (!photoJsonObject.has("url_s")) {
                continue
            }
            item.url = photoJsonObject.getString("url_s")
            items.add(item)
        }

    }

    companion object {

        private val TAG = FlickrFetchr::class.java.name
        private val API_KEY = "296280cb91e69005219b062344aff4bb"
        private val FETCH_RECENTS_METHOD = "flickr.photos.getRecent"
        private val SEARCH_METHOD = "flickr.photos.search"
        private val ENDPOINT = Uri
                .parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .build()
    }
}
