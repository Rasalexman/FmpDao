package pro.krit.fmpdaoexample.fragments.start

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rasalexman.sresult.common.extensions.toastResult
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.vmadalin.easypermissions.models.PermissionRequest
import pro.krit.fmpdaoexample.R
import pro.krit.fmpdaoexample.databinding.FragmentStartBinding

class StartFragment : BaseBindingFragment<FragmentStartBinding, StartViewModel>(), EasyPermissions.PermissionCallbacks {
    override val layoutId: Int
        get() = R.layout.fragment_start

    override val viewModel: StartViewModel by viewModels()

    @Suppress("deprecation")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        methodRequiresTwoPermission()
    }

    private fun methodRequiresTwoPermission() {
        if (EasyPermissions.hasPermissions(this.activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )) {
            // Already have permission, do the thing
            initLogsAndFetch()
        } else {
            val builder = PermissionRequest.Builder(this.requireContext())
                .code(REQUEST_CODE)
                .perms(arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ))
                .rationale(R.string.title_settings_dialog)
                .build()
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, builder)
        }
    }

    private fun initLogsAndFetch() {
        viewModel.openDataBase(this.requireContext().applicationContext)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this.requireContext()).build().show()
        } else {
            onResultHandler(toastResult(R.string.title_settings_dialog))
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        initLogsAndFetch()
    }

    companion object {
        private const val REQUEST_CODE = 11222
    }
}