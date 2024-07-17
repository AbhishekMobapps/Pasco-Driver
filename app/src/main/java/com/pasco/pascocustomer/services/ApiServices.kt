package com.pasco.pascocustomer.services

import VehicleTypeResponse
import com.example.transportapp.DriverApp.MarkDuty.MarkDutyResponse
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.AddAmountResponse
import com.pasco.pascocustome.Driver.Customer.Fragment.CustomerWallet.GetAmountResponse
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AcceptRideResponse
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AddBiddingBody
import com.pasco.pascocustomer.Driver.AcceptRideDetails.ViewModel.AddBidingResponse
import com.pasco.pascocustomer.Driver.ApprovalStatus.ViewModel.ApprovalStatusResponse
import com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel.CouponResponse
import com.pasco.pascocustomer.Driver.CouponDetails.CouponViewModel.CouponUsedResponse
import com.pasco.pascocustomer.Driver.DriverDashboard.ViewModel.MarkDutyBody

import com.pasco.pascocustomer.Driver.DriverWallet.withdraw.WithdrawAmountBody
import com.pasco.pascocustomer.Driver.DriverWallet.withdraw.WithdrawAmountResponse

import com.pasco.pascocustomer.Driver.DriverWallet.wallethistory.GetAddWalletDataBody

import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyCResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverAllBiddsDetail.ViewModel.GetDriverBidDetailsDataResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.CancelReasonResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.DAllOrderResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverOrders.ViewModel.DriverCancelResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryResponse
import com.pasco.pascocustomer.Driver.Fragment.HomeFrag.ViewModel.ShowBookingReqResponse
import com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.appsurvey.AppSurveyBody
import com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.appsurvey.AppSurveyResponse
import com.pasco.pascocustomer.Driver.NotesRemainders.ViewModel.NotesRResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.AfterStartTripResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.CompleteRideResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.GetRouteUpdateResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.StartTripResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.UpDriverStatusResponse
import com.pasco.pascocustomer.Driver.StartRiding.deliveryproof.DeliveryProofResponse
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdateLocationResponse
import com.pasco.pascocustomer.Driver.UpdateLocation.UpdationLocationBody
import com.pasco.pascocustomer.Driver.customerDetails.CustomerDetailsResponse
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackBody
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.EmergencyHelpDriverResponse
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendEmergercyHelpResponse
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendToAllBody
import com.pasco.pascocustomer.Profile.PutViewModel.ProfileResponse
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginBody
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginResponse
import com.pasco.pascocustomer.commonpage.login.loginotpcheck.OtpCheckResponse
import com.pasco.pascocustomer.commonpage.login.signup.clientmodel.ClientSignUpResponse
import com.pasco.pascocustomer.commonpage.login.signup.clientmodel.ClientSignupBody
import com.pasco.pascocustomer.commonpage.login.signup.model.DriverBody
import com.pasco.pascocustomer.commonpage.login.signup.model.DriverResponse
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.customer.activity.hometabactivity.cargoavailable.CargoAvailableBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.cargoavailable.CargoAvailableResponse
import com.pasco.pascocustomer.customer.activity.hometabactivity.checkservicemodel.CheckChargesBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.checkservicemodel.CheckChargesResponse
import com.pasco.pascocustomer.customer.activity.hometabactivity.modelview.BookingOrderBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.modelview.BookingRideResponse
import com.pasco.pascocustomer.customer.activity.notificaion.delete.DeleteNotificationResponse
import com.pasco.pascocustomer.customer.activity.notificaion.modelview.NotificationResponse
import com.pasco.pascocustomer.userFragment.logoutmodel.LogoutBody
import com.pasco.pascocustomer.userFragment.logoutmodel.LogoutResponse
import com.pasco.pascocustomer.userFragment.order.odermodel.OrderResponse
import com.pasco.pascocustomer.userFragment.profile.updatemodel.UpdateProfileResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.pasco.pascocustomer.activity.Driver.AddVehicle.ApprovalRequest.ApprovalRequestResponse
import com.pasco.pascocustomer.commonpage.login.loginotpcheck.CheckOtpBody
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityBody
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityResponse
import com.pasco.pascocustomer.commonpage.login.signup.checknumber.CheckNumberBody
import com.pasco.pascocustomer.commonpage.login.signup.checknumber.CheckNumberResponse
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.acceptreject.AcceptOrRejectBidBody
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.acceptreject.AcceptOrRejectResponse
import com.pasco.pascocustomer.customer.activity.driverdetails.modelview.DriverDetailsResponse
import com.pasco.pascocustomer.customer.activity.hometabactivity.additionalservice.AdditionalServiceBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.additionalservice.AdditionalServiceResponse
import com.pasco.pascocustomer.customer.activity.language.LanguageResponse
import com.pasco.pascocustomer.customer.activity.notificaion.clearnotification.ClearAllNotificationResponse
import com.pasco.pascocustomer.customer.activity.notificaion.delete.NotificationBody
import com.pasco.pascocustomer.customer.activity.notificaion.notificationcount.NotificationCountResponse
import com.pasco.pascocustomer.customer.activity.track.cancelbooking.CancelBookingBody
import com.pasco.pascocustomer.customer.activity.track.completededuct.CompletedDeductAmountBody
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationBody
import com.pasco.pascocustomer.customer.activity.track.trackmodel.TrackLocationResponse
import com.pasco.pascocustomer.customer.activity.updatevehdetails.GetVDetailsResponse
import com.pasco.pascocustomer.customer.activity.updatevehdetails.GetVehicleDetailsBody
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.adddetailsmodel.ServicesResponse
import com.pasco.pascocustomer.customer.activity.updatevehdetails.PutVDetailsResponse
import com.pasco.pascocustomer.customer.activity.vehicledetailactivity.vehicletype.GetVehicleTypeBody
import com.pasco.pascocustomer.customerfeedback.CustomerFeedbackBody
import com.pasco.pascocustomer.loyalty.model.LoyaltyProgramResponse
import com.pasco.pascocustomer.loyalty.useloyaltycode.LoyaltyCodeUseBody
import com.pasco.pascocustomer.notificationoffon.model.GetOnOffNotificationBody
import com.pasco.pascocustomer.notificationoffon.model.NotificationOnOffBody
import com.pasco.pascocustomer.notificationoffon.model.NotificationOnOffResponse
import com.pasco.pascocustomer.reminder.ReminderResponse
import com.pasco.pascocustomer.userFragment.history.complete.CompleteHistoryResponse
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeBody
import com.pasco.pascocustomer.userFragment.home.sliderpage.SliderHomeResponse
import com.pasco.pascocustomer.userFragment.order.odermodel.CustomerOrderBody
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileBody
import com.pasco.pascocustomer.userFragment.profile.modelview.GetProfileResponse
import retrofit2.http.*

