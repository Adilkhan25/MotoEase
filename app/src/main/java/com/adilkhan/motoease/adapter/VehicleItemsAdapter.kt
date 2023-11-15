package com.adilkhan.motoease.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adilkhan.motoease.R
import com.adilkhan.motoease.databinding.VehicleItemBinding
import com.adilkhan.motoease.models.Vehicle
import com.bumptech.glide.Glide

open class VehicleItemsAdapter(private val context: Context,
                               private var list: ArrayList<Vehicle>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class MyViewHolder(private val itemBinding: VehicleItemBinding):RecyclerView.ViewHolder(itemBinding.root)
    {
        @SuppressLint("SetTextI18n")
        fun bindItem(model:Vehicle)
        {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_nav_user)
                .into(itemBinding.ivVehicleImage)

            itemBinding.tvVehicleName.text = model.name
            itemBinding.tvVehicleRate.numStars = model.rate.toInt()
            itemBinding.tvVehicleDetail.text = model.details.toString() + " seater"
            itemBinding.tvVehiclePrice.text = "₹" + model.price.toString() + " per day"
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            VehicleItemBinding.inflate(
            LayoutInflater.from(context),
            parent,false)
        )
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder)
        {
            holder.bindItem(model)
            holder.itemView.setOnClickListener{
                if(onClickListener!=null)
                {
                    onClickListener!!.ocClick(position,model)
                }
            }
        }
    }
    fun setOnClickListener(onClickListener: OnClickListener)
    {
       this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun ocClick(position: Int,model:Vehicle)
    }

    override fun getItemCount(): Int {
        return list.size
    }


}