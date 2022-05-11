package com.rinnbie.customleanbackdemo

import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ObjectAdapter

class VgListRow(
    headerItem: HeaderItem,
    val numOfRows: Int,
    val numOfColumns: Int,
    adapter: ObjectAdapter
) : ListRow(headerItem, adapter)