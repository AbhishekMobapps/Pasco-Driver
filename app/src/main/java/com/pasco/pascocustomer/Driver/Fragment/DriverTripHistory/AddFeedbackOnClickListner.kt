package com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory

import com.google.android.material.bottomsheet.BottomSheetDialog

interface AddFeedbackOnClickListner {
    fun addFeedbackItemClick(
        position: Int,
        id: Int,
        comment: String,
        ratingBars: String,
        bottomSheetDialog: BottomSheetDialog
    )
}