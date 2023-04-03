package com.rasalexman.fmpdaoexample.fragments.users

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.rasalexman.easyrecyclerbinding.DiffCallback
import com.rasalexman.easyrecyclerbinding.createRecyclerConfig
import com.rasalexman.sresult.common.extensions.orIfEmpty
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import com.rasalexman.fmpdaoexample.BR
import com.rasalexman.fmpdaoexample.R
import com.rasalexman.fmpdaoexample.constants.ArgNames
import com.rasalexman.fmpdaoexample.databinding.FragmentUsersBinding
import com.rasalexman.fmpdaoexample.databinding.ItemUserBinding
import com.rasalexman.fmpdaoexample.models.SymptomItemUI
import com.rasalexman.fmpdaoexample.models.UserItemUI

class UsersFragment : BaseBindingFragment<FragmentUsersBinding, UsersViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_users

    override val needBackButton: Boolean
        get() = true

    override val viewModel: UsersViewModel by viewModels()

    override fun onBackArgumentsHandler(backArgs: Bundle) {
        val selectedSymptom = backArgs.getParcelable<SymptomItemUI>(ArgNames.SELECTED_SYMPTOM)
        println("-----> selectedSymptom: ${selectedSymptom?.symptomText.orIfEmpty { "-" }}" )
    }

    override fun initBinding(binding: FragmentUsersBinding) {
        super.initBinding(binding)
        binding.rvConfig = createRecyclerConfig<UserItemUI, ItemUserBinding> {
            layoutId = R.layout.item_user
            itemId = BR.item
            onItemClick = { item ->
                viewModel.onUserClicked(item)
            }
            itemDecorator = listOf(
                DividerItemDecoration(
                    requireContext(), DividerItemDecoration.HORIZONTAL
                )
            )
            diffUtilCallback = object : DiffCallback<UserItemUI>() {
                override fun areContentsTheSame(oldItem: UserItemUI, newItem: UserItemUI): Boolean {
                    return oldItem.description == newItem.description
                }
            }
        }
    }
}