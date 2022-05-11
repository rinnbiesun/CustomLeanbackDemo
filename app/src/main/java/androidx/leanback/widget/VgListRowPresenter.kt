package androidx.leanback.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.leanback.R
import androidx.leanback.system.Settings
import androidx.leanback.transition.TransitionHelper
import com.rinnbie.customleanbackdemo.MainFragment
import com.rinnbie.customleanbackdemo.VgListRow
import com.rinnbie.customleanbackdemo.VgListRowView

class VgListRowPresenter(
    private val focusZoomFactor: Int = FocusHighlight.ZOOM_FACTOR_MEDIUM
) : RowPresenter() {

    companion object {
        const val DEFAULT_RECYCLED_POOL_SIZE = 24
    }

    private var rowVerticalPadding = 0
    private var shadowOverlayHelper: ShadowOverlayHelper? = null
    private var shadowOverlayWrapper: ItemBridgeAdapter.Wrapper? = null
    private val recycledPoolSize = HashMap<Presenter, Int>()

    override fun initializeRowViewHolder(holder: RowPresenter.ViewHolder) {
        super.initializeRowViewHolder(holder)

        val rowViewHolder = holder as VgListRowPresenter.ViewHolder
        val context = holder.view.context
        if (shadowOverlayHelper == null) {
            shadowOverlayHelper = ShadowOverlayHelper.Builder()
                .needsOverlay(true)
                .needsShadow(true)
                .needsRoundedCorner(false)
                .preferZOrder(isUsingZOrder(context))
                .keepForegroundDrawable(true)
                .options(ShadowOverlayHelper.Options.DEFAULT)
                .build(context)
            if (shadowOverlayHelper?.needsWrapper() == true) {
                shadowOverlayWrapper = ItemBridgeAdapterShadowOverlayWrapper(shadowOverlayHelper)
            }
        }

        rowViewHolder.itemBridgeAdapter = ListRowPresenterItemBridgeAdapter(rowViewHolder)
        rowViewHolder.itemBridgeAdapter.wrapper = shadowOverlayWrapper
        shadowOverlayHelper?.prepareParentForShadow(rowViewHolder.gridView)

        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            rowViewHolder.itemBridgeAdapter,
            focusZoomFactor,
            false
        )

        rowViewHolder.gridView.isFocusDrawingOrderEnabled =
            (shadowOverlayHelper?.shadowType != ShadowOverlayHelper.SHADOW_DYNAMIC)

        rowViewHolder.gridView.setOnChildSelectedListener { parent, view, position, id ->
            selectChildView(rowViewHolder, view, true)
        }

        rowViewHolder.gridView.onUnhandledKeyListener =
            BaseGridView.OnUnhandledKeyListener { event ->
                rowViewHolder.onKeyListener != null && rowViewHolder.onKeyListener.onKey(
                    rowViewHolder.view, event.keyCode, event
                )
            }
    }

    override fun createRowViewHolder(parent: ViewGroup): ViewHolder {
        initStatics(parent.context)
        val rowView = VgListRowView(parent.context)
        return ViewHolder(rowView, rowView.gridView, this)
    }

    override fun dispatchItemSelectedListener(holder: RowPresenter.ViewHolder, selected: Boolean) {
        val vh = holder as VgListRowPresenter.ViewHolder
        val itemViewHolder =
            (vh.gridView.findViewHolderForAdapterPosition(vh.getSelectedPosition()) as? ItemBridgeAdapter.ViewHolder)
        if (itemViewHolder == null) {
            super.dispatchItemSelectedListener(holder, selected)
            return
        }

        if (selected) {
            if (holder.onItemViewSelectedListener != null) {
                holder.onItemViewSelectedListener.onItemSelected(
                    itemViewHolder.viewHolder,
                    itemViewHolder.item,
                    vh,
                    vh.row
                )
            }
        }
    }

    override fun onRowViewSelected(holder: RowPresenter.ViewHolder, selected: Boolean) {
        super.onRowViewSelected(holder, selected)
        val vh = holder as VgListRowPresenter.ViewHolder
        setVerticalPadding(vh)
    }

    override fun onRowViewExpanded(holder: RowPresenter.ViewHolder, expanded: Boolean) {
        super.onRowViewExpanded(holder, expanded)
        val vh = holder as VgListRowPresenter.ViewHolder
        vh.gridView.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
        setVerticalPadding(vh)
    }

    override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder, item: Any) {
        super.onBindRowViewHolder(holder, item)

        val vh = holder as VgListRowPresenter.ViewHolder
        val vhListRow = item as VgListRow

        with(vh.gridView) {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (2 * MainFragment.GRID_ITEM_HEIGHT) * vhListRow.numOfRows
            )
            setNumColumns(vhListRow.numOfColumns)
            horizontalSpacing = 10
            verticalSpacing = 10
            vh.itemBridgeAdapter.setAdapter(vhListRow.adapter)
            adapter = vh.itemBridgeAdapter
            contentDescription = vhListRow.contentDescription
        }
    }

    override fun onUnbindRowViewHolder(holder: RowPresenter.ViewHolder) {
        val vh = holder as VgListRowPresenter.ViewHolder
        vh.gridView.adapter = null
        vh.itemBridgeAdapter.clear()
        super.onUnbindRowViewHolder(vh)
    }

    fun setRecycledPoolSize(presenter: Presenter, size: Int) {
        recycledPoolSize[presenter] = size
    }

    fun getRecycledPoolSize(presenter: Presenter): Int {
        return if (recycledPoolSize.containsKey(presenter)) recycledPoolSize.get(presenter)
            ?: DEFAULT_RECYCLED_POOL_SIZE else DEFAULT_RECYCLED_POOL_SIZE
    }

    private fun selectChildView(
        rowViewHolder: VgListRowPresenter.ViewHolder,
        view: View?,
        fireEvent: Boolean
    ) {
        view?.run {
            if (rowViewHolder.isSelected) {
                val ibh =
                    rowViewHolder.gridView.getChildViewHolder(view) as ItemBridgeAdapter.ViewHolder
                if (fireEvent && rowViewHolder.onItemViewSelectedListener != null) {
                    rowViewHolder.onItemViewSelectedListener.onItemSelected(
                        ibh.viewHolder,
                        ibh.item,
                        rowViewHolder,
                        rowViewHolder.row
                    )
                }
            }
        } ?: run {
            if (fireEvent && rowViewHolder.onItemViewSelectedListener != null) {
                rowViewHolder.onItemViewSelectedListener.onItemSelected(
                    null,
                    null,
                    rowViewHolder,
                    rowViewHolder.row
                )
            }
        }
    }

    private fun isUsingZOrder(context: Context?): Boolean {
        return !Settings.getInstance(context).preferStaticShadows()
    }

    private fun initStatics(context: Context) {
        rowVerticalPadding =
            context.resources.getDimensionPixelSize(
                R.dimen.lb_browse_selected_row_top_padding
            )
    }

    private fun setVerticalPadding(vh: VgListRowPresenter.ViewHolder) {
        vh.gridView.setPadding(
            vh.paddingLeft,
            rowVerticalPadding,
            vh.paddingRight,
            rowVerticalPadding
        )
    }

    inner class ViewHolder(
        view: View,
        val gridView: VerticalGridView,
        val listRowPresenter: VgListRowPresenter
    ) : RowPresenter.ViewHolder(view) {

        var paddingTop: Int = 0
        var paddingBottom: Int = 0
        var paddingLeft: Int = 0
        var paddingRight: Int = 0
        lateinit var itemBridgeAdapter: ItemBridgeAdapter

        init {
            paddingTop = gridView.paddingTop
            paddingBottom = gridView.paddingBottom
            paddingLeft = gridView.paddingLeft
            paddingRight = gridView.paddingRight
        }

        fun getSelectedPosition(): Int {
            return gridView.selectedPosition
        }

        fun getItemViewHolder(position: Int): Presenter.ViewHolder? {
            val ibvh =
                gridView.findViewHolderForAdapterPosition(position) as ItemBridgeAdapter.ViewHolder
            return ibvh.viewHolder
        }

        override fun getSelectedItemViewHolder(): Presenter.ViewHolder? {
            return getItemViewHolder(getSelectedPosition())
        }

        override fun getSelectedItem(): Any? {
            val ibvh =
                gridView.findViewHolderForAdapterPosition(getSelectedPosition()) as ItemBridgeAdapter.ViewHolder
            return ibvh.item
        }
    }

    inner class SelectItemViewHolderTask : Presenter.ViewHolderTask() {
        var itemPosition: Int = 0
        var smoothScroll = true
        var mItemTask: Presenter.ViewHolderTask? = null

        override fun run(holder: Presenter.ViewHolder) {
            if (holder is VgListRowPresenter.ViewHolder) {
                val gridView = (holder as VgListRowPresenter.ViewHolder).gridView
                var task: androidx.leanback.widget.ViewHolderTask? = null
                mItemTask?.let { itemTask ->
                    task = ViewHolderTask { rvh ->
                        val ibvh = rvh as ItemBridgeAdapter.ViewHolder
                        itemTask.run(ibvh.viewHolder)
                    }
                }
                if (smoothScroll) {
                    gridView.setSelectedPositionSmooth(itemPosition, task)
                } else {
                    gridView.setSelectedPosition(itemPosition, task)
                }
            }
        }
    }

    inner class ListRowPresenterItemBridgeAdapter(
        private val rowViewHolder: VgListRowPresenter.ViewHolder
    ) : ItemBridgeAdapter() {

        override fun onCreate(viewHolder: ViewHolder) {
            if (viewHolder.itemView is ViewGroup) {
                TransitionHelper.setTransitionGroup(viewHolder.itemView as ViewGroup, true)
            }
        }

        override fun onBind(viewHolder: ViewHolder) {
            rowViewHolder.onItemViewClickedListener?.let {
                viewHolder.viewHolder.view.setOnClickListener {
                    val ibh =
                        rowViewHolder.gridView.getChildViewHolder(viewHolder.itemView) as ItemBridgeAdapter.ViewHolder
                    rowViewHolder.onItemViewClickedListener.onItemClicked(
                        viewHolder.viewHolder,
                        ibh.item,
                        rowViewHolder,
                        rowViewHolder.row as ListRow
                    )
                }
            }
        }

        override fun onUnbind(viewHolder: ViewHolder) {
            rowViewHolder.onItemViewClickedListener?.let {
                viewHolder.viewHolder.view.setOnClickListener(null)
            }
        }

        override fun onAttachedToWindow(viewHolder: ViewHolder) {
            applySelectLevelToChild(rowViewHolder, viewHolder.itemView)
            rowViewHolder.syncActivatedStatus(viewHolder.itemView)
        }

        override fun onAddPresenter(presenter: Presenter, type: Int) {
            rowViewHolder.gridView.recycledViewPool.setMaxRecycledViews(
                type,
                getRecycledPoolSize(presenter)
            )
        }
    }

    override fun onSelectLevelChanged(holder: RowPresenter.ViewHolder) {
        super.onSelectLevelChanged(holder)
        val vh = holder as VgListRowPresenter.ViewHolder
        for (i in 0 until vh.gridView.childCount) {
            applySelectLevelToChild(vh, vh.gridView.getChildAt(0))
        }
    }

    private fun applySelectLevelToChild(
        rowViewHolder: VgListRowPresenter.ViewHolder,
        childView: View
    ) {
        if (shadowOverlayHelper != null && shadowOverlayHelper?.needsOverlay() == true) {
            val dimmedColor = rowViewHolder.mColorDimmer.paint.color
            shadowOverlayHelper?.setOverlayColor(childView, dimmedColor)
        }
    }

    override fun freeze(holder: RowPresenter.ViewHolder, freeze: Boolean) {
        val vh = holder as VgListRowPresenter.ViewHolder
        vh.gridView.isScrollEnabled = !freeze
        vh.gridView.setAnimateChildLayout(!freeze)
    }

    override fun setEntranceTransitionState(
        holder: RowPresenter.ViewHolder,
        afterEntrance: Boolean
    ) {
        super.setEntranceTransitionState(holder, afterEntrance)
        (holder as VgListRowPresenter.ViewHolder).gridView.setChildrenVisibility(if (afterEntrance) View.VISIBLE else View.INVISIBLE)
    }
}