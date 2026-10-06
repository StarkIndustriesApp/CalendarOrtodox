package ro.calendarortodox.app

import android.app.Activity
import android.content.ComponentCallbacks2
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.time.YearMonth

class MainActivity : Activity() {

    private lateinit var loader: MonthImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val catalog = MonthCatalog(assets.list(MonthCatalog.ASSET_DIR).orEmpty().toList())
        loader = MonthImageLoader(assets, catalog)

        val pager = ViewPager2(this).apply {
            // A stable id lets ViewPager2 restore the viewed month after the app is recreated.
            id = R.id.month_pager
            // Swipe left = next month, regardless of the system language direction.
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            offscreenPageLimit = 1
            // No stretch or glow when swiping past January 2026 or December 2028.
            (getChildAt(0) as RecyclerView).overScrollMode = View.OVER_SCROLL_NEVER
        }
        pager.adapter = MonthPagerAdapter(catalog, loader) { index, zoomed ->
            if (index == pager.currentItem) pager.isUserInputEnabled = !zoomed
        }
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pager.isUserInputEnabled = true
                loader.prefetch(position - 1)
                loader.prefetch(position + 1)
            }
        })
        if (savedInstanceState == null) {
            pager.setCurrentItem(catalog.indexOf(YearMonth.now()), false)
        }

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.WHITE)
            addView(
                pager,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT),
            )
            // Keep the image clear of the camera cutout; the band around it stays white.
            setOnApplyWindowInsetsListener { view, insets ->
                val cutout = insets.getInsets(WindowInsets.Type.displayCutout())
                view.setPadding(cutout.left, cutout.top, cutout.right, cutout.bottom)
                WindowInsets.CONSUMED
            }
        }
        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) loader.clear()
    }

    override fun onDestroy() {
        loader.shutdown()
        super.onDestroy()
    }

    private fun hideSystemBars() {
        window.insetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsets.Type.systemBars())
        }
    }
}
