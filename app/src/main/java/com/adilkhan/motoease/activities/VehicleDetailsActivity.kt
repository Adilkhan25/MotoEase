package com.adilkhan.motoease.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.adilkhan.motoease.R
import com.adilkhan.motoease.databinding.ActivityVehicleDetailsBinding
import com.adilkhan.motoease.firebase.FireStoreClass
import com.adilkhan.motoease.models.Vehicle
import com.adilkhan.motoease.utils.Constants
import com.bumptech.glide.Glide

class VehicleDetailsActivity : BaseActivity() {
    private lateinit var bindingVehicleDetailsActivity: ActivityVehicleDetailsBinding
    private lateinit var mVehicleId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingVehicleDetailsActivity = ActivityVehicleDetailsBinding.inflate(layoutInflater)
        setContentView(bindingVehicleDetailsActivity.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
            , WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        if (intent.hasExtra(Constants.VEHICLE_ID)) {
            mVehicleId = intent.getStringExtra(Constants.VEHICLE_ID)!!
        }
        FireStoreClass().getVehicleDetails(this@VehicleDetailsActivity,mVehicleId)
        bindingVehicleDetailsActivity.btnBookNow.setOnClickListener {
            val intent = Intent(this@VehicleDetailsActivity,ConfirmBookingActivity::class.java)
            intent.putExtra(Constants.VEHICLE_ID, mVehicleId)
            startActivity(intent)
        }
    }
    fun updateVehicleDetails(vehicle:Vehicle)
    {
        hideProgressDialog()
        Glide
            .with(this@VehicleDetailsActivity)
            .load(vehicle.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(bindingVehicleDetailsActivity.ivVehicleMainImage)
        bindingVehicleDetailsActivity.tvVehicleName.text = vehicle.name
        bindingVehicleDetailsActivity.tvVehiclePrice.text = "₹" + vehicle.price.toString() +resources.getString(R.string.per_day)
        bindingVehicleDetailsActivity.tvMaxPower.text = resources.getString(R.string.max_power)+ "\n" + vehicle.maxpower
        bindingVehicleDetailsActivity.tvTopSpeed.text = resources.getString(R.string.top_speed)+ "\n" + vehicle.topspeed
        bindingVehicleDetailsActivity.tvMotor.text =  resources.getString(R.string.motor)+ "\n" + vehicle.motor
        if(!vehicle.aircondition) bindingVehicleDetailsActivity.tvAc.visibility = View.INVISIBLE
        if(!vehicle.music) bindingVehicleDetailsActivity.tvMusic.visibility = View.INVISIBLE
        if(!vehicle.backcamera) bindingVehicleDetailsActivity.backCamera.visibility = View.INVISIBLE
        if(!vehicle.gps)  bindingVehicleDetailsActivity.tvGps.visibility= View.INVISIBLE
        bindingVehicleDetailsActivity.tvSeater.text = vehicle.details.toString()+" "+resources.getString(R.string.seater)
        bindingVehicleDetailsActivity.tvRunOn.text = vehicle.runon
   }
    private fun setupActionBar() {

        setSupportActionBar(bindingVehicleDetailsActivity.toolbarVehicleDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        bindingVehicleDetailsActivity.toolbarVehicleDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }
}