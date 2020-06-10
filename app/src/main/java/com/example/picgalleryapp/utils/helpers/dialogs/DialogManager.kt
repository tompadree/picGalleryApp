package com.example.picgalleryapp.utils.helpers.dialogs

import com.example.picgalleryapp.R


interface DialogManager {

    fun openOneButtonDialog(
        buttonTextId: Int = R.string.ok,
        textId: Int,
        cancelable: Boolean = false,
        onClickOk: (() -> Unit)? = null
    )

    fun openOneButtonDialog(
        buttonTextId: Int = R.string.ok,
        text: String,
        cancelable: Boolean = false,
        onClickOk: (() -> Unit)? = null
    )

    fun openOneButtonDialog(
        buttonTextId: Int = R.string.ok,
        titleId: Int,
        messageId: Int,
        cancelable: Boolean = false,
        onClickOk: (() -> Unit)? = null
    )

    fun openOneButtonDialog(
        buttonTextId: Int = R.string.ok,
        title: String,
        message: String,
        cancelable: Boolean = false,
        onClickOk: (() -> Unit)? = null
    )

    fun dismissAll()

    fun isDialogShown() : Boolean
}