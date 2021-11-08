package com.example.heallo

class ListLayout(
    val name: String,      // 장소명
                 val road: String,      // 도로명 주소
                 val address: String,   // 지번 주소
                 val x: Double,         // 경도(Longitude)
                 val y: Double    // 위도(Latitude)
)


class geo(
    var address_name: String,
    var region_1depth_name: String,
    var region_2depth_name: String,
    var region_3depth_name: String
)
