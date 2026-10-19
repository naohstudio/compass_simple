package com.naohstudio.compass.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.naohstudio.compass.databinding.ItemCompassStyleBinding
import com.naohstudio.compass.feature.model.CompassStyle

class CompassStyleAdapter(
    private val onStyleSelected: (CompassStyle) -> Unit
) : RecyclerView.Adapter<CompassStyleAdapter.ViewHolder>() {

    private val styles = CompassStyle.values().toList()

    var selectedStyle: CompassStyle = CompassStyle.CLASSIC
        private set

    fun setSelected(style: CompassStyle) {
        val oldIndex = styles.indexOf(selectedStyle)
        selectedStyle = style
        notifyItemChanged(oldIndex)
        notifyItemChanged(styles.indexOf(style))
    }

    fun positionOf(style: CompassStyle) = styles.indexOf(style)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCompassStyleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(styles[position])
    }

    override fun getItemCount() = styles.size

    inner class ViewHolder(private val binding: ItemCompassStyleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(style: CompassStyle) {
            binding.imgStyle.setImageResource(style.backgroundResId)
            binding.tvStyleName.setText(style.labelResId)
            binding.root.isSelected = style == selectedStyle
            binding.root.setOnClickListener {
                if (style != selectedStyle) {
                    setSelected(style)
                    onStyleSelected(style)
                }
            }
        }
    }
}
