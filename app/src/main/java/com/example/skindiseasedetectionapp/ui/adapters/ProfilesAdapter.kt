package com.example.skindiseasedetectionapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ListRowProfilesBinding
import com.example.skindiseasedetectionapp.model.ProfilesUser
import java.text.SimpleDateFormat

class ProfilesAdapter(private val onProfileClick: (ProfilesUser) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<ProfilesUser, ProfilesAdapter.ProfileViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ListRowProfilesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = getItem(position)
        holder.bind(profile)
        holder.itemView.setOnClickListener {

        }

        val editImage = holder.binding.editButton
        editImage.setOnClickListener {
            onProfileClick(profile)
        }


    }

    class ProfileViewHolder(val binding: ListRowProfilesBinding) : ViewHolder(
        binding.root
    ) {
        fun bind(profile: ProfilesUser){
            binding.tvName.text = profile.name
            if(profile.fotoUrl == ""){
                binding.img.setImageDrawable(ContextCompat.getDrawable(binding.img.context,R.drawable.ic_baseline_account_circle_24))
            }else{
                Glide.with(itemView.context)
                    .load(profile.fotoUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_baseline_account_circle_24).error(R.drawable.ic_error))
                    .into(binding.img)
            }
            val date = profile.created_at?.toDate()
                ?.let { SimpleDateFormat("EE, dd-MM-yyyy").format(it) }
            binding.tvCreateAt.text = date
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ProfilesUser> =
            object : DiffUtil.ItemCallback<ProfilesUser>() {
                override fun areItemsTheSame(oldUser: ProfilesUser, newUser: ProfilesUser): Boolean {
                    return oldUser.id == newUser.id && oldUser.name == newUser.name && oldUser.created_at == newUser.created_at
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: ProfilesUser, newUser: ProfilesUser): Boolean {
                    return oldUser == newUser
                }
            }
    }


}