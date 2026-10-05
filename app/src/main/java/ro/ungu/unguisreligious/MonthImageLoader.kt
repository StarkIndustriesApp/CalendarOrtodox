package ro.ungu.unguisreligious

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import java.util.concurrent.Executors

/**
 * Decodes month images off the main thread and keeps the most recent few in memory.
 * ImageDecoder's default allocator returns hardware bitmaps, so pixels live in GPU memory.
 * All public methods must be called on the main thread.
 */
class MonthImageLoader(
    private val assets: AssetManager,
    private val catalog: MonthCatalog,
) {
    private val cache = LruCache<Int, Bitmap>(CACHE_SIZE)
    private val pending = HashMap<Int, MutableList<(Bitmap) -> Unit>>()
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun load(index: Int, onLoaded: (Bitmap) -> Unit) {
        cache.get(index)?.let {
            onLoaded(it)
            return
        }
        pending[index]?.let {
            it += onLoaded
            return
        }
        pending[index] = mutableListOf(onLoaded)
        val path = "${MonthCatalog.ASSET_DIR}/${catalog.fileName(index)}"
        executor.execute {
            val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(assets, path))
            mainHandler.post {
                cache.put(index, bitmap)
                pending.remove(index)?.forEach { it(bitmap) }
            }
        }
    }

    fun prefetch(index: Int) {
        if (index in 0 until catalog.size) load(index) {}
    }

    fun clear() {
        cache.evictAll()
    }

    fun shutdown() {
        executor.shutdownNow()
        mainHandler.removeCallbacksAndMessages(null)
        pending.clear()
    }

    private companion object {
        // Current page plus two on each side; about 10 MB per decoded 1080x2400 image.
        const val CACHE_SIZE = 5
    }
}
