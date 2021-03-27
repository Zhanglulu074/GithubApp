package com.example.githubapplication.network.model

import android.content.Context
import androidx.annotation.IntDef
import androidx.databinding.ObservableField

class LoadingStatus() {

    public var isLoadingIconVisible: ObservableField<Boolean> = ObservableField(false)
    var hintString: ObservableField<String> = ObservableField("")

    companion object {
        const val STATUS_SUCCESS_FULL = 0
        const val STATUS_SUCCESS_EMPTY = 1
        const val STATUS_FAILED = 2
        const val STATUS_INITIAL = 3
        const val STATUS_LOADING = 4
    }
    @IntDef(STATUS_SUCCESS_FULL, STATUS_SUCCESS_EMPTY, STATUS_FAILED, STATUS_INITIAL, STATUS_LOADING)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class LOADING_STATUS{}

    public fun setLoadingStatus(@LOADING_STATUS status: Int, errorString: String = "") {
        when(status) {
            STATUS_INITIAL -> {
                isLoadingIconVisible.set(false)
                hintString.set("")
            }
            STATUS_SUCCESS_FULL -> {
                isLoadingIconVisible.set(false)
                hintString.set("到底了")
            }
            STATUS_SUCCESS_EMPTY -> {
                isLoadingIconVisible.set(false)
                hintString.set("未发现符合条件的条目")
            }
            STATUS_FAILED -> {
                isLoadingIconVisible.set(false)
                hintString.set("加载失败，错误信息为{${errorString}}")
            }
            STATUS_LOADING -> {
                isLoadingIconVisible.set(true)
                hintString.set("正在加载")
            }
        }
    }

}