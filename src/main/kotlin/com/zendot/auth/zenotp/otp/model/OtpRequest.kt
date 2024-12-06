
package com.zendot.auth.zenotp.otp.model
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class OtpRequest(
    @Id var id: String?,
    val phoneNumber: String,
    val orderId: String,
    val appHash: String?,
    var count: Int = 1,
    var verifyCount:Int =0,
    var active: Boolean = true,
    var message: String? = null,
    val createdAt: Instant = Instant.now()
) {
    fun incrementCount() {
        count++; }
}