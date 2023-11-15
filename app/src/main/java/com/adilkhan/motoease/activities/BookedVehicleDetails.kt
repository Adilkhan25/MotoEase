package com.adilkhan.motoease.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.adilkhan.motoease.R
import com.adilkhan.motoease.databinding.ActivityBookedVehicleDatailsBinding
import com.adilkhan.motoease.databinding.CustomDialogCancelBinding
import com.adilkhan.motoease.firebase.FireStoreClass
import com.adilkhan.motoease.models.Vehicle
import com.adilkhan.motoease.utils.Constants
import com.bumptech.glide.Glide

class BookedVehicleDetails : BaseActivity() {
    private lateinit var bindingBookedVehicleDetails: ActivityBookedVehicleDatailsBinding
    private lateinit var mVehicleId : String
    private lateinit var vehicl : Vehicle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingBookedVehicleDetails = ActivityBookedVehicleDatailsBinding.inflate(layoutInflater)
        setContentView(bindingBookedVehicleDetails.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
            , WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        if (intent.hasExtra(Constants.VEHICLE_ID)) {
            mVehicleId = intent.getStringExtra(Constants.VEHICLE_ID)!!
        }
        bindingBookedVehicleDetails.btnCancelNow.setOnClickListener {
            val customDialog = Dialog(this)
            val customDialogBinding = CustomDialogCancelBinding.inflate(layoutInflater)
            customDialog.setContentView(customDialogBinding.root)
            customDialog.setCanceledOnTouchOutside(false)
            customDialogBinding.btnYes.setOnClickListener {
                customDialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().upDateBookedVehicle(this@BookedVehicleDetails, vehicl)
            }
            customDialogBinding.btnNo.setOnClickListener {
                customDialog.dismiss()
            }

            customDialog.show()
        }
        FireStoreClass().getVehicleDetails(this@BookedVehicleDetails,mVehicleId)
    }
    private fun setupActionBar() {

        setSupportActionBar(bindingBookedVehicleDetails.toolbarVehicleDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        bindingBookedVehicleDetails.toolbarVehicleDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }
    fun updateVehicleDetails(vehicle: Vehicle)
    {
        vehicl = vehicle
        vehicl.id = mVehicleId
        hideProgressDialog()
        Glide
            .with(this@BookedVehicleDetails)
            .load(vehicle.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(bindingBookedVehicleDetails.ivVehicleMainImage)
       bindingBookedVehicleDetails.tvVehicleName.text = vehicle.name
        bindingBookedVehicleDetails.tvVehiclePrice.text = "₹" +vehicle.price.toString() +resources.getString(R.string.per_day)
        bindingBookedVehicleDetails.tvMaxPower.text = resources.getString(R.string.max_power)+ "\n" + vehicle.maxpower
        bindingBookedVehicleDetails.tvTopSpeed.text = resources.getString(R.string.top_speed)+ "\n" + vehicle.topspeed
        bindingBookedVehicleDetails.tvMotor.text =  resources.getString(R.string.motor)+ "\n" + vehicle.motor
        if(!vehicle.aircondition) bindingBookedVehicleDetails.tvAc.visibility = View.INVISIBLE
        if(!vehicle.music) bindingBookedVehicleDetails.tvMusic.visibility = View.INVISIBLE
        if(!vehicle.backcamera) bindingBookedVehicleDetails.backCamera.visibility = View.INVISIBLE
        if(!vehicle.gps)  bindingBookedVehicleDetails.tvGps.visibility= View.INVISIBLE
        bindingBookedVehicleDetails.tvSeater.text = vehicle.details.toString()+" "+resources.getString(R.string.seater)
        bindingBookedVehicleDetails.tvRunOn.text = vehicle.runon
        hideProgressDialog()
        Glide
            .with(this@BookedVehicleDetails)
            .load(vehicle.renterimage)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(bindingBookedVehicleDetails.ivRenterImage)
        bindingBookedVehicleDetails.tvRenterName.text = "${resources.getString(R.string.Renter_name)} ${vehicle.rentername}"
        bindingBookedVehicleDetails.tvRenterContactNumber.text = "${resources.getString(R.string.renter_contact_number)} ${vehicle.renternumber}"
        bindingBookedVehicleDetails.tvVehicleNumber.text = "${resources.getString(R.string.vehicle_number)} ${vehicle.vehicleNo}"
        bindingBookedVehicleDetails.tvTotalAmount.text = vehicle.totalPrice
        val refundAmount = vehicle.totalPrice
        val rfa = refundAmount.toString().substring(1).toDouble() - 150.0
        bindingBookedVehicleDetails.tvRefundAmount.text = "₹${rfa}"

    }
    fun successFullCancel()
    {
        hideProgressDialog()
        showSuccessSnackBar(resources.getString(R.string.cancel_msg))
        Handler().postDelayed({
        startActivity(Intent(this@BookedVehicleDetails, MainActivity::class.java))

        },4000)


    }
}