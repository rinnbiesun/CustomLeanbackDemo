package com.rinnbie.customleanbackdemo.presenter

import androidx.leanback.widget.ListRowPresenter

class CustomListRowPresenter : ListRowPresenter() {
    private var customHeaderPresenter: CustomHeaderPresenter = CustomHeaderPresenter()

    init {
        headerPresenter = customHeaderPresenter
        shadowEnabled = false
        selectEffectEnabled = false
    }
}