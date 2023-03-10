package com.example.libroapi

import android.os.Parcelable
import android.os.Parcel
import androidx.annotation.NonNull
import java.math.BigInteger
import java.util.Date

data class PostModel(
    val bookname: String?,
    val author: String?,
    val year: Int?,
    val price: Int?,
    val quantity: Int?,
    val avil: Boolean?,
    val date: String?,
    val isbn: Long?
    ) : Parcelable {

    companion object CREATOR : Parcelable.Creator<PostModel> {
        override fun createFromParcel(parcel: Parcel): PostModel {
            return PostModel(
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
                parcel.readString(),
                parcel.readValue(Long::class.java.classLoader) as? Long
            )
        }

        override fun newArray(size: Int): Array<PostModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookname)
        parcel.writeString(author)
        parcel.writeValue(year)
        parcel.writeValue(price)
        parcel.writeValue(quantity)
        parcel.writeValue(avil)
        parcel.writeSerializable(date)
        parcel.writeSerializable(isbn)
    }

    override fun describeContents(): Int {
        return 0
    }

}