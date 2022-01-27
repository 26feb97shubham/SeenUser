package com.seen.user.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.seen.user.R
import com.seen.user.fragment.IntroFragment
import com.seen.user.utils.SharedPreferenceUtility
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_introduction.*

class IntroductionActivity : AppCompatActivity() {
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        setUpViews()
    }

    private fun setUpViews() {
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager2.adapter = pagerAdapter

        /* mView.viewPager2.setPageTransformer(ZoomOutPageTransformer())*/

        TabLayoutMediator(tabLayout,   viewPager2){ tab, position ->

        }.attach()

        /*next.setOnClickListener {
            next.startAnimation(AlphaAnimation(1f, 0.5f))
            if (viewPager2.currentItem == 2) {
                startActivity(Intent(this, ChooseLangActivity::class.java))
                finishAffinity()
            } else {
                viewPager2.setCurrentItem(viewPager2.currentItem + 1, true)
            }
        }
        skip.setOnClickListener {
            skip.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, ChooseLangActivity::class.java))
            finishAffinity()

        }*/

    }

    override fun onBackPressed() {
        exitApp()
    }
    private fun exitApp() {
        val toast = Toast.makeText(
            this,
            getString(R.string.please_click_back_again_to_exist),
            Toast.LENGTH_SHORT
        )


        if(doubleClick){
            finishAffinity()
            doubleClick=false
        }
        else{

            doubleClick=true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                toast.show()
                doubleClick=false
            }, 500)
        }
    }

    private inner class ScreenSlidePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int{
            return 3
        }

        override fun createFragment(position: Int): Fragment {

            val fragment= IntroFragment(position)
            return fragment
        }
    }
}