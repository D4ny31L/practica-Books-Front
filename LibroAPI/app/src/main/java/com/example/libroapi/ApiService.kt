package com.example.libroapi

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/")
    fun getPosts(): Call<MutableList<PostModel>>

    @POST("/post")
    fun createPost(@Body postModel: PostModel): Call<PostModel>

    @PUT("/update")
    fun updatePost(@Body postModel: PostModel): Call<PostModel>

    @DELETE("/delete/{ISBN}")
    fun deletePost(@Path("ISBN") isbn: Long): Call<Void>

    @GET("/sortedname")
    fun getPostsSortn(): Call<MutableList<PostModel>>

    @GET("/sortedisbn")
    fun getPostsSorti(): Call<MutableList<PostModel>>

    @GET("/nombre/{Bookname}")
    fun searchPosts(@Path("Bookname") bookname: String): Call<MutableList<PostModel>>

    @GET("{ISBN}")
    fun searchPostsId(@Path("ISBN") ISBN: String): Call<MutableList<PostModel>>
}