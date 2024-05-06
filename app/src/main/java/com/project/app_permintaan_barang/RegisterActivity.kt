package com.project.app_permintaan_barang


import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.project.app_permintaan_barang.model.ResponseData
import com.project.app_permintaan_barang.model.ResponseDataRegister
import com.project.app_permintaan_barang.network.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etDob: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var loginSekarang: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etUsername = findViewById(R.id.username)
        etName = findViewById(R.id.namaLengkap)
        etEmail = findViewById(R.id.email)
        etPhone = findViewById(R.id.noHp)
        etDob = findViewById(R.id.tanggalLahir)
        etAddress = findViewById(R.id.alamat)
        etPassword = findViewById(R.id.password)
        btnRegister = findViewById(R.id.btnRegister)
        loginSekarang = findViewById(R.id.login_sekarang)

        progressBar = findViewById(R.id.progressBar)

        etDob.setOnClickListener {
            // Mendapatkan tanggal sekarang untuk ditampilkan di DatePickerDialog
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Membuat instance DatePickerDialog
            val datePickerDialog = DatePickerDialog(this@RegisterActivity, { _, selectedYear, selectedMonth, selectedDay ->
                // Tetapkan tanggal yang dipilih ke EditText
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                // Format tanggal menjadi string
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selectedDateString = dateFormat.format(selectedDate.time)

                // Set tanggal yang dipilih ke EditText
                etDob.setText(selectedDateString)
            }, year, month, day)

            // Tampilkan DatePickerDialog
            datePickerDialog.show()
        }


        loginSekarang.setOnClickListener {
            // Start RegisterActivity
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val dob = etDob.text.toString()
            val address = etAddress.text.toString()
            val password = etPassword.text.toString()

            // Buat map requestBody untuk mengirim data registrasi
            val requestBody = mapOf(
                "username" to username,
                "namaLengkap" to name,
                "email" to email,
                "noHp" to phone,
                "tanggalLahir" to dob,
                "alamat" to address,
                "password" to password
            )
            registerUser(requestBody)
        }
    }


    private fun registerUser(requestBody: Map<String, String>) {
        progressBar.visibility = View.VISIBLE

        // Mendapatkan instance dari ApiService dari RetrofitClient
        val apiService = RetrofitClient.instance

        // Memanggil metode registerUser dengan requestBody
        val call = apiService.registerUser(requestBody)

        // Melakukan panggilan async ke endpoint
        call.enqueue(object : Callback<ResponseDataRegister> {
            override fun onResponse(call: Call<ResponseDataRegister>, response: Response<ResponseDataRegister>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    // Respon sukses
                    val registerResponse = response.body()

                    if (registerResponse != null) {
                        val token = registerResponse.data?.token
                        if (!token.isNullOrEmpty()) {
                            // Token berhasil diterima
                            Toast.makeText(this@RegisterActivity, "Register berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Token kosong atau null
                            Toast.makeText(this@RegisterActivity, "Token kosong atau null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Respon kosong
                        Toast.makeText(this@RegisterActivity, "Respon kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Terjadi kesalahan server
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val errorMessage = try {
                            val jsonObject = JSONObject(errorBody)
                            val subErrorsArray = jsonObject.optJSONArray("subErrors")
                            if (subErrorsArray != null) {
                                val subErrorsList = mutableListOf<String>()

                                for (i in 0 until subErrorsArray.length()) {
                                    val subErrorObject = subErrorsArray.getJSONObject(i)
                                    val field = subErrorObject.optString("field", null)
                                    val message = subErrorObject.optString("message", null)
                                    if (!field.isNullOrEmpty() && !message.isNullOrEmpty()) {
                                        subErrorsList.add("Field: $field, Message: $message")
                                    }
                                }
                                if (subErrorsList.isNotEmpty()) {
                                    subErrorsList.joinToString("\n")
                                } else {
                                    "Terjadi kesalahan pada server: No subErrors found"
                                }
                            } else {
                                "Terjadi kesalahan pada server: No subErrors found"
                            }
                        } catch (e: JSONException) {
                            // Log the exception for debugging
                            val logMessage = "Error parsing JSON object: $errorBody"
                            Log.e("JSONException", logMessage, e)
                            // Return the log message as the error message
                            logMessage
                        }
                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Empty or null error body
                        Toast.makeText(this@RegisterActivity, "Terjadi kesalahan pada server: Empty or null error body", Toast.LENGTH_SHORT).show()
                    }
                }



            }

            override fun onFailure(call: Call<ResponseDataRegister>, t: Throwable) {
                progressBar.visibility = View.GONE
                // Terjadi kesalahan jaringan
                Toast.makeText(this@RegisterActivity, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
            }
        })
    }
}