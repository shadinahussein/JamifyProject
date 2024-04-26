package com.example.jamify

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jamify.com.example.jamify.APIInterface
import com.example.jamify.com.example.jamify.DataAdapter
import com.example.jamify.databinding.FragmentCreateBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Long

/**
 * A fragment representing a list of Items.
 */
class CreateFragment : Fragment() {
    companion object {
        fun newInstance() = CreateFragment()
    }
    private val viewModel: MainViewModel by viewModels()

    // XXX initialize viewModel
    private var _binding: FragmentCreateBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val PICK_IMAGE_REQUEST = 1

    lateinit var recyclerView: RecyclerView
    lateinit var myAdapter: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        recyclerView = binding.recyclerView
        myAdapter = DataAdapter(requireActivity(), viewModel, ::onClickListener)


        // populate adapter with list of songs
//        val retrofitBuilder = Retrofit.Builder()
//            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(APIInterface::class.java)
//
//        val retrofitData = retrofitBuilder.getData("eminem")
//
//        retrofitData.enqueue(object : Callback<MyData?> {
//            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
//                val responseBody = response.body()?.data!!
//                myAdapter = DataAdapter(activity!!, responseBody)
//                recyclerView.adapter = myAdapter
//                recyclerView.layoutManager = LinearLayoutManager(context)
////                val textView = binding.response
////                textView.text = responseBody.toString()
//                Log.d("TAG: onResponse", "onResponse: "  + responseBody.toString())
//
//            }
//
//            override fun onFailure(call: Call<MyData?>, t: Throwable) {
//                // if api call is failure then this method is executed
//                Log.e("MainActivity", "onFailure: " + t.message)
//            }
//        })
        binding.image.setOnClickListener {
            openFileChooser()
        }


        binding.search.setOnClickListener {
            val searchTerm = binding.searchText.text.toString()
            if (searchTerm.isNotEmpty()) {
                Log.e("MainActivity", searchTerm)
                searchSong(searchTerm)
            }
        }

        // on pressing create, will upload image to firebase storage and will create firebase doc

        binding.create.setOnClickListener {
            if (viewModel.songName.value != "") {
//                if (binding.image.drawable != null) {
//                    // upload image to firebase storage
//                    // create firebase doc
//                    viewModel.pictureSuccess()
//                }
                Log.d("CreateFragment", viewModel.getCurrentAuthUser().toString())
                viewModel.createNote(binding.captionText.text.toString())

            }
            // otherwise, show popup to prompt user to select a song
        }




    }


    fun searchSong(searchTerm: String) {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIInterface::class.java)

        val retrofitData = retrofitBuilder.getData(searchTerm)

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                val responseBody = response.body()?.data!!
                viewModel.setSearchedSongs(responseBody)
                myAdapter.submitList(viewModel.getCopyOfSongInfo())

                recyclerView.adapter = myAdapter
                recyclerView.layoutManager = LinearLayoutManager(context)
//                val textView = binding.response
//                textView.text = responseBody.toString()
                Log.d("TAG: onResponse", "onResponse: "  + responseBody.toString())

            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                // if api call is failure then this method is executed
                Log.e("MainActivity", "onFailure: " + t.message)
            }
        })
    }
    fun onClickListener(index: Int) : Unit {
        Log.d("CreateFragment", "onClickListener")
        viewModel.selectedIndex = index
        viewModel.songName.value = viewModel.getSong(index).title
        viewModel.setSongId(viewModel.getSong(index).id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data

            binding.image.setImageURI(imageUri)
            viewModel.setSelectedImage(imageUri!!)
        }
    }
}