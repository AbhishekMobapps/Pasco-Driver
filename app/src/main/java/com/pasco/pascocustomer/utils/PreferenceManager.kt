package  com.pasco.pascocustomer.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class PreferenceManager(context: Context) {

    private var mPrefs: PreferenceManager? = null

/*

    private val masterKeyAlias: String = MasterKey.DEFAULT_MASTER_KEY_ALIAS
    private val masterKey: MasterKey =
        MasterKey.Builder(context, masterKeyAlias).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()


    private val prefs = EncryptedSharedPreferences.create(
        context, "myPrefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
*/

    private var masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private var prefs = EncryptedSharedPreferences.create(
        context,
        "PromotrEncryptedPr",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val editor = prefs.edit()

    fun getInstance(context: Context): PreferenceManager {
        if (mPrefs == null) {
            synchronized(PreferenceManager::class.java) {
                if (mPrefs == null) mPrefs = PreferenceManager(context)
            }
        }
        return mPrefs!!
    }

    // region "Getters & Setters"
    var isFirstTime: Boolean
        get() = prefs.getBoolean(IS_FIRST_TIME, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME, isFirstTime)
            editor.apply()
        }

    var appLanguage: String
        get() = prefs.getString(USER_LANG, "") ?: ""
        set(appLanguage) {
            editor.putString(USER_LANG, appLanguage)
            editor.apply()
        }


    var token: String
        get() = prefs.getString(RefreshToken, "") ?: ""
        set(userToken) {
            editor.putString(RefreshToken, userToken)
            editor.apply()
        }

    var bearerToken: String
        get() = prefs.getString(BEARER_USER_TOKEN, "") ?: ""
        set(userToken) {
            editor.putString(BEARER_USER_TOKEN, userToken)
            editor.apply()
        }
    var approvedId: String
        get() = prefs.getString(APPROVEDID, "") ?: ""
        set(approvedId) {
            editor.putString(APPROVEDID, approvedId)
            editor.apply()
        }

    var driverApprovedId: String
        get() = prefs.getString(DRIVERAPPROVEDID, "") ?: ""
        set(driverApprovedId) {
            editor.putString(DRIVERAPPROVEDID, driverApprovedId)
            editor.apply()
        }

    var driverStatus: String
        get() = prefs.getString(DRIVERSTATUS, "") ?: ""
        set(driverStatus) {
            editor.putString(DRIVERSTATUS, driverStatus)
            editor.apply()
        }

    var userId: String
        get() = prefs.getString(USER_ID, "") ?: ""
        set(userId) {
            editor.putString(USER_ID, userId)
            editor.apply()
        }

    var CheckedType: String
        get() = prefs.getString(CHECKED_TYPE, "") ?: ""
        set(checkedType) {
            editor.putString(CHECKED_TYPE, checkedType)
            editor.apply()
        }

    var driverStatuss: String
        get() = prefs.getString(DRIVERSTATUSS, "") ?: ""
        set(driverStatuss) {
            editor.putString(DRIVERSTATUSS, driverStatuss)
            editor.apply()
        }

    var FCMToken: String
        get() = prefs.getString(FCM_TOKEN, "") ?: ""
        set(userToken) {
            editor.putString(FCM_TOKEN, userToken)
            editor.apply()
        }

    var isNotification: Boolean
        get() = prefs.getBoolean(IS_NOTIFICATION, true)
        set(isNotification) {
            editor.putBoolean(IS_NOTIFICATION, isNotification)
            editor.apply()
        }


    var userType: String
        get() = prefs.getString(UserType, "") ?: ""
        set(userType) {
            editor.putString(UserType, userType)
            editor.apply()
        }

    var profileUpdate: String
        get() = prefs.getString(Profile, "") ?: ""
        set(profile) {
            editor.putString(Profile, profile)
            editor.apply()
        }
    var countryCode: String
        get() = prefs.getString(Code, "") ?: ""
        set(code) {
            editor.putString(Code, code)
            editor.apply()
        }



    var requestOrderId: String
        get() = prefs.getString(ORDERID, "") ?: ""
        set(requestOrderId) {
            editor.putString(ORDERID, requestOrderId)
            editor.apply()
        }

    var bidId: String
        get() = prefs.getString(BidId, "") ?: ""
        set(bidsId) {
            editor.putString(BidId, bidsId)
            editor.apply()
        }
    var userName: String
        get() = prefs.getString(userNames, "") ?: ""
        set(name) {
            editor.putString(userNames, name)
            editor.apply()
        }

    var orderId: String
        get() = prefs.getString(orderIds, "") ?: ""
        set(id) {
            editor.putString(orderIds, id)
            editor.apply()
        }

    var dateTime: String
        get() = prefs.getString(dateTimes, "") ?: ""
        set(date) {
            editor.putString(dateTimes, date)
            editor.apply()
        }

    var pickupLocation: String
        get() = prefs.getString(pickupLocations, "") ?: ""
        set(pick) {
            editor.putString(pickupLocations, pick)
            editor.apply()
        }

    var dropLocation: String
        get() = prefs.getString(dropLocations, "") ?: ""
        set(drop) {
            editor.putString(dropLocations, drop)
            editor.apply()
        }


    var distance: String
        get() = prefs.getString(distances, "") ?: ""
        set(distanc) {
            editor.putString(distances, distanc)
            editor.apply()
        }

    var totalPrice: String
        get() = prefs.getString(totalPrices, "") ?: ""
        set(totalPric) {
            editor.putString(totalPrices, totalPric)
            editor.apply()
        }

    var approvalStatus: String
        get() = prefs.getString(ApprovalStatus, "") ?: ""
        set(status) {
            editor.putString(ApprovalStatus, status)
            editor.apply()
        }

    var drPickupLatitude: String
        get() = prefs.getString(DrPickupLatitude, "") ?: ""
        set(drPickupLatitude) {
            editor.putString(DrPickupLatitude, drPickupLatitude)
            editor.apply()
        }


    var drPickupLongitude: String
        get() = prefs.getString(DrPickupLongitude, "") ?: ""
        set(drLong) {
            editor.putString(DrPickupLongitude, drLong)
            editor.apply()
        }

    var drDropLatitude: String
        get() = prefs.getString(DrDroplatitude, "") ?: ""
        set(drPickupL) {
            editor.putString(DrDroplatitude, drPickupL)
            editor.apply()
        }

    var drDropLongitude: String
        get() = prefs.getString(DrDropLongitude, "") ?: ""
        set(drPickupLp) {
            editor.putString(DrDropLongitude, drPickupLp)
            editor.apply()
        }

    companion object {
        // region "Tags"
        private const val IS_FIRST_TIME = "isFirstTime"
        private const val DRIVERSTATUSS = "DRIVERSTATUSS"
        private const val DrPickupLatitude = "DrPickupLatitude"
        private const val DrPickupLongitude = "DrPickupLongitude"
        private const val DrDroplatitude = "drDroplatitude"
        private const val DrDropLongitude = "drDropLongitude"

        private const val RefreshToken = "RefreshToken"
        private const val Profile = "Profile"

        private const val BEARER_USER_TOKEN = "BEARER_USER_TOKEN"
        private const val UserType = "UserType"
        private const val DRIVERAPPROVEDID = "DRIVERAPPROVEDID"


        private const val USER_ID = "USER_ID"
        private const val BidId = "BidId"
        private const val userNames = "userNames"
        private const val orderIds = "orderIds"
        private const val dateTimes = "dateTimes"
        private const val pickupLocations = "pickupLocations"
        private const val dropLocations = "dropLocations"
        private const val distances = "distances"
        private const val totalPrices = "totalPrices"
        private const val ApprovalStatus = "ApprovalStatus"
        private const val Code = "Code"
        private const val UpdateNotification = true
        private const val USER_LANG = "USER_LANG"
        private const val FCM_TOKEN = "FCM_TOKEN"
        private const val APPROVEDID = "APPROVEDID"
        private const val IS_NOTIFICATION = "IS_NOTIFICATION"
        private const val CHECKED_TYPE = "CHECKED_TYPE"
        private const val DRIVERSTATUS = "DRIVERSTATUS"
        private const val ORDERID = "ORDERID"
    }

}