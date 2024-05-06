package com.project.app_permintaan_barang

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.project.app_permintaan_barang.model.ResponseData
import com.project.app_permintaan_barang.network.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var daftarSekarang: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.username)
        etPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.bt_login)
        daftarSekarang = findViewById(R.id.daftar_sekarang)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            // Buat map requestBody untuk mengirim data login
            val requestBody = mapOf("username" to username, "password" to password)
            loginUser(requestBody)
        }

        daftarSekarang.setOnClickListener {
            // Start RegisterActivity
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    private fun loginUser(requestBody: Map<String, String>) {

        progressBar.visibility = View.VISIBLE

        // Mendapatkan instance dari ApiService dari RetrofitClient
        val apiService = RetrofitClient.instance

        // Memanggil metode login dengan requestBody
        val call = apiService.loginUser(requestBody)

        // Melakukan panggilan async ke endpoint
        call.enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    // Respon sukses
                    val loginResponse = response.body()

                    if (loginResponse != null) {
                        val token = loginResponse.data?.token
                        if (!token.isNullOrEmpty()) {
                            // Token berhasil diterima
                            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("TOKEN", token)
                            startActivity(intent)
                            finish() // Optional: Untuk menutup aktivitas login agar tidak bisa kembali ke halaman login dengan menekan tombol kembali
                        } else {
                            // Token kosong atau null
                            Toast.makeText(this@LoginActivity, "Token kosong atau null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Respon kosong
                        Toast.makeText(this@LoginActivity, "Respon kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Terjadi kesalahan server
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val errorMessage = try {
                            val jsonObject = JSONObject(errorBody)
                            jsonObject.getString("message")
                        } catch (e: JSONException) {
                            "Terjadi kesalahan pada server"
                        }
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show()
                    }
                }
            }


            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                progressBar.visibility = View.GONE
                // Terjadi kesalahan jaringan
                Toast.makeText(this@LoginActivity, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
