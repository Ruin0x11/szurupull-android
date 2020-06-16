package xyz.ruin.kiki

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_upload.view.*
import xyz.ruin.kiki.model.Upload

class UploadAdapter(
    private var uploadList: ArrayList<Upload>
) : RecyclerView.Adapter<UploadAdapter.UploadViewHolder>() {

    private val placeHolderImage = "https://pbs.twimg.com/profile_images/467502291415617536/SP8_ylk9.png"
    private lateinit var viewGroupContext: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): UploadViewHolder {
        viewGroupContext = viewGroup.context
        val itemView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_upload, viewGroup, false)
        return UploadViewHolder(itemView)
    }

    override fun getItemCount(): Int = uploadList.size

    override fun onBindViewHolder(uploadViewHolder: UploadViewHolder, itemIndex: Int) {
        val upload: Upload = uploadList[itemIndex]
        setPropertiesForUploadViewHolder(uploadViewHolder, upload)
    }

    private fun setPropertiesForUploadViewHolder(uploadViewHolder: UploadViewHolder, upload: Upload) {
        checkForUrlToImage(upload, uploadViewHolder)
        uploadViewHolder.id.text = upload.source

        val tagListAdapter = TagAdapter(upload.tags)
        uploadViewHolder.tagList.setHasFixedSize(true)
        uploadViewHolder.tagList.itemAnimator = DefaultItemAnimator()
        uploadViewHolder.tagList.layoutManager = LinearLayoutManager(uploadViewHolder.tagList.context, VERTICAL, false)
        uploadViewHolder.tagList.adapter = tagListAdapter
    }

    private fun checkForUrlToImage(upload: Upload, uploadViewHolder: UploadViewHolder) {
        Picasso.get()
            .load(upload.url)
            .centerInside()
            .fit()
            .into(uploadViewHolder.image)
    }

    fun setUploads(uploads: ArrayList<Upload>) {
        uploadList = uploads
        notifyDataSetChanged()
    }

    inner class UploadViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView by lazy { view.upload_image }
        val id: TextView by lazy { view.upload_id }
        val tagList: RecyclerView by lazy { view.tag_list }
    }
}