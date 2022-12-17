package com.rimir.linky.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.rimir.linky.R
import com.rimir.linky.data.Folder
import com.rimir.linky.databinding.ListItemFolderBinding

private const val layoutID = R.layout.list_item_folder

class FolderArrayAdapter(
    context: Context,
) : ArrayAdapter<Folder>(context, layoutID) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutID, parent, false)
        val binding = ListItemFolderBinding.bind(view)
        val folder = getItem(position)
        if(folder != null) {
            binding.folderNameTxt.text = folder.name
            binding.folderPinImg.visibility = if(folder.isPinned) View.VISIBLE else View.INVISIBLE
            binding.folderIconImg.setImageResource(folder.folderColor.drawableId)
        }
        return view
    }
}