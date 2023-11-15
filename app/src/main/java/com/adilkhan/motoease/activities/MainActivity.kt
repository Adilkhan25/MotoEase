package com.adilkhan.motoease.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.adilkhan.motoease.R
import com.adilkhan.motoease.adapter.VehicleItemsAdapter
import com.adilkhan.motoease.databinding.ActivityMainBinding
import com.adilkhan.motoease.firebase.FireStoreClass
import com.adilkhan.motoease.models.User
import com.adilkhan.motoease.models.Vehicle
import com.adilkhan.motoease.utils.Constants
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        // TODO (Step 8: Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.)
        // START
        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        binding.navView.setNavigationItemSelectedListener(this)
        // END
        // TODO (Step 3: Call a function to get the current logged in user details.)
        // START
        // Get the current logged in user details.
        FireStoreClass().loadUserData(this@MainActivity)
        // END
        showProgressDialog("Data Loading")
        FireStoreClass().getVehiclesList(this@MainActivity,"all")
        /**
         * Apply filter from here
         *
         */
        binding.toolbarMAin.mainContentUi.tvAllCars.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            // change ui
            binding.toolbarMAin.mainContentUi.tvAllCars.setTextColor(resources.getColor(R.color.bc_colour))
            binding.toolbarMAin.mainContentUi.tvAllBikes.setTextColor(resources.getColor(R.color.text_color))
            binding.toolbarMAin.mainContentUi.tvAllVehicles.setTextColor(resources.getColor(R.color.text_color))

            binding.toolbarMAin.mainContentUi.tvAllVehicles.background = resources.getDrawable(R.drawable.white_border_shape_button_rounded)
            binding.toolbarMAin.mainContentUi.tvAllCars.background = resources.getDrawable(R.drawable.shape_button_rounded)
            binding.toolbarMAin.mainContentUi.tvAllBikes.background = resources.getDrawable(R.drawable.white_border_shape_button_rounded)

            FireStoreClass().getVehiclesList(this@MainActivity,"car")
        }
        binding.toolbarMAin.mainContentUi.tvAllBikes.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            // change ui
            binding.toolbarMAin.mainContentUi.tvAllBikes.setTextColor(resources.getColor(R.color.bc_colour))
            binding.toolbarMAin.mainContentUi.tvAllCars.setTextColor(resources.getColor(R.color.text_color))
            binding.toolbarMAin.mainContentUi.tvAllVehicles.setTextColor(resources.getColor(R.color.text_color))

            binding.toolbarMAin.mainContentUi.tvAllVehicles.background = resources.getDrawable(R.drawable.white_border_shape_button_rounded)
            binding.toolbarMAin.mainContentUi.tvAllBikes.background = resources.getDrawable(R.drawable.shape_button_rounded)
            binding.toolbarMAin.mainContentUi.tvAllCars.background = resources.getDrawable(R.drawable.white_border_shape_button_rounded)
            FireStoreClass().getVehiclesList(this@MainActivity,"bike")
        }
        binding.toolbarMAin.mainContentUi.tvAllVehicles.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            // change ui
            binding.toolbarMAin.mainContentUi.tvAllVehicles.setTextColor(resources.getColor(R.color.bc_colour))
            binding.toolbarMAin.mainContentUi.tvAllBikes.setTextColor(resources.getColor(R.color.text_color))
            binding.toolbarMAin.mainContentUi.tvAllCars.setTextColor(resources.getColor(R.color.text_color))

            binding.toolbarMAin.mainContentUi.tvAllCars.background = resources.getDrawable(R.drawable.white_border_shape_button_rounded)
            binding.toolbarMAin.mainContentUi.tvAllVehicles.background = resources.getDrawable(R.drawable.shape_button_rounded)
            binding.toolbarMAin.mainContentUi.tvAllBikes.background = resources.getDrawable(R.drawable.white_border_shape_button_rounded)
            FireStoreClass().getVehiclesList(this@MainActivity,"all")
        }
    }
    // TODO (Step 1: Create a function to setup action bar.)
    // START
    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {
        // val progressDialogBinding = DialogProgressBinding.inflate(layoutInflater)
        // val bindingAppBarMain = AppBarMainBinding.inflate(layoutInflater)
        // setSupportActionBar(bindingAppBarMain.toolbarMainActivity)
        binding.toolbarMAin.toolbarMainActivity.setNavigationIcon(R.drawable.ic_user_place_holder)
        binding.toolbarMAin.toolbarMainActivity.title = resources.getString(R.string.user_location)

        // TODO (Step 3: Add click event for navigation in the action bar and call the toggleDrawer function.)
        // START
        binding.toolbarMAin.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
        // END
    }
    // END
    // TODO (Step 2: Create a function for opening and closing the Navigation Drawer.)
    // START
    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    // END
    // TODO (Step 7: Implement members of NavigationView.OnNavigationItemSelectedListener.)
    // START
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // TODO (Step 9: Add the click events of navigation menu items.)
        // START
        when (menuItem.itemId) {
            R.id.nav_edit_profile -> {

                // TODO (Step 2: Launch the my profile activity for Result.)
                // START
                startActivityForResult(Intent(this@MainActivity, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
                // END
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.nav_my_booking ->{
                // Send the user to the intro screen of the application.
                val intent = Intent(this,MyBooking::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        // END
        return true
    }
    // END

    // START
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FireStoreClass().loadUserData(this@MainActivity)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }
    // END
    // START
    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user: User) {
        // The instance of the header view of the navigation view.
        Constants.USER_ID = user.id
        val headerView = binding.navView.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)

        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername.text = user.name
    }
    // END

    /**
     * A function to populate the vehicle List Ui
     */
    //START
    fun populateVehicleListUi(vehicleList:ArrayList<Vehicle>)
    {
        hideProgressDialog()
        if (vehicleList.size > 0) {
                binding.toolbarMAin.mainContentUi.rvVehiclesList.visibility = View.VISIBLE
                binding.toolbarMAin.mainContentUi.tvNoVehiclesAvailable.visibility = View.GONE

                binding.toolbarMAin.mainContentUi.rvVehiclesList.layoutManager =
                    LinearLayoutManager(this@MainActivity)
                binding.toolbarMAin.mainContentUi.rvVehiclesList.setHasFixedSize(true)

//            // Create an instance of BoardItemsAdapter and pass the boardList to it.
//            vehicleList.sortedByDescending { it.price }
//            Log.i("vehicle sort ", "${vehicleList[1].price}")
                val adapter = VehicleItemsAdapter(this@MainActivity, vehicleList)
                binding.toolbarMAin.mainContentUi.rvVehiclesList.adapter =
                    adapter // Attach the adapter to the recyclerView.

                // TODO (Step 9: Add click event for boards item and launch the TaskListActivity)
                // START
                adapter.setOnClickListener(object : VehicleItemsAdapter.OnClickListener {
                    override fun ocClick(position: Int, model: Vehicle) {
                        val intent = Intent(this@MainActivity, VehicleDetailsActivity::class.java)
                        intent.putExtra(Constants.VEHICLE_ID, model.id)
                        startActivity(intent)
                    }
                })
            // END
        } else {
            binding.toolbarMAin.mainContentUi.rvVehiclesList.visibility = View.GONE
            binding.toolbarMAin.mainContentUi.tvNoVehiclesAvailable.visibility = View.VISIBLE
        }

    }
    // TODO (Step 1: Create a companion object and a constant variable for My profile Screen result.)
    // START
    /**
     * A companion object to declare the constants.
     */
    companion object {
        //A unique code for starting the activity for result
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }
    // END
}