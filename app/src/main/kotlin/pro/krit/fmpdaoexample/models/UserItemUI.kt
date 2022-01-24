package pro.krit.fmpdaoexample.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserItemUI(
    val id: String,
    val marker: PmType,
    val userName: String,
    val tabNumber: String,
    val symptomCode: String,
    val rbnr: String
) : Parcelable {

    val description: String
        get() = if(symptomCode.isNotEmpty()) {
            "$tabNumber / $symptomCode"
        } else {
            tabNumber
        }
}
