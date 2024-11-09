    package com.example.mad_assignment_1

    import android.os.Parcel
    import android.os.Parcelable
    import androidx.room.Entity
    import androidx.room.PrimaryKey


    @Entity(tableName = "transaction_table")
    data class Transaction(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val iconResId: Int,
        val title: String,
        val amount: String,
        val date: String,
        val iconColor: Int
    ): Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeInt(iconResId)
            parcel.writeString(title)
            parcel.writeString(amount)
            parcel.writeString(date)
            parcel.writeInt(iconColor)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Transaction> {
            override fun createFromParcel(parcel: Parcel): Transaction {
                return Transaction(parcel)
            }

            override fun newArray(size: Int): Array<Transaction?> {
                return arrayOfNulls(size)
            }
        }
    }