package com.rimir.linky.ui.folderlist

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rimir.linky.ui.adapter.FolderAdapter
import com.rimir.linky.R
import com.rimir.linky.data.Folder
import com.rimir.linky.databinding.FragmentFolderListBinding
import com.rimir.linky.util.hide
import com.rimir.linky.util.show
import com.rimir.linky.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderListFragment : Fragment() {

    private var _binding : FragmentFolderListBinding? = null
    private val binding get() = _binding!!

    private lateinit var folderAdapter: FolderAdapter
    private val folderListViewModel by viewModels<FolderListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderListBinding.inflate(inflater, container, false)

        setupFolderRecyclerView()
        setupObservers()

        folderListViewModel.getSortedFolderList()

        return binding.root
    }

    private fun setupObservers() {
        folderListViewModel.foldersLiveData.observe(viewLifecycleOwner, {
            binding.foldersCountTxt.text = getString(R.string.folder_count, it.size)
            setupFoldersListState(it)
        })

        folderListViewModel.dataLoading.observe(viewLifecycleOwner, {
            binding.loadingIndicator.visibility = if(it) View.VISIBLE else View.GONE
        })

        folderListViewModel.errorMessages.observe(viewLifecycleOwner, { messageId ->
            activity.showSnackBar(messageId)
        })
    }

    private fun setupFoldersListState(folders : List<Folder>) {
        if(folders.isEmpty()) {
            binding.folderEmptyLottie.show()
            binding.folderEmptyLottie.resumeAnimation()
            binding.folderEmptyText.show()
            binding.folderList.hide()
        } else {
            binding.folderEmptyLottie.hide()
            binding.folderEmptyLottie.pauseAnimation()
            binding.folderEmptyText.hide()
            binding.folderList.show()
            folderAdapter.submitList(folders)
        }
    }

    private fun setupFolderRecyclerView() {
        folderAdapter = FolderAdapter()

        binding.folderList.adapter = folderAdapter
        binding.folderList.layoutManager = LinearLayoutManager(activity)

        folderAdapter.setOnFolderClickListener {
            folderListViewModel.updateFolderClickCount(it.id, it.clickedCount.plus(1))
            val bundle = bundleOf("folder" to it)
            findNavController().navigate(R.id.action_folderListFragment_to_linkListFragment, bundle)
        }

        folderAdapter.setOnFolderLongClickListener {
            val bundle = bundleOf("folder" to it)
            findNavController().navigate(R.id.action_folderListFragment_to_folderFragment, bundle)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)

        val menuItem = menu.findItem(R.id.search_action)
        val searchView = menuItem?.actionView as SearchView
        searchView.queryHint = "Search keyword"
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(searchViewQueryListener)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private val searchViewQueryListener = object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(keyword: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(keyword: String?): Boolean {
            if(keyword.isNullOrEmpty()) folderListViewModel.getSortedFolderList()
            else folderListViewModel.getSortedFolderListByKeyword(keyword.trim())
            return false
        }
    }

    override fun onDestroyView() {
        binding.folderList.adapter = null
        super.onDestroyView()
        _binding = null
    }
}