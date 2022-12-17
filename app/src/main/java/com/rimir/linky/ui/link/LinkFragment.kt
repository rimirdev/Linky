package com.rimir.linky.ui.link

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.*
import android.webkit.URLUtil
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rimir.linky.R
import com.rimir.linky.data.Folder
import com.rimir.linky.data.Link
import com.rimir.linky.databinding.FragmentLinkBinding
import com.rimir.linky.ui.adapter.FolderArrayAdapter
import com.rimir.linky.ui.widget.PinnedLinksWidget
import com.rimir.linky.util.showError
import com.rimir.linky.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class LinkFragment : Fragment() {

    private var _binding: FragmentLinkBinding? = null
    private val binding get() = _binding!!

    private val safeArguments by navArgs<LinkFragmentArgs>()

    private lateinit var currentLink: Link
    private var linkFolderID: Int = -1
    private lateinit var folderMenuAdapter: FolderArrayAdapter
    private val linkViewModel by viewModels<LinkViewModel>()

    private val simpleDateFormatter = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        safeArguments.link?.let { currentLink = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkBinding.inflate(inflater, container, false)

        handleIntentSharedLink()
        handleLinkArgument()
        setupObservers()
        setupFolderListMenu()

        linkViewModel.getFolderList()

        binding.linkUrlLayout.setEndIconOnClickListener {
            val clipBoardManager = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val copiedString = clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()
            binding.linkUrlEdit.setText(copiedString)

        }
        return binding.root
    }

    private fun handleIntentSharedLink() {
        val sharedLink = arguments?.getString("shared_link") ?: return

        if (URLUtil.isValidUrl(sharedLink).not()) {
            binding.linkUrlLayout.showError(R.string.error_link_url_invalid)
            return
        }

        binding.linkUrlEdit.setText(sharedLink)
        linkViewModel.generateLinkTitleAndSubTitle(sharedLink)
    }

    private fun handleLinkArgument() {
        if (::currentLink.isInitialized) {
            binding.linkTitleEdit.setText(currentLink.title)
            binding.linkSubtitleEdit.setText(currentLink.subtitle)
            binding.linkUrlEdit.setText(currentLink.url)
            binding.linkPinnedSwitch.isChecked = currentLink.isPinned
            val linkCreatedStamp =
                if (currentLink.createdTime == 0L) System.currentTimeMillis() else currentLink.createdTime
            val formattedCreationDate = simpleDateFormatter.format(linkCreatedStamp)
            binding.linkCreatedStatus.text = "Created at ${formattedCreationDate}"
            if (currentLink.isUpdated) {
                val linkUpdatedStamp =
                    if (currentLink.createdTime == 0L) System.currentTimeMillis() else currentLink.createdTime
                val formattedUpdateDate = simpleDateFormatter.format(linkUpdatedStamp)
                binding.linkUpdatedStatus.text = "Last updated at ${formattedUpdateDate}"
            }
            if (currentLink.folderId != -1) linkViewModel.getFolderWithId(currentLink.folderId)
        }
    }

    private fun setupObservers() {
        linkViewModel.currentFolderLiveData.observe(viewLifecycleOwner) {
            binding.folderNameMenu.setText(it.name, false)
            linkFolderID = it.id
        }

        linkViewModel.folderLiveData.observe(viewLifecycleOwner) {
            folderMenuAdapter.addAll(it)
        }

        linkViewModel.linkInfoLiveData.observe(viewLifecycleOwner) {
            binding.linkTitleEdit.setText(it.linkTitle)
            binding.linkSubtitleEdit.setText(it.linkSubtitle)
        }

        linkViewModel.completeSuccessTask.observe(viewLifecycleOwner) {
            PinnedLinksWidget.refresh(requireContext())
            findNavController().navigateUp()
        }

        linkViewModel.errorMessages.observe(viewLifecycleOwner) { messageId ->
            activity.showSnackBar(messageId)
        }
    }

    private fun setupFolderListMenu() {
        binding.folderNameMenu.setOnItemClickListener { _, _, position, _ ->
            val folder = folderMenuAdapter.getItem(position)
            if (folder != null) linkFolderID = folder.id
        }

        folderMenuAdapter = FolderArrayAdapter(requireContext())
        folderMenuAdapter.add(Folder("None", false, id = -1))
        binding.folderNameMenu.setAdapter(folderMenuAdapter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        inflater.inflate(R.menu.menu_delete, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_action -> {
                if (::currentLink.isInitialized) updateCurrentLink()
                else createNewLink()
                true
            }
            R.id.delete_action -> {
                if (::currentLink.isInitialized) deleteCurrentLink()
                else findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createNewLink() {
        val title = binding.linkTitleEdit.text.toString()
        val subtitle = binding.linkSubtitleEdit.text.toString()
        val url = binding.linkUrlEdit.text.toString()
        val isPinned = binding.linkPinnedSwitch.isChecked

        if (title.isEmpty()) {
            binding.linkTitleLayout.showError(R.string.error_link_title_empty)
            return
        }

        if (url.isEmpty()) {
            binding.linkUrlLayout.showError(R.string.error_link_url_empty)
            return
        }

        if (URLUtil.isValidUrl(url).not()) {
            binding.linkUrlLayout.showError(R.string.error_link_url_invalid)
            return
        }

        val link = Link(title, subtitle, url, isPinned, folderId = linkFolderID)
        linkViewModel.createNewLink(link)
    }

    private fun updateCurrentLink() {
        val title = binding.linkTitleEdit.text.toString()
        val subtitle = binding.linkSubtitleEdit.text.toString()
        val url = binding.linkUrlEdit.text.toString()
        val isPinned = binding.linkPinnedSwitch.isChecked

        if (title.isEmpty()) {
            binding.linkTitleLayout.showError(R.string.error_link_title_empty)
            return
        }

        if (url.isEmpty()) {
            binding.linkUrlLayout.showError(R.string.error_link_url_empty)
            return
        }

        if (URLUtil.isValidUrl(url).not()) {
            binding.linkUrlLayout.showError(R.string.error_link_url_invalid)
            return
        }

        currentLink.title = title
        currentLink.subtitle = subtitle
        currentLink.url = url
        currentLink.isPinned = isPinned
        currentLink.isUpdated = true
        currentLink.folderId = linkFolderID
        currentLink.lastUpdatedTime = System.currentTimeMillis()

        linkViewModel.updateLink(currentLink)
    }

    private fun deleteCurrentLink() {
        linkViewModel.deleteLink(currentLink)
    }

    override fun onDestroyView() {
        binding.folderNameMenu.setAdapter(null)
        super.onDestroyView()
        _binding = null
    }
}