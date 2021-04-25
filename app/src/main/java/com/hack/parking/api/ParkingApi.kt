package com.hack.parking.api

import retrofit2.http.GET

interface ParkingApi {

    @GET("parking")
    suspend fun getParking()
}