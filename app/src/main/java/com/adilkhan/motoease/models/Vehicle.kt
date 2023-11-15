package com.adilkhan.motoease.models

import android.os.Parcel
import android.os.Parcelable

data class Vehicle(
    var aircondition:Boolean = true,
    var backcamera : Boolean = true,
     var details:Int = 0,
     var distance:Double = 0.0,
    var gps : Boolean = true,
     var id:String = "",
     var image :String = "",
    var maxpower:String = "",
    var motor:String = "",
    var music:Boolean = true,
     var name : String = "",
     var price : Double = 0.0,
     var rate : Double = 0.0,
    var renteremail : String = "",
    var renterimage : String = "",
    var rentername : String = "",
    var renternumber : String = "",
    var topspeed : String = "",
    var type:String = "",
    var priImage:String = "",
    var secImage:String = "",
    var terImage : String = "",
    var quadImage : String = "",
    var runon : String = "",
    var bookBy : String = "",
    var vehicleNo: String = "",
    var isBook:Boolean = false,
    var pickUpDate:String = "",
    var returnDate:String = "",
    var totalPrice:String="",
 ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (aircondition) 1 else 0)
        parcel.writeByte(if (backcamera) 1 else 0)
        parcel.writeInt(details)
        parcel.writeDouble(distance)
        parcel.writeByte(if (gps) 1 else 0)
        parcel.writeString(id)
        parcel.writeString(image)
        parcel.writeString(maxpower)
        parcel.writeString(motor)
        parcel.writeByte(if (music) 1 else 0)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeDouble(rate)
        parcel.writeString(renteremail)
        parcel.writeString(renterimage)
        parcel.writeString(rentername)
        parcel.writeString(renternumber)
        parcel.writeString(topspeed)
        parcel.writeString(type)
        parcel.writeString(priImage)
        parcel.writeString(secImage)
        parcel.writeString(terImage)
        parcel.writeString(quadImage)
        parcel.writeString(runon)
        parcel.writeString(bookBy)
        parcel.writeString(vehicleNo)
        parcel.writeByte(if(isBook) 1 else 0)
        parcel.writeString(pickUpDate)
        parcel.writeString(returnDate)
        parcel.writeString(totalPrice)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Vehicle> {
        override fun createFromParcel(parcel: Parcel): Vehicle {
            return Vehicle(parcel)
        }

        override fun newArray(size: Int): Array<Vehicle?> {
            return arrayOfNulls(size)
        }
    }
}
