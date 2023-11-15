package com.adilkhan.motoease.activities

import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adilkhan.motoease.R
import com.adilkhan.motoease.adapter.VehicleBookedItemsAdapter

import com.adilkhan.motoease.databinding.ActivityMyBookingBinding
import com.adilkhan.motoease.firebase.FireStoreClass
import com.adilkhan.motoease.models.Vehicle
import com.adilkhan.motoease.utils.Constants

class MyBooking : BaseActivity() {
    private lateinit var bindingMyBookingActivity: ActivityMyBookingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMyBookingActivity = ActivityMyBookingBinding.inflate(layoutInflater)
        setContentView(bindingMyBookingActivity.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
            , WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBookedVehicleList(this@MyBooking)
    }
    private fun setupActionBar() {

        setSupportActionBar(bindingMyBookingActivity.toolbarVehicleMyBookingActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        bindingMyBookingActivity.toolbarVehicleMyBookingActivity.setNavigationOnClickListener { onBackPressed() }
    }
    fun updateBookedVehicleList(vehicleList:ArrayList<Vehicle>)
    {
        hideProgressDialog()
        if (vehicleList.size > 0) {

           bindingMyBookingActivity.rvVehiclesBookedList.visibility = View.VISIBLE
            bindingMyBookingActivity.btnNoVehiclesAvailable.visibility = View.GONE

            bindingMyBookingActivity.rvVehiclesBookedList.layoutManager = LinearLayoutManager(this@MyBooking)
          bindingMyBookingActivity.rvVehiclesBookedList.setHasFixedSize(true)

            // Create an instance of BoardItemsAdapter and pass the boardList to it.
            val adapter =VehicleBookedItemsAdapter(this@MyBooking, vehicleList)
            bindingMyBookingActivity.rvVehiclesBookedList.adapter = adapter // Attach the adapter to the recyclerView.

            // TODO (Step 9: Add click event for boards item and launch the TaskListActivity)
            // START
            adapter.setOnClickListener(object : VehicleBookedItemsAdapter.OnClickListener{
                override fun ocClick(position: Int, model: Vehicle) {
                    val intent = Intent(this@MyBooking,BookedVehicleDetails::class.java)
                     intent.putExtra(Constants.VEHICLE_ID, model.id)
                    startActivity(intent)
                }
            })
            // END
        } else {
            bindingMyBookingActivity.rvVehiclesBookedList.visibility = View.GONE
            bindingMyBookingActivity.btnNoVehiclesAvailable.visibility = View.VISIBLE
            bindingMyBookingActivity.btnNoVehiclesAvailable.setOnClickListener {
                startActivity(Intent(this@MyBooking, MainActivity::class.java))
                finish()
            }
        }

    }
}