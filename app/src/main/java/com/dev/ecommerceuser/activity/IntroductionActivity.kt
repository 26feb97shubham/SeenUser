package com.dev.ecommerceuser.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.fragment.IntroFragment
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_introduction.*

class IntroductionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        setUpViews()
    }

    private fun setUpViews() {
        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsWelcomeShow, true)
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