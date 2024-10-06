package com.rosebank.st10070002.chirpquest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class FlockAdapter(private var flockList: MutableList<BirdCapture>) : RecyclerView.Adapter<FlockAdapter.FlockViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flock_item, parent, false)
        return FlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlockViewHolder, position: Int) {
        val birdCapture = flockList[position]
        holder.bind(birdCapture)
    }

    override fun getItemCount(): Int {
        return flockList.size
    }

    fun updateFlock(newFlock: List<BirdCapture>) {
        flockList.clear()
        flockList.addAll(newFlock)
        notifyDataSetChanged()
    }

    class FlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val postDateTextView: TextView = itemView.findViewById(R.id.postDateTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.flockImageView)
        private val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)

        fun bind(birdCapture: BirdCapture) {
            usernameTextView.text = birdCapture.username
            postDateTextView.text = birdCapture.date

            // Load user avatar using Glide (if you have user avatars saved)


            // Load bird image using Glide
            Glide.with(itemView.context)
                .load(birdCapture.imageUrl)
                .placeholder(R.drawable.addimagebutton)
                .error(R.drawable.addimagebutton)
                .into(imageView)
        }
    }
}

