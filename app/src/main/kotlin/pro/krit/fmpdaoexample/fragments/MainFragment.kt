package pro.krit.fmpdaoexample.fragments

import androidx.fragment.app.viewModels
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import pro.krit.fmpdaoexample.R
import pro.krit.fmpdaoexample.databinding.FragmentMainBinding

class MainFragment : BaseBindingFragment<FragmentMainBinding, MainViewModel>() {

    override val layoutId: Int
        get() = R.layout.fragment_main

    override val viewModel: MainViewModel by viewModels()
}