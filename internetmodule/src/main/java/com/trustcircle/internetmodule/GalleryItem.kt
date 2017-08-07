package com.trustcircle.internetmodule

/**
 * Created by tripham on 8/4/17.
 */



class GalleryItem {
    var caption: String? = null
    var id: String? = null
    var url: String? = null

    override fun toString(): String {
        return caption!!
    }
}
