package com.example.pexelsapp.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.FragmentHomeBinding
import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.domain.models.RequestModel
import com.example.pexelsapp.ui.PhotosViewModel
import com.example.pexelsapp.ui.base.BaseViewBindingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseViewBindingFragment<FragmentHomeBinding>() {

    private val photosViewModel by activityViewModels<PhotosViewModel>()

    private val requestsAdapter = RequestsAdapter()
    private val photosAdapter = PhotosAdapter()
    private var requests = listOf<String>()

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.visibility = View.VISIBLE
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        initObservers()
        photosViewModel.getPopularRequests()
        loadPhotos()
    }

    private fun initViews() {
        val requestItemClick: (String, Int) -> Unit = { title, position ->
            requestsAdapter.setSelectedPosition(position)
            photosViewModel.setCurrentQuery(title)
            binding.searchBar.setText(title)
        }
        val photoItemClick: (Int) -> Unit = { id ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToDetailsFragment(HOME_FRAGMENT, id)
            findNavController().navigate(action)
        }

        val popularRequestRecycler = binding.popularRequestRecycler
        requestsAdapter.itemClick = requestItemClick
        popularRequestRecycler.apply {
            adapter = requestsAdapter
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        }

        val photosRecycler = binding.photosRecycler
        photosAdapter.itemClick = photoItemClick
        photosRecycler.apply {
            adapter = photosAdapter
            layoutManager = StaggeredGridLayoutManager(SPAN_COUNT, LinearLayoutManager.VERTICAL)
        }
    }

    private fun initListeners() {
        binding.searchBar.onSearchListener = { query ->
            photosViewModel.setCurrentQuery(query)
            selectRequestPosition(query)
            photosViewModel.getPhotos(query)
        }

        binding.searchBar.onClearListener = {
            requestsAdapter.setSelectedPosition(NO_POSITION)
            photosViewModel.setCurrentQuery(POPULAR_PHOTOS)
        }

        binding.noResultsStubBt.setOnClickListener {
            binding.searchBar.clearText()
            requestsAdapter.setSelectedPosition(NO_POSITION)
            photosViewModel.setCurrentQuery(POPULAR_PHOTOS)
            photosViewModel.getPopularPhotos()
        }

        binding.noInternetStub.setOnClickListener {
            if (photosViewModel.getCurrentQuery() == POPULAR_PHOTOS) photosViewModel.getPopularPhotos()
            else photosViewModel.getPhotos(photosViewModel.getCurrentQuery())
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    photosViewModel.requestsFlow.collect { requestsData ->
                        requestDataCollect(requestsData)
                    }
                }
                launch {
                    photosViewModel.photosFlow.collect { photos ->
                        photosDataCollect(photos)
                    }
                }
                launch {
                    photosViewModel.photosLoadingFlow.collect { isLoading ->
                        changeProgressBarVisibility(isLoading)
                    }
                }
                launch {
                    photosViewModel.photosLoadingProgressFlow.collect { progress ->
                        binding.progressBar.progress = progress
                    }
                }
                launch {
                    photosViewModel.networkErrorFlow.collect { isError ->
                        showNetworkErrorStub(isError)
                    }
                }
            }
        }
    }

    private fun requestDataCollect(requestsData: List<RequestModel>) {
        requests = requestsData.map { requestModel -> requestModel.title }
        if (requestsData.isNotEmpty()) requestsAdapter.submitList(requestsData)
        selectRequestPosition(photosViewModel.getCurrentQuery())
    }

    private fun photosDataCollect(photos: List<PhotoModel>) {
        with(binding) {
            if (photos.isNotEmpty()) {
                if (!getNetworkStatus()) {
                    Toast.makeText(
                        this@HomeFragment.context,
                        getText(R.string.bad_internet_connection),
                        Toast.LENGTH_LONG
                    ).show()
                }
                stabGroupView.isVisible = false
            } else {
                stabGroupView.isVisible = true
            }
        }
        photosAdapter.submitList(photos)
    }

    private fun changeProgressBarVisibility(isLoading: Boolean) {
        with(binding) {
            progressBar.isVisible = isLoading
            progressBar.progress = ZERO
        }
    }

    private fun showNetworkErrorStub(isError: Boolean) {
        with(binding) {
            if (isError) {
                photosAdapter.submitList(listOf())
                noInternetStub.isVisible = true
                Toast.makeText(
                    this@HomeFragment.context,
                    getText(R.string.bad_internet_connection),
                    Toast.LENGTH_LONG
                ).show()
            } else noInternetStub.isVisible = false
        }
    }

    private fun loadPhotos() {
        if (photosViewModel.getCurrentQuery() == POPULAR_PHOTOS) photosViewModel.getPopularPhotos()
        else binding.searchBar.setText(photosViewModel.getCurrentQuery())
    }

    private fun getNetworkStatus(): Boolean {
        val connectivityManager =
            context?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }

    private fun selectRequestPosition(query: String) {
        val selectedPosition = requests.indexOfFirst { request ->
            request == query
        }
        if (selectedPosition != NO_POSITION) requestsAdapter.setSelectedPosition(selectedPosition)
    }

    companion object {
        private const val POPULAR_PHOTOS = "popular_photos"
        private const val HOME_FRAGMENT = "HOME"
        private const val NO_POSITION = -1
        private const val SPAN_COUNT = 2
        private const val ZERO = 0
    }
}