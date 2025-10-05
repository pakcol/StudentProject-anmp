package com.anmp.studentproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.anmp.studentproject.model.Student
import com.anmp.studentproject.util.FileHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListViewModel(app: Application): AndroidViewModel(app) {
    val studentsLD = MutableLiveData<ArrayList<Student>>()
    val LoadingLD = MutableLiveData<Boolean>()
    val errorLD = MutableLiveData<Boolean>()
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null

    fun refresh(){
        LoadingLD.value = true //progress bar start muncul
        errorLD.value = false //tidak ada error
        studentsLD.value = arrayListOf(
            Student("16055","Nonie","1998/03/28","5718444778","http://dummyimage.com/75x100"
                    + ".jpg/cc0000/ffffff"),
            Student("13312","Rich","1994/12/14","3925444073","http://dummyimage.com/75x100" +
                    ".jpg/5fa2dd/ffffff"),
            Student("11204","Dinny","1994/10/07","6827808747",
                "http://dummyimage.com/75x100.jpg/5fa2dd/ffffff1")
        )

        queue = Volley.newRequestQueue(getApplication())
        val url = "https://www.jsonkeeper.com/b/LLMW"
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            {
                //sukses
                val sType = object: TypeToken<List<Student>>() {}.type
                val result = Gson().fromJson<List<Student>>(it, sType)
                studentsLD.value = result as ArrayList<Student>
                LoadingLD.value = false

                //simpan juga ke file
                val filehelper = FileHelper(getApplication())
                val jsonstring = Gson().toJson(result)
                filehelper.writeToFile(jsonstring)
                Log.d("print_file", jsonstring)

                //baca json string dari file
                val hasil = filehelper.readFromFile()
                val listStudent = Gson().fromJson<List<Student>>(hasil, sType)
                Log.d("print_file", listStudent.toString())
            },
            {
                //failed
                errorLD.value = true
                LoadingLD.value = false
            }
        )

        stringRequest.tag = TAG
        queue?.add(stringRequest)

        LoadingLD.value = false
        errorLD.value = false
    }

    fun testSaveFile(){
        val filehelper = FileHelper(getApplication())
        filehelper.writeToFile("Hello World")
        val content = filehelper.readFromFile()
        Log.d("print_file", content)
        Log.d("print_file", filehelper.getFilePath())
    }

    override fun onCleared() {
        super.onCleared()
        queue?.cancelAll(TAG)
    }
}
