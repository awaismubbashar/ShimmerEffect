package com.example.shimmereffect.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shimmereffect.adapter.QuotesAdapter
import com.example.shimmereffect.database.QuoteEntity
import com.example.shimmereffect.databinding.ActivityMainBinding
import com.example.shimmereffect.mapper.quotesToQuoteEntity
import com.example.shimmereffect.network.Resource
import com.example.shimmereffect.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit var binding: ActivityMainBinding
    private lateinit var adapter: QuotesAdapter
    private var quotesList: MutableList<QuoteEntity> = mutableListOf()
    var listSize:Int = 0
    private val viewModel: MainViewModel by viewModels()
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.shimmerFrameLayout.startShimmerAnimation()

        // set adapter
        adapter = QuotesAdapter(this)
        binding.recyclerView.adapter = adapter

        // call api
        viewModel.getQuotesFromServer()
//        viewModel.getQuotesFromDB()

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)  && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    listSize.let {
                        offset += 10
                        if (offset <= listSize) {
//                            offset += 25
                            viewModel.getQuotesFromDB(offset)
                        }
                    }
                }
            }
        })

        observeQuotesResponse()
        observeRoomQuoteResponse()

    }

    private fun observeQuotesResponse() {
        viewModel.quoteResponse.observe(this) {
            when(it) {
                is Resource.Success -> {
                    hideShimmer()

                    val quotes = it.value.quotesList?.quotesToQuoteEntity()
                    listSize = it.value.quotesList?.size ?: 0

                    viewModel.addQuotesInDB(quotes)
                    viewModel.getQuotesFromDB(offset)

                }
                is Resource.Failure -> {
                    hideShimmer()
                }
                Resource.Loading -> {
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                    binding.shimmerFrameLayout.startShimmerAnimation()
                }
            }
        }
    }

    private fun observeRoomQuoteResponse() {
        viewModel.localQuoteResponse.observe(this) {
            when(it) {
                is Resource.Success -> {

                    binding.recyclerView.visibility = View.VISIBLE

                   /* for (i in it.value) {
                        quotesList.add(QuoteEntity(id = i.id,quote = i.quote, author = i.author))
                        Log.d(TAG, "value :  ${i.id}")
                    }*/


                    var responseSize = it.value.size
                    var index = 0
                    var currentListSize = quotesList.size
                    while (index != responseSize) {
                        quotesList.add(currentListSize, it.value[index] )
                        index++;currentListSize++

                    }

                    adapter.submitList(quotesList.toList())
//                    adapter.notifyDataSetChanged()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.progress.visibility = View.GONE
                    },1000)


                }
                is Resource.Failure -> {
                    binding.progress.visibility = View.GONE
                }
                Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideShimmer() {
        binding.shimmerFrameLayout.stopShimmerAnimation()
        binding.shimmerFrameLayout.visibility = View.GONE
    }
}