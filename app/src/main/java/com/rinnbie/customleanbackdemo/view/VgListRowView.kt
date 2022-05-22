package com.rinnbie.customleanbackdemo.view

import android.content.Context
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import android.widget.LinearLayout
import androidx.leanback.widget.VerticalGridView
import android.view.LayoutInflater
import com.rinnbie.customleanbackdemo.R

class VgListRowView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    val gridView: VerticalGridView

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_vg_list_row, this)
        gridView = findViewById(R.id.row_content)
        gridView.setHasFixedSize(false)
        orientation = VERTICAL
        descendantFocusability = FOCUS_AFTER_DESCENDANTS
    }
}