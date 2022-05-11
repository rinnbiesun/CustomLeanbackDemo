package androidx.leanback.app

import androidx.leanback.widget.*
import androidx.recyclerview.widget.RecyclerView

open class CustomRowsSupportFragment : RowsSupportFragment() {
    private var vgListRowRecycledViewPool: RecyclerView.RecycledViewPool? = null
    private var vgListRowPresenterMapper: ArrayList<Presenter>? = null


    override fun setupSharedViewPool(bridgeVh: ItemBridgeAdapter.ViewHolder) {
        super.setupSharedViewPool(bridgeVh)

        val rowPresenter = bridgeVh.presenter as RowPresenter
        val rowVh = rowPresenter.getRowViewHolder(bridgeVh.viewHolder)

        if (rowVh is VgListRowPresenter.ViewHolder) {
            val view: VerticalGridView = rowVh.gridView
            if (vgListRowRecycledViewPool == null) {
                vgListRowRecycledViewPool = view.recycledViewPool
            } else {
                view.setRecycledViewPool(vgListRowRecycledViewPool)
            }

            rowVh.itemBridgeAdapter.let { bridgeAdapter ->
                if (vgListRowPresenterMapper == null) {
                    vgListRowPresenterMapper = bridgeAdapter.presenterMapper
                } else {
                    bridgeAdapter.presenterMapper = vgListRowPresenterMapper
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vgListRowRecycledViewPool = null
    }
}