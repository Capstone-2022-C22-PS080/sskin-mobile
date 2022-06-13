package com.example.skindiseasedetectionapp.ui.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ListRowScanBinding
import com.example.skindiseasedetectionapp.model.DetectionHistories
import java.text.SimpleDateFormat

class DetectionsAdapter(private val onProfileClick: (DetectionHistories) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<DetectionHistories, DetectionsAdapter.ScanViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val binding = ListRowScanBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ScanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        val profile = getItem(position)
        holder.bind(profile)
        holder.itemView.setOnClickListener {

        }




    }

    class ScanViewHolder(val binding: ListRowScanBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(detectionHistories: DetectionHistories) {
            Glide.with(itemView.context)
                .load(Uri.parse(detectionHistories.photoLocation))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_baseline_account_circle_24).error(R.drawable.ic_error))
                .into(binding.img)
            binding.tvResult.text = detectionHistories.disease_name
            val date = SimpleDateFormat("dd-MM-yyyy").format(detectionHistories.takenPhoto?.toDate())
            binding.tvDate.text = date
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DetectionHistories> =
            object : DiffUtil.ItemCallback<DetectionHistories>() {
                override fun areItemsTheSame(oldUser: DetectionHistories, newUser: DetectionHistories): Boolean {
                    return oldUser.disease_id == newUser.disease_id && oldUser.takenPhoto == newUser.takenPhoto && oldUser.photoLocation == newUser.photoLocation
                            && oldUser.profile_id == newUser.profile_id && oldUser.userId == newUser.userId
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: DetectionHistories, newUser: DetectionHistories): Boolean {
                    return oldUser == newUser
                }
            }
    }
}