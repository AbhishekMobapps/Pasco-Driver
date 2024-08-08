package com.pasco.pascocustomer.repository

import VehicleTypeResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CancelledTripResponse
import com.pasco.pascocustomer.Driver.Fragment.DriverTripHistory.CompletedTripHistoryResponse
import com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.appsurvey.AppSurveyBody
import com.pasco.pascocustomer.Driver.Fragment.MoreFragDriver.appsurvey.AppSurveyResponse
import com.pasco.pascocustomer.Driver.StartRiding.ViewModel.AfterStartTripResponse
import com.pasco.pascocustomer.Driver.StartRiding.deliveryproof.DeliveryProofResponse
import com.pasco.pascocustomer.Driver.customerDetails.CustomerDetailsResponse
import com.pasco.pascocustomer.Driver.driverFeedback.DriverFeedbackBody
import com.pasco.pascocustomer.Driver.emergencyhelp.ViewModel.SendEmergercyHelpResponse
import com.pasco.pascocustomer.application.PascoApp
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginBody
import com.pasco.pascocustomer.commonpage.login.loginmodel.LoginResponse
import com.pasco.pascocustomer.commonpage.login.loginotpcheck.OtpCheckResponse
import com.pasco.pascocustomer.commonpage.login.signup.clientmodel.ClientSignUpResponse
import com.pasco.pascocustomer.commonpage.login.signup.clientmodel.ClientSignupBody
import com.pasco.pascocustomer.customer.activity.allbiddsdetailsactivity.model.AllBiddsDetailResponse
import com.pasco.pascocustomer.customer.activity.hometabactivity.cargoavailable.CargoAvailableBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.cargoavailable.CargoAvailableResponse
import com.pasco.pascocustomer.customer.activity.hometabactivity.checkservicemodel.CheckChargesBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.checkservicemodel.CheckChargesResponse
import com.pasco.pascocustomer.customer.activity.hometabactivity.modelview.BookingOrderBody
import com.pasco.pascocustomer.customer.activity.hometabactivity.modelview.BookingRideResponse
import com.pasco.pascocustomer.customer.activity.notificaion.delete.DeleteNotificationResponse
import com.pasco.pascocustomer.customer.activity.notificaion.modelview.NotificationResponse
import com.pasco.pascocustomer.services.ApiServices
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
import com.pasco.pascocustomer.notificationoffon.model.GetNotificationOFF
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
import retrofit2.http.Body
import javax.inject.Inject

class CommonRepository @Inject constructor(private val apiService: ApiServices) {
    fun getClientSign(courseBody: ClientSignupBody): Observable<ClientSignUpResponse> {
        return apiService.userRegister(courseBody)
    }

    fun getCityList(cityBody: UpdateCityBody): Observable<UpdateCityResponse> {
        return apiService.getCityList(cityBody)
    }

    fun getOtpCheck(courseBody: CheckOtpBody): Observable<OtpCheckResponse> {
        return apiService.otpCheck(courseBody)
    }

    fun checkNumber(courseBody: CheckNumberBody): Observable<CheckNumberResponse> {
        return apiService.checkNumber(courseBody)
    }

    fun getLogin(courseBody: LoginBody): Observable<LoginResponse> {
        return apiService.userLogin(courseBody)
    }

    fun getServicesRepo(body: GetVehicleTypeBody): Observable<ServicesResponse> {
        return apiService.getServicesList(PascoApp.encryptedPrefs.bearerToken,body)
    }

    fun getVehicleType(shipment_type: String, language: String): Observable<VehicleTypeResponse> {
        return apiService.getCargoList(PascoApp.encryptedPrefs.bearerToken, shipment_type, language)
    }

    fun getApprovalReqRepo(
        cargo: RequestBody,
        vehiclenumber: RequestBody,
        identify_document: MultipartBody.Part,
        identify_document1: MultipartBody.Part,
        identify_document2: MultipartBody.Part,
        language: RequestBody,

        ): Observable<ApprovalRequestResponse> {
        return apiService.getApprovalRequest(
            PascoApp.encryptedPrefs.bearerToken,
            cargo,
            vehiclenumber,
            identify_document,
            identify_document1,
            identify_document2,
            language
        )
    }

    fun getUpdateVDetailRepo(body: GetVehicleDetailsBody): Observable<GetVDetailsResponse> {
        return apiService.getUpdateVehDetails(PascoApp.encryptedPrefs.bearerToken, body)
    }


