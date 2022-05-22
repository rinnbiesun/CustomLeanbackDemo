# CustomLeanbackDemo
A Demo project about custom Android TV leanback cases. I will offen update the cases. :smiley:

## VerticalGridView in ListRow (Custom ListRowPresenter)
<img src="https://user-images.githubusercontent.com/103634274/167927242-0f6f8593-fe7c-4d5e-97c0-aafbc995f6b2.png" width="500">

Custom your `VerticalGridView` in `onBindRowViewHolder()`. Need to set the width & height of `VerticalGridView` because the height will get 0 when `VerticalGridView` is `wrap_content`.
``` Kotlin
override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder, item: Any) {
        super.onBindRowViewHolder(holder, item)

        val vh = holder as VgListRowPresenter.ViewHolder
        val vhListRow = item as VgListRow

        with(vh.gridView) {
        // set the width & height of VerticalGridView
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
```
