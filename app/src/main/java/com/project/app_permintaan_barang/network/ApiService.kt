package com.project.app_permintaan_barang.network

import com.project.app_permintaan_barang.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("api/auth0/v1/login")
    fun loginUser(@Body requestBody: Map<String, String>): Call<ResponseData>

    @Headers("Content-Type: application/json")
    @POST("api/auth0/v1/regis")
    fun registerUser(@Body requestBody: Map<String, String>): Call<ResponseDataRegister>

    //    @Multipart
//    @POST("api/permintaan-mgmnt/v1/usr-req-permintaan")
//    fun uploadFile(@Header("Authorization") token: String, @Part file: MultipartBody.Part): Call<ResponseData>
    @Multipart
    @POST("api/permintaan-mgmnt/v1/usr-req-permintaan")
    fun uploadFile(
        @Header("Authorization") token: String,
        @Part filePermintaan: MultipartBody.Part,
        @Part("namaBarang") namaBarang: RequestBody,
        @Part("deskripsiBarang") deskripsiBarang: RequestBody,
        @Part("jumlahPermintaan") jumlahPermintaan: RequestBody,
        @Part("tanggalPermintaan") tanggalPermintaan: RequestBody,
    ): Call<ResponseDataUp>

    @GET("api/permintaan-mgmnt/v1/usr-req-permintaan-get-all-data")
    fun HistoryUser( @Header("Authorization") token: String): Call<HistoryResponse>

}

