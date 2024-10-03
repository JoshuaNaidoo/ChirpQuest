package com.rosebank.st10070002.chirpquest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FindingsAdapter(private var findingsList: MutableList<BirdCapture>) : RecyclerView.Adapter<FindingsAdapter.FindingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindingsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.finding_item, parent, false)
        return FindingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FindingsViewHolder, position: Int) {
        val finding = findingsList[position]
        holder.bind(finding)
    }

    override fun getItemCount(): Int {
        return findingsList.size
    }

    fun updateFindings(newFindings: List<BirdCapture>) {
        findingsList.clear()
        findingsList.addAll(newFindings)
        notifyDataSetChanged()
    }

    class FindingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val speciesTextView: TextView = itemView.findViewById(R.id.speciesTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.findingImageView)

        fun bind(birdCapture: BirdCapture) {
            speciesTextView.text = birdCapture.species
            dateTextView.text = birdCapture.date
            timeTextView.text = birdCapture.time
            locationTextView.text = birdCapture.location

            // Load image using Glide
            val imageUrl = birdCapture.imageUrl
            if (imageUrl != null) {
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.addimagebutton) // Add a placeholder image
                    .into(imageView)
            } else {
                // Set a default placeholder if no image is available
                imageView.setImageResource(R.drawable.addimagebutton)
            }
        }
    }
}

