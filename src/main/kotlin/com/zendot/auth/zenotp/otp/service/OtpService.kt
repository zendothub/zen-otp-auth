package com.zendot.auth.zenotp.otp.service

import com.zendot.auth.zenotp.otp.model.OtpRequest


import com.zendot.auth.zenotp.users.model.User


interface OtpService {

    fun sendOtp(phoneNumber: String, apphash: String)

    fun resendOtp(phoneNumber: String)

    fun verifyOtp(phoneNumber: String, otp: String): User

    fun save(otpRequest: OtpRequest): OtpRequest
}