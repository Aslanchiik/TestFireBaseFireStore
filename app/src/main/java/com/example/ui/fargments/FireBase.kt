package com.example.ui.fargments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testfirebasefirestore.databinding.FragmentFireBaseBinding
import com.example.ui.adapters.TaskAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap



@InternalCoroutinesApi
@AndroidEntryPoint
class FireBase : Fragment() {

    private lateinit var binding: FragmentFireBaseBinding
    private val viewModel: FirebaseFireStoreViewModel by viewModels()
    private val taskAdapter: TaskAdapter = TaskAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFireBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.getCoroutines()
        updateFromDataServer()
        setupDataToFirestore()
    }

    private fun setupRecyclerView() {
        binding.recView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    private fun verifyAvailableNetwork(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    private fun updateFromDataServer() {
            if (verifyAvailableNetwork()) {
                viewModel.data.observe(viewLifecycleOwner, {
                    taskAdapter.submitList(it)
                })
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupDataToFirestore() {
        binding.btnGo.setOnClickListener {
            if (verifyAvailableNetwork()) {
                val number = Date().time
                val date = SimpleDateFormat("HH:mm:ss")
                val time = date.format(Date())

                val user = HashMap<String, Any>()
                user["name"] = binding.editText.text.toString()
                user["number"] = number
                user["time"] = time
                binding.editText.setText("")
                viewModel.setupData(user)
            }
        }
        binding.deleteButton.setOnClickListener {
            taskAdapter.submitList(null)
            viewModel.deleteCollection()
        }
    }


    private fun getWifi() {

        val date = SimpleDateFormat("HH:mm:ss")
        Log.e("tag", date.format(Date()))

        val manager =
            requireContext().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val dhcp = manager.dhcpInfo

        var apiAddress = dhcp.gateway
        apiAddress =
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) Integer.reverseBytes(
                apiAddress
            )
            else apiAddress

        val apiAddressByte: ByteArray = BigInteger.valueOf(apiAddress.toLong()).toByteArray()
        try {
            val myAddress: InetAddress = InetAddress.getByAddress(apiAddressByte)
            binding.ipAddress.text = myAddress.hostAddress
            Log.e("tag", myAddress.hostAddress)
        } catch (e: UnknownHostException) {
            Log.e("Wifi class", "Error getting IP address")
        }
    }







}
