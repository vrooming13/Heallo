package com.example.heallo

import java.util.HashMap

data class ContentDTO
    (
        var explain: String? = null,
        var imageUrl: String? = null,
        var uid: String? = null,
        var userId: String? = null,
        var timestamp: Long? = null,
        var favoriteCount: Int = 0,
        var favorites: MutableMap<String, Boolean> = HashMap(),
        var latitude: Double?= null,
        var longtiude: Double?= null,
        var address: String?=null,
        var rating:Float?=null
    ) {

        data class Comment(
            var uid: String? = null,
            var userId: String? = null,
            var comment: String? = null,
            var timestamp: Long? = null
        )

    }