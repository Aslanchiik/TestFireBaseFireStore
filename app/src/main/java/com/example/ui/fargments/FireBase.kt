package com.example.ui.fargments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.model.TaskModel
import com.example.testfirebasefirestore.databinding.FragmentFireBaseBinding
import com.example.ui.adapters.TaskAdapter
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FireBase : Fragment() {


    private lateinit var binding: FragmentFireBaseBinding
    private lateinit var fireStoreSave: FirebaseFirestore
    private val db = Firebase.firestore
    private val taskAdapter: TaskAdapter = TaskAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFireBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getDataFromServer()
        setupDataToFireBase()
    }

    private fun setupRecyclerView() {
        binding.recView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupDataToFireBase() {
        fireStoreSave = FirebaseFirestore.getInstance()

        binding.btnGo.setOnClickListener {
            val date = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val time = date.format(Date())
            Toast.makeText(context, "Tag", Toast.LENGTH_SHORT).show()
            val user = hashMapOf(
                "first" to binding.editText.text.toString(),
                "second" to time
            )

            db.collection("users").add(user)
                .addOnCompleteListener { document ->
                    if (document.isSuccessful) {
                        Log.e("tag", "Is Good")
                    } else {
                        Log.e("tag", "Not bad$document")
                    }
                }
            getDataFromServer()
        }
    }

    private fun getDataFromServer() {
        db.collection("users")
            .orderBy("second", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val list: ArrayList<TaskModel> = ArrayList()

                for (document in result) {

                    val model: String = document.getString("first").toString()
                    val second: String = document.getString("second").toString()

                    val models = TaskModel(model, second)

                    Log.d("TAG"
                        ,"${document.id} => ${document.data}")

                    list.add(models)
                }
                taskAdapter.addAllList(list)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }


    }
}