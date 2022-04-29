package com.seen.user.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.utils.SharedPreferenceUtility
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_player_view.view.*

class PlayerViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var thumbnail:String=""
    var files_path:String=""
    var extension:String=""
    var files:String=""
    lateinit var mView: View
    var simpleExoPlayer: SimpleExoPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            thumbnail = it.getString("thumbnail", "")
            files_path = it.getString("files_path", "")
            extension = it.getString("extension", "")
            files = it.getString("files", "")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_player_view, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        return mView
    }
    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }
        if(files_path != thumbnail){
            mView.ep_video_view.visibility=View.VISIBLE
            mView.image.visibility=View.GONE
            initPlayer()
        }
        else{
            mView.ep_video_view.visibility=View.GONE
            mView.image.visibility=View.VISIBLE
            Glide.with(requireContext()).load(thumbnail).placeholder(R.drawable.default_icon).into(mView.image)
        }

    }
    private fun initPlayer() {
        simpleExoPlayer= SimpleExoPlayer.Builder(requireContext()).build()
        mView.ep_video_view.player=simpleExoPlayer

        val uri= Uri.parse(files_path)
        val mediaItem= MediaItem.fromUri(uri)
        simpleExoPlayer!!.setMediaItem(mediaItem)

        playVideo()
    }

    private fun playVideo() {
        simpleExoPlayer!!.prepare()
        simpleExoPlayer!!.play()
    }



    override  fun onDestroy() {
        super.onDestroy()
        stopMedia()
    }

    override  fun onPause() {
        super.onPause()
        if(simpleExoPlayer != null){
            if(simpleExoPlayer!!.isPlaying){
                simpleExoPlayer!!.pause()
            }
        }

    }

    private fun stopMedia() {
        if(simpleExoPlayer != null){
            simpleExoPlayer!!.stop()
            simpleExoPlayer!!.release()
        }

    }
}