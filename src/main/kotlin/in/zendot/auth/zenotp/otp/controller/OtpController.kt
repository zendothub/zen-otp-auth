package `in`.zendot.auth.zenotp.otp.controller


import `in`.zendot.auth.zenotp.JwtTokenUtils
import `in`.zendot.auth.zenotp.otp.service.OtpService
import `in`.zendot.auth.zenotp.users.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RequestMapping("/api/auth")
@RestController
class OtpController(val otpService: OtpService, val jwtUtils: JwtTokenUtils) {

    @PostMapping("/sendOtp")
    fun sendOtp(
        @RequestParam phoneNumber: String,
        @RequestParam(required = false, defaultValue = "") apphash: String
    ): ResponseEntity<String> {
        try {
            otpService.sendOtp(phoneNumber, apphash)
            return ResponseEntity.status(200).body("Code Sent")
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PostMapping("/verifyOtp")
    fun verifyOtp(
        @RequestParam phoneNumber: String,
        @RequestParam otp: String
    ): ResponseEntity<User> {
        try {
            val user = otpService.verifyOtp(phoneNumber, otp)
            val jwtToken = jwtUtils.generateToken(user)
            val refreshToken = jwtUtils.generateRefreshToken(user)
            return ResponseEntity
                .status(200)
                .header("Authorization", jwtToken)
                .header("RefreshToken", refreshToken)
                .body(user)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to verify OTP!")
        }
    }

}