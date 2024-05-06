package com.project.app_permintaan_barang.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.app_permintaan_barang.DataAdapter
import com.project.app_permintaan_barang.databinding.FragmentDashboardBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var token: String

    private lateinit var dataAdapter: DataAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dashboardViewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        token = requireActivity().intent.getStringExtra("TOKEN") ?: ""
        dashboardViewModel.uploadImage(token)

        // Inisialisasi RecyclerView dan set LayoutManager
        binding.recyclerViewData.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi ProgressBar
        val progressBar = binding.progressBar

        // Atur observer untuk isLoading LiveData
        dashboardViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


        // Inisialisasi adapter dengan data dan pasang ke RecyclerView
        dashboardViewModel.upload.observe(viewLifecycleOwner) { response ->
            if (response != null ) {
                dataAdapter = DataAdapter(response.data)
                binding.recyclerViewData.adapter = dataAdapter
            } else {
                // Handle error, such as showing a toast message
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
