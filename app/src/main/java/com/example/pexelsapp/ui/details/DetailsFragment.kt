package com.example.pexelsapp.ui.details

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.FragmentDetailsBinding
import com.example.pexelsapp.domain.models.PhotoModel
import com.example.pexelsapp.ui.PhotosViewModel
import com.example.pexelsapp.ui.base.BaseViewBindingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

@AndroidEntryPoint
class DetailsFragment : BaseViewBindingFragment<FragmentDetailsBinding>() {

    private val photosViewModel by activityViewModels<PhotosViewModel>()
    private val args: DetailsFragmentArgs by navArgs()
    private var currentPhoto: PhotoModel? = null

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentDetailsBinding {
        return FragmentDetailsBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.visibility = View.GONE
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
        if (args.fragment == HOME_FRAGMENT) photosViewModel.getPhoto(args.id)
        else photosViewModel.getPhotoFromDB(args.id)
        photosViewModel.checkPhotoInDB(args.id)
    }

    private fun initListeners() {
        binding.backIcon.setOnClickListener {
            navigateBack()
        }

        binding.bookmarkBt.setOnClickListener {
            with(binding) {
                bookmarkBt.isVisible = false
                bookmarkBtActive.isVisible = true
            }
            currentPhoto?.let { photo -> photosViewModel.insertPhotosInDB(photo) }
        }

        binding.bookmarkBtActive.setOnClickListener {
            with(binding) {
                bookmarkBtActive.isVisible = false
                bookmarkBt.isVisible = true
            }
            currentPhoto?.id?.let { id -> photosViewModel.deletePhotoFromDB(id) }
        }

        binding.downloadBt.setOnClickListener {
            downloadButtonCLick()
        }

        binding.noImageStubBt.setOnClickListener {
            navigateBack()
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    photosViewModel.photoFlow.collect { photo ->
                        photoDataCollect(photo)
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
                    photosViewModel.photoInDBFlow.collect { isInDB ->
                        with(binding) {
                            bookmarkBt.isVisible = !isInDB
                            bookmarkBtActive.isVisible = isInDB
                        }
                    }
                }
                launch {
                    photosViewModel.networkErrorFlow.collect { isError ->
                        with(binding) {
                            stabGroupView.isVisible = isError
                            resultsGroupView.isVisible = !isError
                        }
                    }
                }
            }
        }
    }

    private fun photoDataCollect(photo: PhotoModel?) {
        currentPhoto = photo
        with(binding) {
            if (photo == null) {
                stabGroupView.isVisible = true
                resultsGroupView.isVisible = false
            } else {
                stabGroupView.isVisible = false
                resultsGroupView.isVisible = true
                authorsInitials.text = photo.photographer

                loadImage(photo.url)
            }
        }
    }

    private fun changeProgressBarVisibility(isLoading: Boolean) {
        with(binding) {
            progressBar.isVisible = isLoading
            progressBar.progress = ZERO
        }
    }

    private fun navigateBack() {
        if (args.fragment == HOME_FRAGMENT) {
            val action = DetailsFragmentDirections.actionDetailsFragmentToHomeFragment()
            findNavController().navigate(action)
        } else {
            val action = DetailsFragmentDirections.actionDetailsFragmentToBookmarksFragment()
            findNavController().navigate(action)
        }
    }

    private fun loadImage(url: String) {
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(true)
            .timeout(CACHE_TIMEOUT)
        Glide
            .with(this@DetailsFragment)
            .load(url)
            .apply(requestOptions)
            .into(binding.photoItem)
    }

    private fun downloadButtonCLick() {
        var mImage: Bitmap?
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        myExecutor.execute {
            mImage = currentPhoto?.url?.let { url -> mLoad(url) }
            myHandler.post {
                if (mImage != null) {
                    mSaveMediaToStorage(mImage)
                }
            }
        }
    }

    private fun mLoad(string: String): Bitmap? {
        val url = mStringToURL(string)
        if (url != null) {
            val connection: HttpURLConnection?
            try {
                connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream = connection.inputStream
                val bufferedInputStream = BufferedInputStream(inputStream)
                return BitmapFactory.decodeStream(bufferedInputStream)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this.context, getString(R.string.error), Toast.LENGTH_LONG).show()
            }
        }
        return null
    }

    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = System.currentTimeMillis().toString() + getString(R.string.jpg)
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.context?.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, getString(R.string.path))
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, it)
            Toast.makeText(this.context, getString(R.string.saved_to_gallery), Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private const val HOME_FRAGMENT = "HOME"
        private const val ZERO = 0
        private const val CACHE_TIMEOUT = 3600 * 1000
        private const val BITMAP_QUALITY = 100
    }
}