    fun putApprovalReqRepo(
        cargo: RequestBody,
        vehiclenumber: RequestBody,
        language: RequestBody,
        attachmentP: MultipartBody.Part,
        attachmentD: MultipartBody.Part,
        attachmentDl: MultipartBody.Part

    ): Observable<PutVDetailsResponse> {
        return apiService.putVehicleUpdate(
            PascoApp.encryptedPrefs.bearerToken,
            cargo,
            vehiclenumber,
            language,
            attachmentP,
            attachmentD,
            attachmentDl
        )
    }

    fun bookingOrder(courseBody: BookingOrderBody): Observable<BookingRideResponse> {
        return apiService.bookRideServices(PascoApp.encryptedPrefs.bearerToken, courseBody)
    }

    fun checkCharge(courseBody: CheckChargesBody): Observable<CheckChargesResponse> {
        return apiService.getCharges(PascoApp.encryptedPrefs.bearerToken, courseBody)
    }

    fun cargoAvailable(courseBody: CargoAvailableBody): Observable<CargoAvailableResponse> {
        return apiService.cargoAvailable(PascoApp.encryptedPrefs.bearerToken, courseBody)
    }

    fun logOut(courseBody: LogoutBody): Observable<LogoutResponse> {
        return apiService.logOut(PascoApp.encryptedPrefs.bearerToken, courseBody)
    }

