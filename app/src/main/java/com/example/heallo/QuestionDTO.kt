package com.example.heallo

import java.util.HashMap

data class QuestionDTO(
    var uid: String? = null,
    var userId: String? = null,
    var title : String? =null,
    var explain: String? = null,
    var timestamp: Long? = null,

) {

    data class Answer(
        var uid: String? = null,
        var userId: String? = null,
        var answer: String? = null,
        var timestamp: Long? = null
    )

}
