package `in`.zendot.auth.zenotp.common.exception

import java.io.Serial


class ResourceNotFoundException(errorMessage: String?) : RuntimeException(errorMessage) {
    companion object {
        @Serial
        private val serialVersionUID = -2947210857950100650L
    }
}