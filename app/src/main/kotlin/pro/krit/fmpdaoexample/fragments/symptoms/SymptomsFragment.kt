package com.rasalexman.fmpdaoexample.fragments.symptoms

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.rasalexman.easyrecyclerbinding.createRecyclerConfig
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import com.rasalexman.fmpdaoexample.BR
import com.rasalexman.fmpdaoexample.R
import com.rasalexman.fmpdaoexample.databinding.FragmentSymptomsBinding
import com.rasalexman.fmpdaoexample.databinding.ItemSymptomBinding
import com.rasalexman.fmpdaoexample.models.SymptomItemUI

class SymptomsFragment : BaseBindingFragment<FragmentSymptomsBinding, SymptomsViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_symptoms

    override val needBackButton: Boolean
        get() = true

    override val canPressBack: Boolean
        get() = false

    override val viewModel: SymptomsViewModel by viewModels()

    override fun initBinding(binding: FragmentSymptomsBinding) {
        super.initBinding(binding)
        binding.rvConfig = createRecyclerConfig<SymptomItemUI, ItemSymptomBinding> {
            layoutId = R.layout.item_symptom
            itemId = BR.item
            itemDecorator = listOf(DividerItemDecoration(
                requireContext(), DividerItemDecoration.HORIZONTAL
            ))
            onItemClick = { item ->
                viewModel.onSymptomClicked(item)
            }
        }
    }

    override fun onToolbarBackPressed() {
        viewModel.onBackClicked()
    }
}