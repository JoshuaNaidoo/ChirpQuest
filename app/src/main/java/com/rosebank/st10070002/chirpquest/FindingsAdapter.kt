package com.rosebank.st10070002.chirpquest

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FindingsAdapter(private val findingsList: List<Finding>) :
    RecyclerView.Adapter<FindingsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val speciesText: TextView = view.findViewById(R.id.speciesText)
        val descriptionText: TextView = view.findViewById(R.id.descriptionText)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)
        val imageView: ImageView = view.findViewById(R.id.findingImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_finding, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val finding = findingsList[position]
        holder.speciesText.text = finding.species
        holder.descriptionText.text = finding.description
        holder.favoriteIcon.setImageResource(
            if (finding.isFavorite) R.drawable.ic_launcher_foreground else R.drawable.ic_menu_gallery
        )

        // Use Glide to load the image from the URI
        Glide.with(holder.itemView.context)
            .load(Uri.parse(finding.photoUri))
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return findingsList.size
    }
}
