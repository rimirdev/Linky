package com.rimir.linky.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rimir.linky.R
import com.rimir.linky.data.Link
import com.rimir.linky.databinding.ListItemLinkBinding
import com.rimir.linky.util.URLSUtils
import com.rimir.linky.util.fileExtensions
import java.net.URL

class LinkAdapter : ListAdapter<Link, RecyclerView.ViewHolder>(LinkDiffCallback()) {

    private lateinit var onLinkClick: (Link, Int) -> Unit
    private lateinit var onLinkLongClick: (Link) -> Unit
    private var enableClickCounter = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val link = getItem(position)
        (holder as LinkViewHolder).bind(link)
    }

    private fun updateClickCounter(position: Int) {
        getItem(position).clickedCount++
        notifyItemChanged(position)
    }

    fun setOnLinkClickListener(listener: (Link, Int) -> Unit) {
        onLinkClick = listener
    }

    fun setOnLinkLongClickListener(listener: (Link) -> Unit) {
        onLinkLongClick = listener
    }

    fun setEnableClickCounter(enable: Boolean) {
        enableClickCounter = enable
    }

    inner class LinkViewHolder(private val binding: ListItemLinkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(link: Link) {

            val url = URL(link.url)
            val host: String = url.getHost()
            Log.d("rimir2", "bind: " + host)
            when (host) {
                "www.youtube.com" -> binding.linkImage.setImageResource(R.drawable.ic_youtube)
                "youtu.be" -> binding.linkImage.setImageResource(R.drawable.ic_youtube)

                "www.telegram.me" -> binding.linkImage.setImageResource(R.drawable.ic_telegram)
                "telegram.me" -> binding.linkImage.setImageResource(R.drawable.ic_telegram)

                "www.pinterest.com" -> binding.linkImage.setImageResource(R.drawable.ic_pinterest)
                "pinterest.com" -> binding.linkImage.setImageResource(R.drawable.ic_pinterest)

                "www.tiktok.com" -> binding.linkImage.setImageResource(R.drawable.ic_tik_tok)
                "tiktok.com" -> binding.linkImage.setImageResource(R.drawable.ic_tik_tok)

                "www.whatsapp.com" -> binding.linkImage.setImageResource(R.drawable.ic_whatsapp)
                "whatsapp.com" -> binding.linkImage.setImageResource(R.drawable.ic_whatsapp)

                "www.gmail.com" -> binding.linkImage.setImageResource(R.drawable.ic_gmail)
                "gmail.com" -> binding.linkImage.setImageResource(R.drawable.ic_gmail)

                "www.twitter.com" -> binding.linkImage.setImageResource(R.drawable.ic_twitter_login)
                "twitter.com" -> binding.linkImage.setImageResource(R.drawable.ic_twitter_login)

                "www.instagram.com" -> binding.linkImage.setImageResource(R.drawable.ic_instagram2)
                "instagram.com" -> binding.linkImage.setImageResource(R.drawable.ic_instagram2)

                "www.facebook.com" -> binding.linkImage.setImageResource(R.drawable.ic_facebook_new)
                "facebook.com" -> binding.linkImage.setImageResource(R.drawable.ic_facebook_new)

                else -> { // Note the block

                    when (URLSUtils.getExtension(link.url)) {
                        ".pdf" -> binding.linkImage.setImageResource(R.drawable.ic_pdf_doc)
                        ".doc" -> binding.linkImage.setImageResource(R.drawable.ic_word_doc)
                        ".ppsx" -> binding.linkImage.setImageResource(R.drawable.ic_pptx_doc)
                        ".pptx" -> binding.linkImage.setImageResource(R.drawable.ic_pptx_doc)
                        ".zip" -> binding.linkImage.setImageResource(R.drawable.ic_zip_file)
                        ".rar" -> binding.linkImage.setImageResource(R.drawable.ic_zip_file)
                        ".jpg" -> binding.linkImage.setImageResource(R.drawable.ic_multi_image)
                        ".jpeg" -> binding.linkImage.setImageResource(R.drawable.ic_multi_image)
                        ".png" -> binding.linkImage.setImageResource(R.drawable.ic_multi_image)

                        else -> { // Note the block
                            binding.linkImage.setImageResource(R.drawable.ic_internet)
                        }
                    }
                }
            }

            binding.linkTitle.text = link.title
            binding.linkSubtitle.text = link.subtitle
            if (enableClickCounter) binding.linkClickCount.text = link.clickedCount.toString()
            else binding.linkClickCount.visibility = View.GONE
            binding.linkPinImg.visibility = if (link.isPinned) View.VISIBLE else View.INVISIBLE

            if (::onLinkClick.isInitialized) {
                itemView.setOnClickListener {
                    onLinkClick(link, adapterPosition)
                    updateClickCounter(adapterPosition)
                }
            }

            if (::onLinkLongClick.isInitialized) {
                itemView.setOnLongClickListener {
                    onLinkLongClick(link)
                    true
                }
            }
        }
    }
}

private class LinkDiffCallback : DiffUtil.ItemCallback<Link>() {

    override fun areItemsTheSame(oldItem: Link, newItem: Link): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Link, newItem: Link): Boolean {
        return oldItem == newItem
    }
}