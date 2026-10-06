/*
 * Vendored into CalendarOrtodox from PhotoView 2.3.0 (Apache License 2.0, see LICENSE-PhotoView.txt).
 * Local modifications are marked with "CalendarOrtodox:".
 */
package com.github.chrisbanes.photoview;

import android.graphics.RectF;

/**
 * Interface definition for a callback to be invoked when the internal Matrix has changed for
 * this View.
 */
public interface OnMatrixChangedListener {

    /**
     * Callback for when the Matrix displaying the Drawable has changed. This could be because
     * the View's bounds have changed, or the user has zoomed.
     *
     * @param rect - Rectangle displaying the Drawable's new bounds.
     */
    void onMatrixChanged(RectF rect);
}
