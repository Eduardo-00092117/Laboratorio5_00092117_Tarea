package com.example.pokedeskfragment.data

import android.os.Parcel
import android.os.Parcelable

data class pokemonResul(
    var name : String,
    var url : String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        name = parcel.readString(),
        url = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<pokemonResul> {
        override fun createFromParcel(parcel: Parcel): pokemonResul {
            return pokemonResul(parcel)
        }

        override fun newArray(size: Int): Array<pokemonResul?> {
            return arrayOfNulls(size)
        }
    }
}
