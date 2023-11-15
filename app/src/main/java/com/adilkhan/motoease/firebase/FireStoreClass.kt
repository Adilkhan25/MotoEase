package com.adilkhan.motoease.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.adilkhan.motoease.activities.*
import com.adilkhan.motoease.models.User
import com.adilkhan.motoease.models.Vehicle
import com.adilkhan.motoease.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    // Create a instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the fireStore database.
     */
    fun registerUser(activity: SignUp, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }//END

    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */
    fun loadUserData(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(
                    activity.javaClass.simpleName, document.toString()
                )

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!
                // TODO(Step 6: Modify the parameter and check the instance of activity and send the success result to it.)
                // START
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignIn -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                    is ConfirmBookingActivity ->{
                        activity.setUserDetails(loggedInUser)
                    }
                    // END
                }
                // END
            }
            .addOnFailureListener { e ->
                // TODO(Step 2: Hide the progress dialog in failure function based on instance of activity.)
                // START
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignIn -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                    // END
                }
                // END
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }

    // START
    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
            mFireStore.collection(Constants.USERS) // Collection Name
                .document(getCurrentUserID()) // Document ID
                .update(userHashMap) // A hashmap of fields which are to be updated.
                .addOnSuccessListener {
                    // Profile data is updated successfully.
                    Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                    Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT)
                        .show()

                    // Notify the success result.
                    activity.profileUpdateSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while creating a user.",
                        e
                    )
                }

    }
    // END
    /**
     * A function to get the list of created boards from the database.
     */
    fun getVehiclesList(activity: MainActivity,type:String) {
        // The collection name for Cars
       if(type=="all")
       {
           mFireStore.collection(Constants.CARS).whereEqualTo(Constants.IS_BOOK, false)
               // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
               .get() // Will get the documents snapshots.
               .addOnSuccessListener { document ->
                   // Here we get the list of boards in the form of documents.
                   Log.e(activity.javaClass.simpleName, document.documents.toString())
                   // Here we have created a new instance for Boards ArrayList.
                   val vehicleList: ArrayList<Vehicle> = ArrayList()

                   // A for loop as per the list of documents to convert them into Boards ArrayList.
                   for (i in document.documents) {

                       val vehicle = i.toObject(Vehicle::class.java)!!
                       vehicle.id = i.id

                       vehicleList.add(vehicle)
                   }

                   // Here pass the result to the base activity
                   activity.populateVehicleListUi(vehicleList)
               }
               .addOnFailureListener { e ->

                   activity.hideProgressDialog()
                   Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
               }
       }
        else
       {
           if(type=="car")
           {
               mFireStore.collection(Constants.CARS).whereEqualTo(Constants.IS_BOOK, false)
                   .whereEqualTo(Constants.TYPE,type)
                   // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
                   .get() // Will get the documents snapshots.
                   .addOnSuccessListener { document ->
                       // Here we get the list of boards in the form of documents.
                       Log.e(activity.javaClass.simpleName, document.documents.toString())
                       // Here we have created a new instance for Boards ArrayList.
                       val vehicleList: ArrayList<Vehicle> = ArrayList()

                       // A for loop as per the list of documents to convert them into Boards ArrayList.
                       for (i in document.documents) {

                           val vehicle = i.toObject(Vehicle::class.java)!!
                           vehicle.id = i.id

                           vehicleList.add(vehicle)
                       }

                       // Here pass the result to the base activity
                       activity.populateVehicleListUi(vehicleList)
                   }
                   .addOnFailureListener { e ->

                       activity.hideProgressDialog()
                       Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
                   }
           }
           else
           {   if(type=="bike") {
               mFireStore.collection(Constants.CARS).whereEqualTo(Constants.IS_BOOK, false)
                   .whereEqualTo(Constants.TYPE, "bike")
                   // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
                   .get() // Will get the documents snapshots.
                   .addOnSuccessListener { document ->
                       // Here we get the list of boards in the form of documents.
                       Log.e(activity.javaClass.simpleName, document.documents.toString())
                       // Here we have created a new instance for Boards ArrayList.
                       val vehicleList: ArrayList<Vehicle> = ArrayList()

                       // A for loop as per the list of documents to convert them into Boards ArrayList.
                       for (i in document.documents) {

                           val vehicle = i.toObject(Vehicle::class.java)!!
                           vehicle.id = i.id

                           vehicleList.add(vehicle)
                       }

                       // Here pass the result to the base activity
                       activity.populateVehicleListUi(vehicleList)
                   }
                   .addOnFailureListener { e ->

                       activity.hideProgressDialog()
                       Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
                   }
           }
           }
       }
    }

    /**
     * A function for getting the user id of current logged user.
     */
    fun getVehicleDetails(activity: Activity,vehicleId : String)
    {
        when(activity)
        {
             is VehicleDetailsActivity ->{

                 // The collection name for Vehicles
                 mFireStore.collection(Constants.CARS)
                     // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
                     .document(vehicleId)
                     .get() // Will get the documents snapshots.
                     .addOnSuccessListener { document ->
                         val vehicle = document.toObject(Vehicle::class.java)
                         if(vehicle!=null)
                             activity.updateVehicleDetails(vehicle)
                         else activity.hideProgressDialog()
                     }
                     .addOnFailureListener { e ->

                         activity.hideProgressDialog()
                         Log.e(activity.javaClass.simpleName, "Error while loading car details.", e)
                     }
            }
            is ConfirmBookingActivity ->{
                // The collection name for Vehicles
                mFireStore.collection(Constants.CARS)
                    // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
                    .document(vehicleId)
                    .get() // Will get the documents snapshots.
                    .addOnSuccessListener { document ->
                        val vehicle = document.toObject(Vehicle::class.java)
                        if(vehicle!=null)
                            activity.updateVehicleDetails(vehicle)
                        else activity.hideProgressDialog()
                    }
                    .addOnFailureListener { e ->

                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while loading car details.", e)
                    }
            }
            is BookedVehicleDetails -> {
                // The collection name for Vehicles
                mFireStore.collection(Constants.CARS)
                    // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
                    .document(vehicleId)
                    .get() // Will get the documents snapshots.
                    .addOnSuccessListener { document ->
                        val vehicle = document.toObject(Vehicle::class.java)
                        if(vehicle!=null)
                            activity.updateVehicleDetails(vehicle)
                        else activity.hideProgressDialog()
                    }
                    .addOnFailureListener { e ->

                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while loading car details.", e)
                    }

            }
        }

    }
    /**
     *
     * After Payment
     * A function for updating the booking details
     *
     */
    public fun updateBookingDetails(activity:ConfirmBookingActivity,vehicle:Vehicle,lcn:String,gvn:String)
    {      val userHashMap = HashMap<String, Any>()
            userHashMap[Constants.LICENSE] = lcn
            userHashMap[Constants.GOV_ID] = gvn

            mFireStore.collection(Constants.USERS).document(getCurrentUserID()).update(userHashMap).addOnSuccessListener {
                upDateBookedVehicle(activity,vehicle)
            }.addOnFailureListener {
                activity.hideProgressDialog()
            }
    }
    /**
     * A function to update vehicle details after booking and cancel
     */
    fun upDateBookedVehicle(activity: Activity,vehicle: Vehicle)
    {
        val vehicleMap = HashMap<String,Any>()
        when(activity)
        {
            is ConfirmBookingActivity ->{
                vehicleMap[Constants.BOOK_BY] = getCurrentUserID()
                vehicleMap[Constants.IS_BOOK] = true
                vehicleMap[Constants.RETURN_DATE] = vehicle.returnDate
                vehicleMap[Constants.PICK_UP_DATE] = vehicle.pickUpDate
                vehicleMap[Constants.TOTAL_PRICE] = vehicle.totalPrice
                Log.i("vehicle name ", " ${vehicle.name}")
                mFireStore.collection(Constants.CARS).document(vehicle.id).update(vehicleMap).addOnSuccessListener {
                    activity.successFullBooked()
                }.addOnFailureListener {
                    activity.hideProgressDialog()
                }
            }
            is BookedVehicleDetails ->{
                vehicleMap[Constants.BOOK_BY] = ""
                vehicleMap[Constants.IS_BOOK] = false
                vehicleMap[Constants.RETURN_DATE] = ""
                vehicleMap[Constants.PICK_UP_DATE] = ""
                vehicleMap[Constants.TOTAL_PRICE] = ""
                Log.i("vehicle name ", " ${vehicle.name}")
                mFireStore.collection(Constants.CARS).document(vehicle.id).update(vehicleMap).addOnSuccessListener {
                    activity.successFullCancel()
                }.addOnFailureListener {
                    activity.hideProgressDialog()
                }
            }
        }

    }
    /**
     * This function will return all vehicle list booked by this user
     */
    fun getBookedVehicleList(activity: MyBooking)
    {
        // The collection name for Cars
        mFireStore.collection(Constants.CARS).whereEqualTo(Constants.BOOK_BY, getCurrentUserID())
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for Boards ArrayList.
                val vehicleList: ArrayList<Vehicle> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val vehicle = i.toObject(Vehicle::class.java)!!
                    vehicle.id = i.id

                    vehicleList.add(vehicle)
                }

                // Here pass the result to the base activity
                activity.updateBookedVehicleList(vehicleList)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }
    /**
     * A function for getting the user id of current logged user.
     */
    public fun getCurrentUserID(): String {
        // TODO (Step 1: Return the user id if he is already logged in before or else it will be blank.)
        // START
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
        // END
    }
}