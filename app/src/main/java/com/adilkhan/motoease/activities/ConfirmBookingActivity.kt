package com.adilkhan.motoease.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.adilkhan.motoease.R
import com.adilkhan.motoease.databinding.ActivityConfirmBookingAcitivityBinding
import com.adilkhan.motoease.firebase.FireStoreClass
import com.adilkhan.motoease.models.User
import com.adilkhan.motoease.models.Vehicle
import com.adilkhan.motoease.utils.Constants
import com.bumptech.glide.Glide
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class ConfirmBookingActivity : BaseActivity(),PaymentResultListener {
    private lateinit var bindingConfirmBookingActivity: ActivityConfirmBookingAcitivityBinding
    private lateinit var mVehicleId : String
    private var pickeUpCalender : Calendar = Calendar.getInstance()
    private var returnCalendar : Calendar = Calendar.getInstance()
    private var vehiclePrice : Double =0.0
    private var totalPrice:Double = 0.0
    private lateinit var vehicl:Vehicle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingConfirmBookingActivity = ActivityConfirmBookingAcitivityBinding.inflate(layoutInflater)
        setContentView(bindingConfirmBookingActivity.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
            , WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
        //Update the user details like lcn and gvn
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadUserData(this@ConfirmBookingActivity)
        if (intent.hasExtra(Constants.VEHICLE_ID)) {
            mVehicleId = intent.getStringExtra(Constants.VEHICLE_ID)!!
        }
        FireStoreClass().getVehicleDetails(this@ConfirmBookingActivity,mVehicleId)

        // let choose the date
        updatePrice()
        bindingConfirmBookingActivity.etPickUpDate.setOnClickListener{
            val year = pickeUpCalender.get(Calendar.YEAR)

            val month = pickeUpCalender.get(Calendar.MONTH)

            val days = pickeUpCalender.get(Calendar.DAY_OF_MONTH)

           val datePickerDialogue =  DatePickerDialog(this@ConfirmBookingActivity,
                DatePickerDialog.OnDateSetListener{_, year, month, dayOfMonth ->
                    pickeUpCalender.set(Calendar.YEAR, year)
                    pickeUpCalender.set(Calendar.MONTH, month)
                    pickeUpCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    bindingConfirmBookingActivity.etPickUpDate.setText(sdf.format(pickeUpCalender.time).toString())
                    updatePrice()
                   // Checkout.preload(this@ConfirmBookingActivity)


                },
                year,
                month,
                days
            )
            datePickerDialogue.datePicker.minDate = System.currentTimeMillis()
            if(bindingConfirmBookingActivity.etReturnDate.text.toString().isNotEmpty())
            datePickerDialogue.datePicker.maxDate = returnCalendar.timeInMillis - (24*60*60*1000)
            datePickerDialogue.show()


        }
        bindingConfirmBookingActivity.etReturnDate.setOnClickListener {
            val year = returnCalendar.get(Calendar.YEAR)

            val month = returnCalendar.get(Calendar.MONTH)

            val days = returnCalendar.get(Calendar.DAY_OF_MONTH)

           val datePickerDialog =  DatePickerDialog(this@ConfirmBookingActivity,
                DatePickerDialog.OnDateSetListener{_, year, month, dayOfMonth ->
                    returnCalendar.set(Calendar.YEAR, year)
                    returnCalendar.set(Calendar.MONTH, month)
                    returnCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    bindingConfirmBookingActivity.etReturnDate.setText(sdf.format(returnCalendar.time).toString())
                    updatePrice()
                    //Checkout.preload(this@ConfirmBookingActivity)


                },
                year,
                month,
                days
            )
            datePickerDialog.datePicker.minDate = pickeUpCalender.timeInMillis+(24*60*60*1000)
            datePickerDialog.show()

        }
        bindingConfirmBookingActivity.btnProceedToPaymentNow.setOnClickListener {
            val lcn = bindingConfirmBookingActivity.etLicense.text.toString()
            val gvn = bindingConfirmBookingActivity.etGovId.text.toString()
            val pickUpDate = bindingConfirmBookingActivity.etPickUpDate.text.toString()
            val returnDate = bindingConfirmBookingActivity.etReturnDate.text.toString()
            if(validateForm(lcn, gvn,pickUpDate,returnDate,totalPrice)) {

                doPayment()
            }
           // Toast.makeText(this@ConfirmBookingActivity,"Btn presseed",Toast.LENGTH_LONG)
            Log.i("btn pre", "dsdfsfgegoieti")
        }
        Checkout.preload(this@ConfirmBookingActivity)


    }
    private fun setupActionBar() {

        setSupportActionBar(bindingConfirmBookingActivity.toolbarVehicleConfirmActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        bindingConfirmBookingActivity.toolbarVehicleConfirmActivity.setNavigationOnClickListener { onBackPressed() }
    }
    fun setUserDetails(user:User)
    {
        bindingConfirmBookingActivity.etLicense.setText(user.license)
        bindingConfirmBookingActivity.etGovId.setText(user.govId)
        hideProgressDialog()

    }
   @SuppressLint("SetTextI18n")
   fun updateVehicleDetails(vehicle: Vehicle)
   {
       hideProgressDialog()
       vehicl  = vehicle
       vehicl.id = mVehicleId
       vehiclePrice = vehicle.price
       Glide
           .with(this@ConfirmBookingActivity)
           .load(vehicle.image)
           .centerCrop()
           .placeholder(R.drawable.ic_user_place_holder)
           .into(bindingConfirmBookingActivity.ivVehicleMainImage)
       bindingConfirmBookingActivity.tvVehicleName.text = vehicle.name
       bindingConfirmBookingActivity.tvVehiclePrice.text =
           "${resources.getString(R.string.rupees)}${vehicle.price}/day"
       bindingConfirmBookingActivity.tvVehiclePricePerDay.text = resources.getString(R.string.rupees)+vehicle.price.toString()
   }
    @SuppressLint("SetTextI18n")
    private fun updatePrice()
    {   val pickUpDate = bindingConfirmBookingActivity.etPickUpDate.text.toString()
        val returnDate = bindingConfirmBookingActivity.etReturnDate.text.toString()
        if(!(TextUtils.isEmpty(pickUpDate) || TextUtils.isEmpty(returnDate)))
        {
                val startDay = LocalDate.parse(convertIsoDateTimeFormat(pickUpDate))
                val endDay = LocalDate.parse(convertIsoDateTimeFormat(returnDate))
                val noOfDays = ChronoUnit.DAYS.between(startDay,endDay)
                totalPrice = vehiclePrice*noOfDays
                bindingConfirmBookingActivity.tvVehicleTotalPrice.text =
                        "${resources.getString(R.string.rupees)}$totalPrice"
        }

    }
    private fun convertIsoDateTimeFormat(str: String): String {
        var ans=""
        val parts = str.split('.')
        val separateList = ArrayList<String>()
        separateList.addAll(parts)
        separateList.reverse()
        ans = separateList.joinToString('-'.toString())
        return ans
    }
    private fun doPayment()
    {
        val co = Checkout()
       // val amount = price.trim { it<='₹' }.toInt()
        val price = bindingConfirmBookingActivity.tvVehicleTotalPrice.text.toString()
        val amount = price.substring(1).toDouble()
        Log.i("total price", "pay $amount")
        co.setKeyID("rzp_test_CF8lX3aNSP6GRn")
        try {

            val options = JSONObject()

            options.put("name", bindingConfirmBookingActivity.tvVehicleName.text.toString())

            options.put("description", "Please pay for book")

            options.put("image",vehicl.image)

            options.put("theme.color", "#068DA9")

            options.put("currency", "INR")
            options.put("amount", amount*100)



            val retryObj = JSONObject()

            retryObj.put("enabled", true)

            retryObj.put("max_count", 4)

            options.put("retry", retryObj)



            co.open(this@ConfirmBookingActivity, options)

        } catch (e: Exception) {

            Toast.makeText(this@ConfirmBookingActivity, "Error in Payment : " + e.message, Toast.LENGTH_LONG)

                .show()

            e.printStackTrace()

        }
    }

    override fun onPaymentSuccess(p0: String?) {
      showProgressDialog("Please wait booking" +
              "\n in process!!")
        val lcn = bindingConfirmBookingActivity.etLicense.text.toString()
        val gvn = bindingConfirmBookingActivity.etGovId.text.toString()
        vehicl.pickUpDate = bindingConfirmBookingActivity.etPickUpDate.text.toString()
        vehicl.returnDate = bindingConfirmBookingActivity.etReturnDate.text.toString()
        vehicl.totalPrice = bindingConfirmBookingActivity.tvVehicleTotalPrice.text.toString()
        FireStoreClass().updateBookingDetails(this@ConfirmBookingActivity,vehicl,lcn,gvn)


    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this@ConfirmBookingActivity, "Payment fail : $p1", Toast.LENGTH_LONG).show()
    }
    fun successFullBooked()
    {
        hideProgressDialog()
        startActivity(Intent(this@ConfirmBookingActivity,MainActivity::class.java))
        finish()
    }
    private fun validateForm(lcn:String, gvn:String, pDate:String, rDate:String, price:Double):Boolean
    {
        return when{
            TextUtils.isEmpty(lcn)->
            {
                showErrorSnackBar("please Enter your license number")
                false
            }
            TextUtils.isEmpty(gvn)->{
                showErrorSnackBar("please Enter your valid gov id")
                false
            }
            TextUtils.isEmpty(pDate)->{
                showErrorSnackBar("please Enter pick up date")
                false
            }
            TextUtils.isEmpty(rDate)->{
                showErrorSnackBar("please Enter return date")
                false
            }
            price>vehiclePrice*7->{
                showErrorSnackBar("We can't give vehicle for more than a week")
                false
            }
            else -> true
        }
    }

}