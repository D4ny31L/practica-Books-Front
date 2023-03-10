package com.example.libroapi

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(val postModel: MutableList<PostModel>): RecyclerView.Adapter<PostViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(postModel: PostModel)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_post,parent,false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postModel.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bindView(postModel[position])
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(postModel[position])
        }
    }

}

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private val booksViewerBookname: TextView = itemView.findViewById(R.id.booksViewerBookname)
    private val booksViewerAuthor: TextView = itemView.findViewById(R.id.booksViewerAuthor)
    private val booksViewerYear: TextView = itemView.findViewById(R.id.booksViewerYear)
    private val booksViewerPrice: TextView = itemView.findViewById(R.id.booksViewerPrice)
    private val booksViewerQuantity: TextView = itemView.findViewById(R.id.booksViewerQuantity)
    private val booksViewerAvil: TextView = itemView.findViewById(R.id.booksViewerAvil)
    private val booksViewerDate: TextView = itemView.findViewById(R.id.booksViewerDate)
    private val booksViewerISBN: TextView = itemView.findViewById(R.id.booksViewerISBN)

    fun bindView(postModel: PostModel){
        booksViewerBookname.text = postModel.bookname
        booksViewerAuthor.text = postModel.author
        booksViewerYear.text = postModel.year.toString()
        booksViewerPrice.text = postModel.price.toString()
        booksViewerQuantity.text = postModel.quantity.toString()
        booksViewerAvil.text = postModel.avil.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val date = dateFormat.parse(postModel.date)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        booksViewerDate.text = formattedDate
        booksViewerISBN.text = postModel.isbn.toString()

    }

}