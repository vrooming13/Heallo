package com.example.heallo


data class ResultSearchGeo(
    var documents: List<TotalAddress>
)

data class TotalAddress(  // response 데이터 타입 확인결과 documents : TotalAddress 로 리턴됨을 확인
    var address : Address,  // 지번주소
    var road_address : RoadAddress  // 도로명주소
)

data class  Address (
    var address_name: String,
    var region_1depth_name: String,
    var region_2depth_name: String,
    var region_3depth_name: String,
    )

data class  RoadAddress (
    var address_name: String,
    var region_1depth_name: String,
    var region_2depth_name: String,
    var region_3depth_name: String,
)





