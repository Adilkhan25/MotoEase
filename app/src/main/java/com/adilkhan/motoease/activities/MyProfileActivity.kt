package com.adilkhan.motoease.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.adilkhan.motoease.R
import com.adilkhan.motoease.databinding.ActivityMyProfileBinding
import com.adilkhan.motoease.firebase.FireStoreClass
import com.adilkhan.motoease.models.User
import com.adilkhan.motoease.utils.Constants
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    lateinit var bindingProfileActivity: ActivityMyProfileBinding
    // TODO (Step 10: Add a global variable for URI of a selected image from phone storage.)
    // START
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null
    // END
    // TODO (Step 6: Add the global variables for UserDetails and Profile Image URL.)
    // START
    // A global variable for user details.
    private lateinit var mUserDetails: User

    // A global variable for a user profile image URL
    private var mProfileImageURL: String = ""
    // END
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingProfileActivity = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(bindingProfileActivity.root)
        setupActionBar()
        FireStoreClass().loadUserData(this@MyProfileActivity)
        // TODO (Step 4: Add a click event for iv_profile_user_image.)
        // START
        bindingProfileActivity.ivProfileUserImage.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // TODO (Step 8: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        // END
        // START
        bindingProfileActivity.btnUpdate.setOnClickListener {

            // Here if the image is not selected then update the other details of user.
            if (mSelectedImageFileUri != null) {

                uploadUserImage()
            } else {

                showProgressDialog(resources.getString(R.string.please_wait))

                // Call a function to update user details in the database.
                updateUserProfileData()
            }
        }
        // END
    }
    // TODO (Step 11: Get the result of the image selection based on the constant code.)
    // START
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this@MyProfileActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(bindingProfileActivity.ivProfileUserImage) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    // END

    // TODO (Step 5: Check the result of runtime permission after the user allows or deny based on the unique code.)
    // START
    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO (Step 9: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    // END

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(bindingProfileActivity.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_from_my_profile)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        bindingProfileActivity.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }
    /**
     * A function to set the existing details in UI.
     */
    fun setUserDataInUI(user: User) {
        // TODO (Step 7: Initialize the user details variable)
        // START
        // Initialize the user details variable
        mUserDetails = user
        // END

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(bindingProfileActivity.ivProfileUserImage)

        bindingProfileActivity.etName.setText(user.name)
        bindingProfileActivity.etEmail.setText(user.email)
        if (user.mobile != 0L) {
            bindingProfileActivity.etMobile.setText(user.mobile.toString())
        }
    }
    // TODO (Step 7: Create a function for image selection from phone storage.)
    // START
    /**
     * A function for user profile image selection from phone storage.
     */
    private fun showImageChooser() {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    // END
    // START
    // Before start with database we need to perform some steps in Firebase Console and after adding a dependency in Gradle file.
    // Follow the Steps:
    // Step 1: Go to the "Storage" tab in the Firebase Console in your project details in the navigation bar under "Develop".
    // Step 2: In the Storage Page click on the Get Started. Click on Next
    // Step 3: As we have already selected the storage location while creating the database so now click the Done button.
    // Step 4: Now the storage bucket is created.
    // Step 5: For more details visit the link: https://firebase.google.com/docs/storage/android/start
    // Step 6: Now add the code to upload image.
    /**
     * A function to upload the selected user image to firebase cloud storage.
     */
    private fun uploadUserImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                    mSelectedImageFileUri
                )
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // Call a function to update user details in the database.
                            updateUserProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@MyProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }
    //END
    // START
    /**
     * A function to update the user profile details into the database.
     */
    private fun updateUserProfileData() {

        val userHashMap = HashMap<String, Any>()

            if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
                userHashMap[Constants.IMAGE] = mProfileImageURL
            }

            if (bindingProfileActivity.etName.text.toString() != mUserDetails.name) {
                userHashMap[Constants.NAME] = bindingProfileActivity.etName.text.toString()
            }

            if (bindingProfileActivity.etMobile.text.toString() != mUserDetails.mobile.toString()) {
                userHashMap[Constants.MOBILE] =
                    bindingProfileActivity.etMobile.text.toString().toLong()

            }

        // Update the data in the database.
        FireStoreClass().updateUserProfileData(this@MyProfileActivity, userHashMap)

    }
    // END
    // START
    /**
     * A function to get the extension of selected image.
     */
    private fun getFileExtension(uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    // END

    // START
    /**
     * A function to notify the user profile is updated successfully.
     */
    fun profileUpdateSuccess() {

        hideProgressDialog()
        // TODO (Step 3: Send the success result to the Base Activity.)
        // START
        setResult(Activity.RESULT_OK)
        // END
        finish()
    }
    // END
    // TODO (Step 3: Create a companion object and add a constant for Read Storage runtime permission.)
    // START
    /**
     * A companion object to declare the constants.
     */
    companion object {
        //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
        private const val READ_STORAGE_PERMISSION_CODE = 1

        // TODO (Step 6: Add a constant for image selection from phone storage)
        // START
        private const val PICK_IMAGE_REQUEST_CODE = 2
        // END
    }
    // END
}