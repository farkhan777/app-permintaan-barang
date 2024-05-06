package com.project.app_permintaan_barang.ui.request

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ccom.project.app_permintaan_barang.ui.home.RequestViewModel
import com.project.app_permintaan_barang.R
import com.project.app_permintaan_barang.databinding.FragmentRequestBinding
import com.project.app_permintaan_barang.model.ResponseDataUp
import com.project.app_permintaan_barang.network.ApiService
import com.project.app_permintaan_barang.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RequestFragment : Fragment() {

    private var _binding: FragmentRequestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var uploadButton: Button
    private lateinit var submitButton: Button
    private lateinit var apiService: ApiService
    private lateinit var token: String
    private lateinit var selectedFile: File
    private lateinit var field1: RequestBody
    private lateinit var field2: RequestBody
    private lateinit var field3: RequestBody
    private lateinit var field4: RequestBody
    private lateinit var namaBarangEditText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var quantityText: EditText

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    try {
                        selectedFile = FileUtil.from(binding.root.context, uri)

                        // Get the file name using FileUtil
                        val fileName = FileUtil.getFileName(binding.root.context.contentResolver, uri)

                        // Update the TextView with the file name
                        binding.message.text = fileName

                    } catch (e: IOException) {
                        Toast.makeText(binding.root.context, "Error selecting file", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(binding.root.context, "File selection canceled", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(RequestViewModel::class.java)

        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = requireActivity().intent.getStringExtra("TOKEN") ?: ""

        // Initialize ApiService using RetrofitClient
        apiService = RetrofitClient.instance


        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

//        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            if (isLoading) {
//                progressBar.visibility = View.VISIBLE
//            } else {
//                progressBar.visibility = View.GONE
//            }
//        }

        namaBarangEditText = view.findViewById(R.id.titleText)
        descriptionText = view.findViewById(R.id.descriptionText)
        quantityText = view.findViewById(R.id.quantityText)

        // Get the current date
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

//        val nama = namaBarangEditText.text.toString()

        // Initialize fields
//        field1 = "value".toRequestBody("text/plain".toMediaType())
//        field1 = nama.toRequestBody("text/plain".toMediaType())
//        field2 = "value".toRequestBody("text/plain".toMediaType())
//        field3 = "1".toRequestBody("text/plain".toMediaType())
//        field4 ="2023-03-21".toRequestBody("text/plain".toMediaType())

        field4 = currentDate.toRequestBody("text/plain".toMediaType())

        uploadButton = view.findViewById(R.id.uploadButton)
        uploadButton.setOnClickListener {
            // Launch file picker intent
            pickFile()
        }

        submitButton = view.findViewById(R.id.fileButton)
        submitButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            val name = namaBarangEditText.text.toString().trim()
            val description = descriptionText.text.toString().trim()
            val quantity = quantityText.text.toString().trim()

            // Update the RequestBody objects with user input
            field1 = createPartFromString(name)
            field2 = createPartFromString(description)
            field3 = createPartFromString(quantity)

            // Check if a file has been selected
            if (::selectedFile.isInitialized) {
                progressBar.visibility = View.VISIBLE

                // Check if token is available
                if (token.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    // Upload the selected file
                    uploadFile(selectedFile, field1, field2, field3, field4)
                    progressBar.visibility = View.GONE

                } else {
                    progressBar.visibility = View.GONE

                    // Notify the user to login again
                    Toast.makeText(binding.root.context, "Please login again", Toast.LENGTH_SHORT).show()
                }
            } else {

                progressBar.visibility = View.GONE
                // Notify the user to select a file first
                Toast.makeText(binding.root.context, "Please select a file first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        pickFileLauncher.launch(Intent.createChooser(intent, "Select File"))
    }

    private fun uploadFile(
        file: File,
        field1: RequestBody,
        field2: RequestBody,
        field3: RequestBody,
        field4: RequestBody,
    ) {

        // Check if the file exists
        if (!file.exists()) {
            // Handle case where file does not exist
            Toast.makeText(binding.root.context, "File does not exist", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert file to MultipartBody.Part
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("filePermintaan", file.name, requestFile)
        Log.e("t",file.name)
        // Send file upload request with token included in headers
        apiService.uploadFile("Bearer $token", filePermintaan = body, namaBarang = field1, deskripsiBarang =  field2, jumlahPermintaan =  field3, tanggalPermintaan = field4)
            .enqueue(object : Callback<ResponseDataUp> {
                override fun onResponse(call: Call<ResponseDataUp>, response: Response<ResponseDataUp>) {
                    // Handle response
                    if (response.isSuccessful) {
                        // File uploaded successfully

                        namaBarangEditText.text.clear()
                        descriptionText.text.clear()
                        quantityText.text.clear()

                        // Reset selected file
                        selectedFile = File("")

                        // Reset message text to default
                        binding.message.text = "Upload File Berformat PDF"

                        Toast.makeText(binding.root.context, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // File upload failed
                        val errorBody = response.errorBody()?.string()
                        if (!errorBody.isNullOrEmpty()) {
                            val errorMessage = try {
                                val jsonObject = JSONObject(errorBody)
                                jsonObject.getString("error")
                            } catch (e: JSONException) {
                                "Server error occurred"
                            }
                            Toast.makeText(binding.root.context, errorMessage, Toast.LENGTH_SHORT).show()
                            Log.e("test",errorMessage)
                        } else {
                            Toast.makeText(binding.root.context, "Server error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDataUp>, t: Throwable) {
                    // Handle failure
                    Toast.makeText(binding.root.context, "File upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    t.message?.let { Log.e("test", it) }
                }
            })
    }

    private fun createPartFromString(string: String): RequestBody {
        return string.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    object FileUtil {
        @Throws(IOException::class)
        fun from(context: Context, uri: Uri): File {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Unable to open stream for $uri")
            val tempFile = File(context.cacheDir, context.contentResolver.getFileName(uri))
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            return tempFile
        }

        @JvmName("getFileName1")
        @Throws(IOException::class)
        fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
            var name = ""
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        name = it.getString(columnIndex)
                    }
                }
            }
            return name
        }
        private fun ContentResolver.getFileName(uri: Uri): String {
            var name = ""
            val cursor = query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex != -1) {
                        name = it.getString(columnIndex)
                    }
                }
            }
            return name
        }
    }
}