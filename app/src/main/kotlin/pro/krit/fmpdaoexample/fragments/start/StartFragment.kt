package pro.krit.fmpdaoexample.fragments.start

import androidx.fragment.app.viewModels
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import pro.krit.fmpdaoexample.R
import pro.krit.fmpdaoexample.databinding.FragmentStartBinding

class StartFragment : BaseBindingFragment<FragmentStartBinding, StartViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_start

    override val viewModel: StartViewModel by viewModels()

    override fun initBinding(binding: FragmentStartBinding) {
        super.initBinding(binding)
        viewModel.openDataBase(this.requireContext().applicationContext)
    }
}