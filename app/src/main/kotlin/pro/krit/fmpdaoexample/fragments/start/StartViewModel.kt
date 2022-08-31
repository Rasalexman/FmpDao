package pro.krit.fmpdaoexample.fragments.start

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.alertResult
import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresultpresentation.extensions.launchUITryCatch
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import pro.krit.fmpdaoexample.database.DataBaseHolder

class StartViewModel : BaseViewModel() {

    val showLoading: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)

    fun openDataBase(appContext: Context) = launchUITryCatch {
        showLoading.postValue(View.VISIBLE)
        val state = DataBaseHolder.createAndOpenDatabase(
            "872134",
            appContext
        )
        if (state.isOpened) {
            navigationLiveData.value = StartFragmentDirections.actionStartFragmentToMainFragment().toNavigateResult()
        } else {
            supportLiveData.value = alertResult("Database not opened")
        }

    }
}