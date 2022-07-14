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

### Limitation
There is a wrong scrolling behavior if the row number of `VerticalGridView` is larger than the screen size. To fix the problem, we can separate items into multiple `VerticalGridView` in multiple `ListRow`. Implementation will be provided in the future.

Example
| Item Count  | Items per VerticalGridView row | Max Items per VerticalGridView | Final ListRow count
| --- | --- | --- | --- |
| 16  | 4 | 8 | 2 |

## Custom Row HeaderView
<img src="https://user-images.githubusercontent.com/103634274/169695385-e7f7826f-5878-46a4-aa8f-f1fa731f1539.png" width="500">

Implement a custom `ListRowPresenter` & `RowHeaderPresenter`
``` Kotlin
class CustomListRowPresenter : ListRowPresenter() {
    private var customHeaderPresenter: CustomHeaderPresenter = CustomHeaderPresenter()

    init {
        headerPresenter = customHeaderPresenter      // set a new RowHeaderPresenter
        shadowEnabled = false
        selectEffectEnabled = false
    }
}
```


``` Kotlin
class CustomHeaderPresenter : RowHeaderPresenter() {
    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        // Create a view by xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_custom_header, parent, false)
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        // Get ListRow object to update a view 
        val header: TextView = viewHolder.view.findViewById(R.id.header)
        header.text = (item as? ListRow)?.headerItem?.name.orEmpty()
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        super.onUnbindViewHolder(viewHolder)
    }
}
```
