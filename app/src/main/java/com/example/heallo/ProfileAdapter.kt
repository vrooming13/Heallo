package com.example.heallo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ProfileAdapter (val profileList: ArrayList<profiles>) : RecyclerView.Adapter<ProfileAdapter.CustomViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.CustomViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_mainview2, parent, false)
        return CustomViewHolder(view).apply {
            itemView.setOnClickListener {

                val curPos : Int = adapterPosition
                val profile: profiles = profileList.get(curPos)
                Toast.makeText(parent.context, "이름 : ${profile.name}\n레벨 : ${profile.level}", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onBindViewHolder(holder: ProfileAdapter.CustomViewHolder, position: Int) {
        holder.gender.setImageResource(profileList.get(position).gender)
        holder.name.text = profileList.get(position).name
        holder.level.text = profileList.get(position).level.toString()
        holder.review.text = profileList.get(position).review
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gender = itemView.findViewById<ImageView>(R.id.iv_profile)  //성별
        val name = itemView.findViewById<TextView>(R.id.tv_name)        //이름
        val level = itemView.findViewById<TextView>(R.id.tv_level)      //레벨
        val review = itemView.findViewById<TextView>(R.id.tv_review)    //리뷰

    }
}