//comment
interface ApiServices {
    @Headers("Accept:application/json")
    @POST("user-registration/")
    fun driverRegister(
        @Body body: DriverBody
    ): Observable<DriverResponse>

    @Headers("Accept:application/json")
    @POST("client-registration/")
    fun userRegister(
        @Body body: ClientSignupBody
    ): Observable<ClientSignUpResponse>

    @Headers("Accept:application/json")
    @POST("addcity/")
    fun getCityList(
        @Body body: UpdateCityBody
    ): Observable<UpdateCityResponse>

    @Headers("Accept:application/json")
    @POST("check-login/")
    fun otpCheck(
        @Body body: CheckOtpBody
    ): Observable<OtpCheckResponse>

    @Headers("Accept:application/json")
    @POST("check-number/")
    fun checkNumber(
        @Body body: CheckNumberBody
    ): Observable<CheckNumberResponse>

    @Headers("Accept:application/json")
    @POST("user-login/")
    fun userLogin(
        @Body body: LoginBody
    ): Observable<LoginResponse>

    //services List
    @POST("service-lists/")
    fun getServicesList(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: GetVehicleTypeBody
    ): Observable<ServicesResponse>


    //vehicle Type
    @FormUrlEncoded
    @POST("cargo-lists/")
    fun getCargoList(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("shipment_type") shipment_type: String,
        @Field("language") language: String
    ): Observable<VehicleTypeResponse>

