package xyz.ruin.kiki

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tag.view.*
import xyz.ruin.kiki.model.Tag

class TagAdapter(private val tagList: ArrayList<Tag>) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
    private lateinit var viewGroupContext: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): TagAdapter.TagViewHolder {
        viewGroupContext = viewGroup.context
        val itemView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_tag, viewGroup, false)
        return TagViewHolder(itemView)
    }

    override fun getItemCount(): Int = tagList.size

    override fun onBindViewHolder(tagViewHolder: TagAdapter.TagViewHolder, itemIndex: Int) {
        val tag: Tag = tagList[itemIndex]
        setPropertiesForTagViewHolder(tagViewHolder, tag)
    }

    private fun getTagCategoryColor(category: String): Int = when(category) {
        "general" -> R.color.tagGeneralDark
        "copyright" -> R.color.tagCopyrightDark
        "character" -> R.color.tagCharacterDark
        "artist" -> R.color.tagArtistDark
        "meta" -> R.color.tagMetaDark
        "faults" -> R.color.tagFaultsDark
        "batch" -> R.color.tagBatchDark
        else -> R.color.tagGeneralDark
    }

    private fun setPropertiesForTagViewHolder(tagViewHolder: TagAdapter.TagViewHolder, tag: Tag) {
        tagViewHolder.text.text = tag.name
        val color = getTagCategoryColor(tag.category);
        tagViewHolder.text.setTextColor(viewGroupContext.getColor(color));
    }

    inner class TagViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView by lazy { view.tag_name }
    }
}