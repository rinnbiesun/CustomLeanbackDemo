package com.rinnbie.customleanbackdemo.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowHeaderPresenter
import com.rinnbie.customleanbackdemo.R

class CustomHeaderPresenter : RowHeaderPresenter() {
    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_custom_header, parent, false)
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val header: TextView = viewHolder.view.findViewById(R.id.header)
        header.text = (item as? ListRow)?.headerItem?.name.orEmpty()
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        super.onUnbindViewHolder(viewHolder)
    }
}