package com.example.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface NewsApiService {
    @GET
    suspend fun fetchRssFeed(@Url url: String): Response<String>
}
