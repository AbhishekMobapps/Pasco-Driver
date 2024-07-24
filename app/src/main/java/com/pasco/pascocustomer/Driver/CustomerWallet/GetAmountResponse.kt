package com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class GetAmountResponse : Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: GetAmountResponseData? = null

    inner class GetAmountResponseData : Serializable {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("user")
        @Expose
        var user: String? = null

        @SerializedName("wallet_amount")
        @Expose
        var walletAmount: Double? = null

        @SerializedName("transactions")
        @Expose
        var transactions: List<Transaction>? = null
    }

    inner class Transaction : Serializable {

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("amount")
        @Expose
        var amount: Double? = null

        @SerializedName("transaction_type")
        @Expose
        var transactionType: String? = null

        @SerializedName("orderid")
        @Expose
        var orderid: Int? = null

        @SerializedName("pickup_location")
        @Expose
        var pickupLocation: String? = null

        @SerializedName("drop_location")
        @Expose
        var dropLocation: String? = null

        @SerializedName("payment_status")
        @Expose
        var paymentStatus: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null
    }

}