package com.uvk.shramapplication.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.lifecycleScope
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.api.ApiClient
import com.uvk.shramapplication.ui.lang.LanguageSelectionActivity
import com.uvk.shramapplication.ui.post.StoryDetailActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private var isDeepLinkHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        lifecycleScope.launch {
            val initialized = ApiClient.init()
        }

        Log.e("DeepLink", "Intent received: $intent")
        handleDeepLink(intent)

        val capLogo = findViewById<ImageView>(R.id.capLogo)
        val fullLogo = findViewById<ImageView>(R.id.fullLogo)

        capLogo.visibility = View.VISIBLE

        capLogo.postDelayed({
            //  fullLogoCap.visibility = View.GONE
            fullLogo.visibility = View.VISIBLE
            // Get screen center
            val screenWidth = capLogo.rootView.width
            val screenHeight = capLogo.rootView.height

            // Get view's current position
            val fromX = capLogo.x
            val fromY = capLogo.y

            val toX = (screenWidth - capLogo.width) / 2f
            val toY = (screenHeight - capLogo.height) / 2f

            // Animate position
            val moveX = ObjectAnimator.ofFloat(capLogo, "x", fromX, toX)
            val moveY = ObjectAnimator.ofFloat(capLogo, "y", fromY, toY)

            // Animate scale
            val scaleX = ObjectAnimator.ofFloat(capLogo, "scaleX", 0.5f, 1.5f)
            val scaleY = ObjectAnimator.ofFloat(capLogo, "scaleY", 0.5f, 1.5f)

            val animatorSet = AnimatorSet().apply {
                playTogether(moveX, moveY, scaleX, scaleY)
                duration = 3000
                interpolator = AccelerateDecelerateInterpolator()
            }

            animatorSet.start()
        }, 3000)


        // Optional: Move to next activity after all animations
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isDeepLinkHandled) {
                startActivity(Intent(this, LanguageSelectionActivity::class.java))
                finish()
            }
        }, 9000)


        /* val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.center_to_top)

         slideAnimation.setAnimationListener(object : Animation.AnimationListener {
             override fun onAnimationStart(animation: Animation?) {
                 logoText.visibility = View.GONE
                 logowithOutCap.visibility = View.VISIBLE
             }

             override fun onAnimationEnd(animation: Animation?) {}
             override fun onAnimationRepeat(animation: Animation?) {}
         })

         Handler(Looper.getMainLooper()).postDelayed({
             llsplash.startAnimation(slideAnimation)
         }, 1000)

         // Wait 3 seconds, then navigate if no deep link was handled
         Handler(Looper.getMainLooper()).postDelayed({
             if (!isDeepLinkHandled) {
                 startActivity(Intent(this, LanguageSelectionActivity::class.java))
                 finish()
             }
         }, 3000)*/
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.e("DeepLink", "New intent received: $intent")
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            Log.e("DeepLink", "URI: $uri")

            val storyId = uri.getQueryParameter("story_post_id")
            Log.e("DeepLink", "StoryId: $storyId")

            if (!storyId.isNullOrEmpty()) {
                isDeepLinkHandled = true
                val detailIntent = Intent(this, StoryDetailActivity::class.java).apply {
                    putExtra("story_post_id", storyId)
                }
                startActivity(detailIntent)
                finish()
            }
        }
    }
}

