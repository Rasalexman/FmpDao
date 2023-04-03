package com.rasalexman.fmpdaoexample.models

import android.os.Parcelable
import androidx.databinding.ObservableBoolean
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SymptomItemUI(
    val symptomCode: String,
    val symptGrpCode: String,
    val symptomText: String,
    var rbnr: String
) : Parcelable {

    @IgnoredOnParcel
    val isSelected: ObservableBoolean = ObservableBoolean(false)

    fun dropSelection() {
        isSelected.set(false)
    }

    fun revertSelection() {
        val lastSelectedFlag = isSelected.get()
        isSelected.set(!lastSelectedFlag)
    }
}
