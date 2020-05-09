package com.blondi.firebasemessenger.Network

import com.blondi.firebasemessenger.models.networkModels.ListOfGifs
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface GiphyAPI {
    @GET("gifs/search?api_key=0mTB76DriafisEc1lcRvjHZGSpHtvRgd&q=recoil&limit=10&offset=0&rating=G&lang=en")
 fun getGIfs() : Call<ListOfGifs>

    @GET("gifs/search")
    fun getGIFs(
        @Query("api_key") apiKey:String,
        @Query("q") searchFor:String,
        @Query("limit") limit:String
    ) : Call<ListOfGifs>
}
