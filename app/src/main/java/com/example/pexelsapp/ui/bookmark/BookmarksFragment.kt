package com.example.pexelsapp.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.FragmentBookmarksBinding
import com.example.pexelsapp.ui.PhotosViewModel
import com.example.pexelsapp.ui.base.BaseViewBindingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarksFragment : BaseViewBindingFragment<FragmentBookmarksBinding>() {

    private val photosViewModel by activityViewModels<PhotosViewModel>()

    private val savedPhotosAdapter = SavedPhotosAdapter()

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentBookmarksBinding {
        return FragmentBookmarksBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        photosViewModel.getPhotosFromDB()
    }

    private fun initViews() {
        val photoItemClick: (Int) -> Unit = { id ->
            val action = BookmarksFragmentDirections.actionBookmarksFragmentToDetailsFragment(
                BOOKMARK_FRAGMENT,
                id
            )
            findNavController().navigate(action)
        }

        val photosRecycler = binding.photosRecycler
        savedPhotosAdapter.itemClick = photoItemClick
        photosRecycler.apply {
            adapter = savedPhotosAdapter
            layoutManager = StaggeredGridLayoutManager(SPAN_COUNT, LinearLayoutManager.VERTICAL)
        }
    }

    private fun initListeners() {
        binding.noResultsStubBt.setOnClickListener {
            val action = BookmarksFragmentDirections.actionBookmarksFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    photosViewModel.photosFlow.collect { photos ->
                        binding.stabGroupView.isVisible = photos.isEmpty()
                        savedPhotosAdapter.submitList(photos)
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
            }
        }
    }

    private fun changeProgressBarVisibility(isLoading: Boolean) {
        with(binding) {
            progressBar.isVisible = isLoading
            progressBar.progress = ZERO
        }
    }

    companion object {
        private const val BOOKMARK_FRAGMENT = "BOOKMARK"
        private const val ZERO = 0
        private const val SPAN_COUNT = 2
    }
}