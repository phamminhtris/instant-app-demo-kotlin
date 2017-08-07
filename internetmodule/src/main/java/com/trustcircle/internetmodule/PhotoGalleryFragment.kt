package com.trustcircle.internetmodule

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.ImageView
import java.util.ArrayList

/**
 * Created by tripham on 8/4/17.
 */
class PhotoGalleryFragment : Fragment() {

    private var mPhotoRecyclerView: RecyclerView? = null
    private var mItems: List<GalleryItem> = ArrayList()
    private var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
        updateItems()

        val responseHandler = Handler()
        mThumbnailDownloader = ThumbnailDownloader(responseHandler)
        mThumbnailDownloader!!.setThumbnailDownloadListener(
                object : ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder> {
                    override fun onThumbnailDownloaded(photoHolder: PhotoHolder, bitmap: Bitmap) {
                        if (this@PhotoGalleryFragment.isAdded) {
                            val drawable = BitmapDrawable(resources, bitmap)
                            photoHolder.bindDrawable(drawable)
                        }
                    }
                }
        )

        mThumbnailDownloader!!.start()
        mThumbnailDownloader!!.getLooper()
        Log.i(TAG, "Background thread started")
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem = menu!!.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                Log.d(TAG, "QueryTextSubmit: " + s)
                QueryPreferences.setStoredQuery(activity, s)
                updateItems()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                Log.d(TAG, "QueryTextChange: " + s)
                return false
            }
        })

        searchView.setOnSearchClickListener {
            val query = QueryPreferences.getStoredQuery(activity)
            searchView.setQuery(query, false)
        }
    }


    private fun updateItems() {
        val query = QueryPreferences.getStoredQuery(activity)
        FetchItemsTask(query).execute()
    }

    override fun onDestroy() {
        super.onDestroy()
        mThumbnailDownloader!!.quit()
        Log.i(TAG, "Background thread destroyed")
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_photo_gallery, container, false)
        mPhotoRecyclerView = v
                .findViewById<View>(R.id.fragment_photo_gallery_recycler_view) as RecyclerView
        mPhotoRecyclerView!!.setLayoutManager(GridLayoutManager(activity, 3))

        setupAdapter()
        return v
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mThumbnailDownloader!!.clearQueue()
    }

    private fun setupAdapter() {
        if (isAdded) {
            mPhotoRecyclerView!!.setAdapter(PhotoAdapter(mItems))
        }
    }

    private inner class FetchItemsTask(private val mQuery: String) : AsyncTask<Void, Void, List<GalleryItem>>() {

        override fun doInBackground(vararg params: Void): List<GalleryItem> {

            if (mQuery == "") {
                return FlickrFetchr().fetchRecentPhotos()
            } else {
                return FlickrFetchr().searchPhotos(mQuery)
            }

        }

        override fun onPostExecute(galleryItems: List<GalleryItem>) {
            mItems = galleryItems
            setupAdapter()
        }
    }


    private inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mItemImageView: ImageView

        init {

            mItemImageView = itemView.findViewById<View>(R.id.fragment_photo_gallery_image_view) as ImageView
        }

        fun bindDrawable(drawable: Drawable) {
            mItemImageView.setImageDrawable(drawable)
        }
    }

    private inner class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {
        override fun getItemCount(): Int {
            return mGalleryItems.size
        }


        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoHolder {
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.gallery_item, viewGroup, false)
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(photoHolder: PhotoHolder, position: Int) {
            val galleryItem = mGalleryItems[position]
            mThumbnailDownloader!!.queueThumbnail(photoHolder, galleryItem.url)
        }

    }

    companion object {

        private val TAG = PhotoGalleryFragment::class.java.name

        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment()
        }
    }
}
