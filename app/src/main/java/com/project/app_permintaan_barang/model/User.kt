package com.project.app_permintaan_barang.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String,
)


data class ResponseData(
    val data: Data?,
    val success: Boolean?,
    val message: String?,
    val status: Int?,
    val timestamp: Long?
)

data class ResponseDataRegister(
    val data: Data?,
    val serverResponse: String?,
    val status: Int?,
    val timestamp: Long?,
    val message: String?,
    val path: String?,
    val subErrors: List<SubErrors>?,
    val errorCode: String?
)

data class SubErrors(
    val field: String?,
    val message: String?,
    val rejectedValue: String?
)

data class ResponseDataUp(
//    val data: Data?,
    val success: Boolean?,
    val message: String?,
    val status: Int?,
    val timestamp: Long?
)

data class User(
    @SerializedName("idUser") val idUser: Int,
    @SerializedName("email") val email: String,
    @SerializedName("noHp") val noHp: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("namaLengkap") val namaLengkap: String,
    @SerializedName("tanggalLahir") val tanggalLahir: List<Int>,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("umur") val umur: Int?,
    @SerializedName("token") val token: String?,
    @SerializedName("createdBy") val createdBy: Int,
    @SerializedName("createdDate") val createdDate: Long,
    @SerializedName("modifiedBy") val modifiedBy: Int?,
    @SerializedName("modifiedDate") val modifiedDate: Long?,
//    @SerializedName("akses") val akses: Akses1,
    @SerializedName("registered") val registered: Boolean
)

//data class Akses1(
//    @SerializedName("idAkses") val idAkses: Int,
//    @SerializedName("namaAkses") val namaAkses: String,
//    @SerializedName("ltMenu") val ltMenu: List<String>,
//    @SerializedName("createdBy") val createdBy: Int,
//    @SerializedName("createdDate") val createdDate: Long,
//    @SerializedName("modifiedBy") val modifiedBy: Int?,
//    @SerializedName("modifiedDate") val modifiedDate: Long?
//)

data class DataItem(
    @SerializedName("idPermintaanBarang") val idPermintaanBarang: Int,
    @SerializedName("namaBarang") val namaBarang: String,
    @SerializedName("deskripsiBarang") val deskripsiBarang: String,
    @SerializedName("jumlahPermintaan") val jumlahPermintaan: Int,
    @SerializedName("statusPermintaan") val statusPermintaan: String,
    @SerializedName("catatanAdmin") val catatanAdmin: String,
    @SerializedName("tanggalPermintaan") val tanggalPermintaan: List<Int>,
    @SerializedName("filePermintaan") val filePermintaan: String,
    @SerializedName("imageId") val imageId: String,
    @SerializedName("user") val user: User,
    @SerializedName("createdBy") val createdBy: Int,
    @SerializedName("createdDate") val createdDate: Long,
    @SerializedName("modifiedBy") val modifiedBy: Int?,
    @SerializedName("modifiedDate") val modifiedDate: Long?
)

data class HistoryResponse(
    @SerializedName("data") val data: List<DataItem>,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Int,
    @SerializedName("timestamp") val timestamp: Long
)
data class Data(
    val token: String?,
    val akses: Akses?
)

data class Akses(
    val idAkses: Int?,
    val namaAkses: String?,
    val ltMenu: List<Any>?,
    val createdBy: Int?,
    val createdDate: Long?,
    val modifiedBy: Any?, // Change to appropriate type if needed
    val modifiedDate: Any? // Change to appropriate type if needed
)

