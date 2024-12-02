package `in`.zendot.auth.zenotp.otp.service

import `in`.zendot.auth.zenotp.otp.model.OtpRequest


import `in`.zendot.auth.zenotp.users.model.User


interface OtpService {

    fun sendOtp(phoneNumber: String, apphash: String)

    fun resendOtp(phoneNumber: String)

    fun verifyOtp(phoneNumber: String, otp: String): User

    fun save(otpRequest: OtpRequest): OtpRequest
}