    //approval request Done -
    @Multipart
    @Headers("Accept:application/json")
    @POST("approvalrequest/")
    fun getApprovalRequest(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Part("cargo") cargo: RequestBody,
        @Part("vehiclenumber") vehiclenumber: RequestBody,
        @Part attachment: MultipartBody.Part,
        @Part attachment1: MultipartBody.Part,
        @Part attachment2: MultipartBody.Part,
        @Part("language") language: RequestBody,
    ): Observable<ApprovalRequestResponse>

    //get Approve requests
    @POST("updateapprovalstatus/")
    fun getUpdateVehDetails(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : GetVehicleDetailsBody
    ): Observable<GetVDetailsResponse>

    //put approve Requests
    @Multipart
    @Headers("Accept:application/json")
    @PUT("updateapprovalstatus/")
    fun putVehicleUpdate(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Part("cargo") cargo: RequestBody,
        @Part("vehiclenumber") vehiclenumber: RequestBody,
        @Part("language") language: RequestBody,
        @Part attachmentP: MultipartBody.Part,
        @Part attachmentD: MultipartBody.Part,
        @Part attachmentDl: MultipartBody.Part
    ): Observable<PutVDetailsResponse>

    @Headers("Accept:application/json")
    @POST("ride-booking/")
    fun bookRideServices(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: BookingOrderBody
    ): Observable<BookingRideResponse>


    @Headers("Accept:application/json")
    @POST("checkcharges/")
    fun getCharges(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: CheckChargesBody
    ): Observable<CheckChargesResponse>

    @Headers("Accept:application/json")
    @POST("checkavailability/")
    fun cargoAvailable(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: CargoAvailableBody
    ): Observable<CargoAvailableResponse>

    @Headers("Accept:application/json")
    @POST("user-logout/")
    fun logOut(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: LogoutBody
    ): Observable<LogoutResponse>

