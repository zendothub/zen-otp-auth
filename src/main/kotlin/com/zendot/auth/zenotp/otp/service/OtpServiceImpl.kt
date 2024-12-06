package com.zendot.auth.zenotp.otp.service


import com.otpless.authsdk.OTPAuth
import com.otpless.authsdk.OTPResponse
import com.otpless.authsdk.OTPVerificationResponse
import com.zendot.auth.zenotp.common.exception.ResourceNotFoundException
import com.zendot.auth.zenotp.otp.model.OtpException
import com.zendot.auth.zenotp.otp.model.OtpRequest
import com.zendot.auth.zenotp.otp.repository.OtpRequestRepository
import com.zendot.auth.zenotp.users.UserService
import com.zendot.auth.zenotp.users.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class OtpServiceImpl(
    val userService: UserService,
    val otpRequestRepository: OtpRequestRepository,
    @Value("\${interkashi.otp.oppless.clientid}") otpLessClientId: String,
    @Value("\${interkashi.otp.oppless.clientsecret}") otpLessClientSecret: String,
) : OtpService {

    val otplessSdk = OTPAuth(otpLessClientId, otpLessClientSecret)

    override fun sendOtp(phoneNumber: String, apphash: String) {
        val otpRequestOpt = otpRequestRepository.findByPhoneNumberAndActive(phoneNumber, true)
        if (otpRequestOpt.isEmpty) { // Send OTP for first time
            var otpRequest = OtpRequest(
                null,
                phoneNumber,
                UUID.randomUUID().toString(),
                apphash.ifEmpty { null })
            otpRequest = otpRequestRepository.save(otpRequest)
            val otpResponse: OTPResponse = otplessSdk.sendOTP(
                otpRequest.orderId,
                "91${otpRequest.phoneNumber}",
                null,
                otpRequest.appHash,
                300,
                6,
                "SMS"
            )
            if (otpResponse.isSuccess) {
                println("OTP sent. orderId=> " + otpResponse.orderId)
            } else {
                onOtpRequestFailure(otpRequest, otpResponse)
                println("OTP send to failed due to " + otpResponse.errorMessage)
            }
        } else { // Resend OTP
            val otpRequest = otpRequestOpt.get()
            // Can't request otp more than 3 times with same order-id
            if (otpRequest.count < 3) {
                val otpResponse: OTPResponse = otplessSdk.resendOTP(otpRequest.orderId)
                if (otpResponse.isSuccess) {
                    otpRequest.incrementCount()
                    save(otpRequest)
                    println("OTP sent. orderId=> " + otpResponse.orderId)
                } else {
                    onOtpRequestFailure(otpRequest, otpResponse)
                    println("OTP send to failed due to " + otpResponse.errorMessage)
                }
            } else {
                otpRequest.apply {
                    active = false
                }
                save(otpRequest)
                sendOtp(phoneNumber, apphash)
            }
        }
    }

    private fun onOtpRequestFailure(
        otpRequest: OtpRequest,
        otpResponse: OTPResponse
    ) {
        otpRequest.apply {
            message = otpResponse.errorMessage
            active = false
        }.let { save(it) }
        throw OtpException(otpResponse.errorMessage)
    }

    override fun resendOtp(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override fun verifyOtp(phoneNumber: String, otp: String): User {
        val otpRequestOpt = otpRequestRepository.findByPhoneNumberAndActive(phoneNumber, true)
        if (otpRequestOpt.isPresent) {
            val otpRequest = otpRequestOpt.get()
            val response: OTPVerificationResponse =
                otplessSdk.verifyOTP(otpRequest.orderId, otp, "91$phoneNumber", null)

            if (response.isOTPVerified) {
                otpRequest.apply {
                    active = false
                }.let { save(it) }
                return userService.findByUsernameElseCreate("+91$phoneNumber")

            } else {
                if(otpRequest.verifyCount>2){
                    save(otpRequest.apply { active=false })
                }else{
                    save(otpRequest.apply { verifyCount += 1 })
                }
                println(response.errorMessage)
                throw OtpException(response.errorMessage)
            }
        } else {
            throw ResourceNotFoundException("OTP request not found!")
        }
    }

    override fun save(otpRequest: OtpRequest): OtpRequest = otpRequestRepository.save(otpRequest)
}