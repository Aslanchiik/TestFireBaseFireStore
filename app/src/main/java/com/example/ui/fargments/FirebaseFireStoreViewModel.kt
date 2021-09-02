package com.example.ui.fargments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repositories.FireStoreRepos
import com.example.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FirebaseFireStoreViewModel @Inject constructor(private val repository: FireStoreRepos) :
    ViewModel() {

    fun setupData(user: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.saveDataInFireStore(user)
        }
    }

    fun deleteCollection() {
        return repository.delete()
    }

    val data: MutableLiveData<List<TaskModel>> = MutableLiveData()

    @ExperimentalCoroutinesApi
    fun getCoroutines() {
        viewModelScope.launch {
            repository.getShoppingListItemsFlow().collect { it ->
                val sortedList = it.sortedWith(compareBy { it.time })
                data.value = sortedList.toMutableList() as ArrayList<TaskModel>
                Log.e("tag", it.toString())
            }
        }
    }
}