    @POST("request-send/")
    fun getOrder(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<OrderResponse>

    @POST("afterbidlist/")
    fun getAllBids(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<OrderResponse>

    @POST("bookingbiddetail/{id}")
    fun getBiddsDetails(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") Id: String,
        @Body body : CustomerOrderBody
    ): Observable<AllBiddsDetailResponse>

    @POST("acceptedbooking/")
    fun acceptedBids(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<AllBiddsDetailResponse>


    @Multipart
    @Headers("Accept:application/json")
    @PUT("user-update-profile/")
    fun updateProfile(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Part("full_name") full_name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("current_city") current_city: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("language") language: RequestBody
    ): Observable<UpdateProfileResponse>

    @POST("ShowNotification/")
    fun getNotification(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<NotificationResponse>

    @Headers("Accept:application/json")
    @POST("NotificationDelete/")
    fun deleteNotifications(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: NotificationBody
    ): Observable<DeleteNotificationResponse>

    @POST("ClearAllNotification/")
    fun getClearAllNotification(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<ClearAllNotificationResponse>


    //approval status
    @POST("approvalstatus/")
    fun getApprovedData(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<ApprovalStatusResponse>

    //duty On
    @PUT("marked-duty-status/")
    fun putMarkDuty(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: MarkDutyBody
    ): Observable<MarkDutyResponse>

    //showBooking request data
    @FormUrlEncoded
    @POST("showbookingriderequests/")
    fun getBookingReq(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("city") city: String,
        @Field("language") language: String
    ): Observable<ShowBookingReqResponse>

    //get update BID details DONE Language
    @POST("updatebookingbid/{id}/")
    fun getBidDetails(
        @Path("id") id: String,
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<AcceptRideResponse>

    //get update and Add BID data
    @PUT("updatebookingbid/{id}/")
    fun addBidDetails(
        @Path("id") id: String,
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body addBidDetails: AddBiddingBody
    ): Observable<AddBidingResponse>

    //get all orders
    @POST("bookingdriverbiddetail/")
    fun getAllOrderDriver(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<DAllOrderResponse>

    @GET("cancelreason/")
    fun CancelReason(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<CancelReasonResponse>

    @FormUrlEncoded
    @POST("UpdateCancelDriverBooking/{id}/")
    fun cancelledData(
        @Path("id") id: String,
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("reasonid") reasonid: String,
        @Field("language") language: String
    ): Observable<DriverCancelResponse>


    @POST("bookdriver-ongoing/")
    fun bookingDriverOnGoing(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<DAllOrderResponse>

    //all bids details
    @POST("bookingdriverdata/{bookingId}/")
    fun bookingDriverData(
        @Path("bookingId") bookingId: String,
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<GetDriverBidDetailsDataResponse>


    //driver status list
    @POST("driver_status_list/")
    fun getDriverStatus(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<GetRouteUpdateResponse>

    //update driver status
    @FormUrlEncoded
    @POST("update-driver-status/{id}/")
    fun getUpdateDriverStatus(
        @Path("id") id: String,
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("driver_status") driver_status: String
    ): Observable<UpDriverStatusResponse>

    //start trip driver
    @FormUrlEncoded
    @POST("start_trip/{id}/")
    fun startTrip(
        @Path("id") id: String,
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("driver_status") driver_status: String,
        @Field("language") language: String,
    ): Observable<StartTripResponse>

    //get profile
    @POST("user-update-profile/")
    fun getUserProfile(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: GetProfileBody
    ): Observable<GetProfileResponse>

    @POST("DriverCompleteBooking/{id}/")
    fun getCompletedRide(
        @Path("id") id: String,
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: GetProfileBody
    ): Observable<CompleteRideResponse>

    @Multipart
    @Headers("Accept:application/json")
    @POST("addddeliveryproof/")
    fun addDeliveryProof(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Part("booking_confimation") booking_confimation: RequestBody,
        @Part("driverID") driverID: RequestBody,
        @Part delivery_image: MultipartBody.Part
    ): Observable<DeliveryProofResponse>

    @FormUrlEncoded
    @POST("verifydeliveryproof/")
    fun verifyDeliveryCode(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("bookingid") bookingid: String,
        @Field("delivery_code") delivery_code: String,
        @Field("language") language: String
    ): Observable<DeliveryProofResponse>


    //put profile
    @Multipart
    @PUT("user-update-profile/")
    fun putProfile(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Part("full_name") full_name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("language") language: RequestBody,
        @Part Attachment: MultipartBody.Part
    ): Observable<ProfileResponse>


    //CouponList
    @POST("coupon-detail/")
    fun getCouponList(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: GetProfileBody
    ): Observable<CouponResponse>

    //coupon used Api
    @FormUrlEncoded
    @POST("add-couponused/")
    fun getCouponUsed(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("coupon_used") coupon_used: String
    ): Observable<CouponUsedResponse>

    //EmergencyDriverList
    @FormUrlEncoded
    @POST("NearbyVehicle/")
    fun getDriverEHelp(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("driver_latitude") driver_latitude: String,
        @Field("driver_longitude") driver_longitude: String,
        @Field("language") language: String
    ): Observable<EmergencyHelpDriverResponse>

    @FormUrlEncoded
    @POST("send-emergency-help/{id}/")
    fun sendEmergencyHelp(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") Id: String,
        @Field("driver_id") driver_id: String,
        @Field("current_location") current_location: String,
        @Field("reason") reason: String,
        @Field("language") language: String
    ): Observable<SendEmergercyHelpResponse>

    @Headers("Accept: application/json")
    @POST("sendemergencyhelptoall/{id}/")
    fun sendToAllDriver(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") Id: String,
        @Body body: SendToAllBody
    ): Observable<SendEmergercyHelpResponse>

    //EmergencyList
    @POST("show-emergencydetail/")
    fun getEmergencyList(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: GetProfileBody
    ): Observable<EmergencyCResponse>


    @GET("UnreadNotification/")
    fun getCountNotification(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
    ): Observable<NotificationCountResponse>

    //addNotesReminder
    @FormUrlEncoded
    @POST("add-reminder/")
    fun addNotesReminder(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("reminderdate") reminderdate: String
    ): Observable<NotesRResponse>

    //update driver location
    @Headers("Accept: application/json")
    @POST("location-update/")
    fun updateGeolocation(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: UpdationLocationBody
    ): Observable<UpdateLocationResponse>

    //user Api


    //add wallet Money
    @FormUrlEncoded
    @POST("addUserWallet/")
    fun addUserWallet(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Field("amount") amount: String,
        @Field("language") language: String
    ): Observable<AddAmountResponse>

    //get User wallet balance
    @POST("userwallet-detail/")
    fun getUserWallet(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: GetAddWalletDataBody
    ): Observable<GetAmountResponse>

    //withdraw amount
    @Headers("Accept:application/json")
    @POST("withdraw-wallet/")
    fun withdrawAmount(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: WithdrawAmountBody
    ): Observable<WithdrawAmountResponse>


    //get  additional Service
    @POST("additional-service/")
    fun additionalService(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : AdditionalServiceBody
    ): Observable<AdditionalServiceResponse>

    @POST("DriverbookedDetail/{id}")
    fun driverDetails(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") Id: String,
        @Body body : CustomerOrderBody
    ): Observable<DriverDetailsResponse>

    @Headers("Accept:application/json")
    @POST("assign_Booking/{id}/")
    fun acceptOrReject(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") Id: String,
        @Body body: AcceptOrRejectBidBody
    ): Observable<AcceptOrRejectResponse>

    @POST("ClientbookedDetail/{id}")
    fun customerDetails(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") Id: String,
        @Body body : CustomerOrderBody
    ): Observable<CustomerDetailsResponse>


    @Headers("Accept: application/json")
    @POST("slideshow-detail/")
    fun sliderHome(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: SliderHomeBody
    ): Observable<SliderHomeResponse>


    @Headers("Accept: application/json")
    @POST("show-location/")
    fun trackLocation(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: TrackLocationBody
    ): Observable<TrackLocationResponse>

    //driver completedHistory
    @GET("bookdriver-completed/")
    fun driverCompletedHistory(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken
    ): Observable<CompletedTripHistoryResponse>

    @POST("bookdriver-cancelled/")
    fun driverCancelledHistory(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: CustomerOrderBody
    ): Observable<CancelledTripResponse>

    @POST("afterstarttrip/{bookingId}")
    fun afterStartTrip(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("bookingId") bookingId: String,
        @Body body : CustomerOrderBody
    ): Observable<AfterStartTripResponse>


    @POST("bookclient-cancelled/")
    fun customerCanceldHistory(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<CompleteHistoryResponse>

    @POST("bookclient-completed/")
    fun customerCompletedHistory(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body : CustomerOrderBody
    ): Observable<CompleteHistoryResponse>

    @Headers("Accept:application/json")
    @POST("DriverCancelledBooking/{id}/")
    fun cancelBooking(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") Id: String,
        @Body body: CancelBookingBody
    ): Observable<AcceptOrRejectResponse>

    @GET("add-reminder/")
    fun getReminder(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken
    ): Observable<ReminderResponse>

    @Headers("Accept:application/json")
    @POST("userfeedback/")
    fun feedback(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: CustomerFeedbackBody
    ): Observable<AcceptOrRejectResponse>

    @Headers("Accept:application/json")
    @POST("Driverfeedback/")
    fun driverFeedback(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: DriverFeedbackBody
    ): Observable<AcceptOrRejectResponse>

    @Headers("Accept:application/json")
    @POST("add-app-survey/")
    fun appSurvery(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: AppSurveyBody
    ): Observable<AppSurveyResponse>

    @POST("loyaltyprogram-detail/")
    fun getLoyaltyProgram(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: CustomerOrderBody
    ): Observable<LoyaltyProgramResponse>


    @Headers("Accept:application/json")
    @POST("add-loyaltyprogramused/")
    fun loyaltyCodeUse(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: LoyaltyCodeUseBody
    ): Observable<AcceptOrRejectResponse>

    @GET("delete-reminder/{id}/")
    fun reminderDelete(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") bookingId: String

    ): Observable<AcceptOrRejectResponse>


    @Headers("Accept:application/json")
    @POST("set-typeof-notification/")
    fun allTypeNOtification(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: NotificationOnOffBody
    ): Observable<NotificationOnOffResponse>

    @POST("set-typeof-notification/")
    fun getAllTypeNotification(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Body body: GetOnOffNotificationBody
    ): Observable<NotificationOnOffResponse>

    @Headers("Accept: application/json")
    @POST("UpdateCompleteClientWallet/{id}/")
    fun deductAmount(
        @Header("Authorization") token: String = PascoApp.encryptedPrefs.bearerToken,
        @Path("id") Id: String,
        @Body body: CompletedDeductAmountBody
    ): Observable<SendEmergercyHelpResponse>


    @GET("show-language-list/")
    fun languageType(
    ): Observable<LanguageResponse>
}
