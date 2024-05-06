package com.project.app_permintaan_barang.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.app_permintaan_barang.model.HistoryResponse
import com.project.app_permintaan_barang.network.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel : ViewModel() {
    private val _upload = MutableLiveData<HistoryResponse>()
    val upload: LiveData<HistoryResponse> = _upload
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadImage(token: String){
        _isLoading.value = true
        RetrofitClient.instance.HistoryUser("Bearer $token").enqueue(object :
            Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    Log.e("suksesss", response.isSuccessful.toString())
                    val responseBody = response.body()
                    if (responseBody != null ) {
                        _upload.value = response.body()
                        Log.e("adaaa", response.body().toString())

                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val errorMessage = try {
                            val jsonObject = JSONObject(errorBody)
                            jsonObject.getString("error")
                        } catch (e: JSONException) {
                            "Server error occurred"
                        }
//                        Toast.makeText(binding.root.context, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("gagal", errorMessage + token)
                    }
                }
            }
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("gagal", t.toString())
            }
        })
    }
}