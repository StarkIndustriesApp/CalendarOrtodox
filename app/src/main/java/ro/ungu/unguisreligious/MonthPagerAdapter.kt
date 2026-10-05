package ro.ungu.unguisreligious

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoView

/**
 * One zoomable [PhotoView] page per month. Reports whether a page is zoomed in so the
 * activity can lock month swiping while the user pans a zoomed image.
 */
class MonthPagerAdapter(
    private val catalog: MonthCatalog,
    private val loader: MonthImageLoader,
    private val onZoomChanged: (index: Int, zoomed: Boolean) -> Unit,
) : RecyclerView.Adapter<MonthPagerAdapter.PageHolder>() {

    class PageHolder(val photoView: PhotoView) : RecyclerView.ViewHolder(photoView) {
        var boundIndex = RecyclerView.NO_POSITION
    }

    override fun getItemCount(): Int = catalog.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val photoView = PhotoView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            setBackgroundColor(Color.WHITE)
            setScaleLevels(MIN_SCALE, DOUBLE_TAP_SCALE, MAX_SCALE)
        }
        val holder = PageHolder(photoView)
        photoView.setOnMatrixChangeListener {
            val index = holder.boundIndex
            if (index != RecyclerView.NO_POSITION) {
                val zoomed = photoView.scale > MIN_SCALE * ZOOMED_THRESHOLD
                // When zoomed, reaching the image edge must never hand the drag to the pager.
                photoView.setAllowParentInterceptOnEdge(!zoomed)
                onZoomChanged(index, zoomed)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        holder.boundIndex = position
        holder.photoView.setImageDrawable(null)
        loader.load(position) { bitmap ->
            if (holder.boundIndex == position) holder.photoView.setImageBitmap(bitmap)
        }
    }

    override fun onViewRecycled(holder: PageHolder) {
        holder.boundIndex = RecyclerView.NO_POSITION
        holder.photoView.setImageDrawable(null)
    }

    private companion object {
        const val MIN_SCALE = 1f
        const val DOUBLE_TAP_SCALE = 2.5f
        const val MAX_SCALE = 4f
        const val ZOOMED_THRESHOLD = 1.01f
    }
}