    fun getOrder(body: CustomerOrderBody): Observable<OrderResponse> {
        return apiService.getOrder(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getAllBidds(body: CustomerOrderBody): Observable<OrderResponse> {
        return apiService.getAllBids(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getAllBiddsDetails(
        Id: String,
        body: CustomerOrderBody
    ): Observable<AllBiddsDetailResponse> {
        return apiService.getBiddsDetails(PascoApp.encryptedPrefs.bearerToken, Id, body)
    }

    fun getAcceptedBids(body: CustomerOrderBody): Observable<AllBiddsDetailResponse> {
        return apiService.acceptedBids(PascoApp.encryptedPrefs.bearerToken, body)
    }


    fun updateProfile(
        full_name: RequestBody,
        email: RequestBody,
        current_city: RequestBody,
        image: MultipartBody.Part,
        language: RequestBody,
    ): Observable<UpdateProfileResponse> {
        return apiService.updateProfile(
            PascoApp.encryptedPrefs.bearerToken, full_name, email, current_city, image, language
        )
    }

    fun uploadDeliveryProofRepo(
        booking_confimation: RequestBody, driverID: RequestBody, delivery_image: MultipartBody.Part
    ): Observable<DeliveryProofResponse> {
        return apiService.addDeliveryProof(
            PascoApp.encryptedPrefs.bearerToken, booking_confimation, driverID, delivery_image
        )
    }

    fun verifyDeliveryProof(
        bookingid: String,
        delivery_code: String,
        language: String
    ): Observable<DeliveryProofResponse> {
        return apiService.verifyDeliveryCode(
            PascoApp.encryptedPrefs.bearerToken,
            bookingid,
            delivery_code,
            language
        )
    }

    fun getUserNotification(body: CustomerOrderBody): Observable<NotificationResponse> {
        return apiService.getNotification(PascoApp.encryptedPrefs.bearerToken, body)
    }


    fun getDeleteNotification(courseBody: NotificationBody): Observable<DeleteNotificationResponse> {
        return apiService.deleteNotifications(PascoApp.encryptedPrefs.bearerToken, courseBody)
    }

    fun clearAllNotification(body: CustomerOrderBody): Observable<ClearAllNotificationResponse> {
        return apiService.getClearAllNotification(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getCountNotifications( body: GetProfileBody): Observable<NotificationCountResponse> {
        return apiService.getCountNotification(PascoApp.encryptedPrefs.bearerToken,body)
    }

    fun getProfile(body: GetProfileBody): Observable<GetProfileResponse> {
        return apiService.getUserProfile(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getAdditionalService(body: AdditionalServiceBody): Observable<AdditionalServiceResponse> {
        return apiService.additionalService(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun driverDetails(Id: String, body: CustomerOrderBody): Observable<DriverDetailsResponse> {
        return apiService.driverDetails(PascoApp.encryptedPrefs.bearerToken, Id, body)
    }

    fun afterTripDetails(
        bookingId: String,
        body: CustomerOrderBody
    ): Observable<AfterStartTripResponse> {
        return apiService.afterStartTrip(PascoApp.encryptedPrefs.bearerToken, bookingId, body)
    }


    fun acceptReject(
        courseBody: AcceptOrRejectBidBody, id: String
    ): Observable<AcceptOrRejectResponse> {
        return apiService.acceptOrReject(PascoApp.encryptedPrefs.bearerToken, id, courseBody)
    }

    fun customerDetails(Id: String, body: CustomerOrderBody): Observable<CustomerDetailsResponse> {
        return apiService.customerDetails(PascoApp.encryptedPrefs.bearerToken, Id, body)
    }

    fun sliderHome(body: SliderHomeBody): Observable<SliderHomeResponse> {
        return apiService.sliderHome(PascoApp.encryptedPrefs.bearerToken, body)
    }


    fun trackLocation(body: TrackLocationBody): Observable<TrackLocationResponse> {
        return apiService.trackLocation(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getDriverCHistory( body : CustomerOrderBody): Observable<CompletedTripHistoryResponse> {
        return apiService.driverCompletedHistory(PascoApp.encryptedPrefs.bearerToken,body)
    }

    fun getDriverCancelledHistory(body: CustomerOrderBody): Observable<CancelledTripResponse> {
        return apiService.driverCancelledHistory(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getCustomerCancelledHistory(body: CustomerOrderBody): Observable<CompleteHistoryResponse> {
        return apiService.customerCanceldHistory(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getCustomerCompletedHistory(body: CustomerOrderBody): Observable<CompleteHistoryResponse> {
        return apiService.customerCompletedHistory(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun cancelBooking(
        courseBody: CancelBookingBody, id: String
    ): Observable<AcceptOrRejectResponse> {
        return apiService.cancelBooking(PascoApp.encryptedPrefs.bearerToken, id, courseBody)
    }

    fun getReminderAlert(  body : CustomerOrderBody): Observable<ReminderResponse> {
        return apiService.getReminder(PascoApp.encryptedPrefs.bearerToken,body)
    }

    fun feedback(
        courseBody: CustomerFeedbackBody
    ): Observable<AcceptOrRejectResponse> {
        return apiService.feedback(PascoApp.encryptedPrefs.bearerToken, courseBody)
    }

    fun feedbackDriver(
        courseBody: DriverFeedbackBody
    ): Observable<AcceptOrRejectResponse> {
        return apiService.driverFeedback(PascoApp.encryptedPrefs.bearerToken, courseBody)
    }

    fun appSurvey(
        body: AppSurveyBody
    ): Observable<AppSurveyResponse> {
        return apiService.appSurvery(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getLoyalty(body: CustomerOrderBody): Observable<LoyaltyProgramResponse> {
        return apiService.getLoyaltyProgram(PascoApp.encryptedPrefs.bearerToken,body)
    }

    fun loyaltyCodeUse(body: LoyaltyCodeUseBody): Observable<AcceptOrRejectResponse> {
        return apiService.loyaltyCodeUse(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getReminderDelete(Id: String,body: CustomerOrderBody): Observable<AcceptOrRejectResponse> {
        return apiService.reminderDelete(PascoApp.encryptedPrefs.bearerToken, Id,body)
    }

    fun notificationOnOff(body: NotificationOnOffBody): Observable<NotificationOnOffResponse> {
        return apiService.allTypeNOtification(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun getNotificationOnOff(body: GetOnOffNotificationBody): Observable<GetNotificationOFF> {
        return apiService.getAllTypeNotification(PascoApp.encryptedPrefs.bearerToken, body)
    }

    fun deductAmount(
        courseBody: CompletedDeductAmountBody, id: String
    ): Observable<SendEmergercyHelpResponse> {
        return apiService.deductAmount(PascoApp.encryptedPrefs.bearerToken, id, courseBody)
    }


    fun getLanguage(): Observable<LanguageResponse> {
        return apiService.languageType()
    }

    fun receiveAmountPending(Id: String,body: CustomerOrderBody): Observable<AcceptOrRejectResponse> {
        return apiService.receiveAmount(PascoApp.encryptedPrefs.bearerToken, Id,body)
    }

    fun updateLanguage(body: CustomerOrderBody): Observable<AcceptOrRejectResponse> {
        return apiService.updateLanguage(PascoApp.encryptedPrefs.bearerToken,body)
    }

    fun rejectBids(Id: String,body: CustomerOrderBody): Observable<AcceptOrRejectResponse> {
        return apiService.rejectBids(PascoApp.encryptedPrefs.bearerToken, Id,body)
    }
}
