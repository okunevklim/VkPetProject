package com.example.vk.interfaces

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vk.R
import kotlin.math.abs

class SwipeItemTouchHelper(private val swipeActionsListener: ItemSwipeActionsListener) :
    ItemTouchHelper.Callback() {
    private var swipeBackgroundColor: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))
    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(
            PorterDuff.Mode.CLEAR
        )
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val position = viewHolder.adapterPosition
        val profileViewType = 12
        val profileSwipeFlags = createSwipeFlags(position)
        val defaultSwipeFlags = ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        val dragFlags = 0
        return if (viewHolder.itemViewType == profileViewType) makeMovementFlags(
            dragFlags,
            profileSwipeFlags
        ) else makeMovementFlags(dragFlags, defaultSwipeFlags)

    }

    /**Making ProfileInfo not available for swipes*/
    private fun createSwipeFlags(position: Int): Int {
        return if (position != 0) {
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        } else {
            0
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.RIGHT -> swipeActionsListener.onSwipedLike(viewHolder.adapterPosition)
            ItemTouchHelper.LEFT -> swipeActionsListener.onSwipeHide(viewHolder.adapterPosition)
        }
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val deleteIcon: Drawable =
            ContextCompat.getDrawable(viewHolder.itemView.context, R.drawable.ic_delete)!!
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive
        val intrinsicHeight = deleteIcon.intrinsicHeight

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - intrinsicHeight
        val deleteIconRight = itemView.right
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX > 0F) {
                // свайп вправо!
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX / 2, dY,
                    actionState, isCurrentlyActive
                )
            } else {
                // а это свайп влево
                /** Плавное исчезание айтема во время свайпа */
                val width = itemView.width.toFloat()
                val alpha = 1.0f - abs(dX) / width
                itemView.alpha = alpha
                itemView.translationX = dX
                /***/
                swipeBackgroundColor.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                swipeBackgroundColor.draw(c)
                deleteIcon.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteIcon.draw(c)
            }
            c.save()
            if (dX > 0) {
                // ничего
            } else {
                c.clipRect(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
            }
            c.restore()
        }
    }

    /** Метод возвращает расстояние в процентах типа Float, после преодоления которого свайп автоматически произойдет */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.5f
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}