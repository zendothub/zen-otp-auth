package `in`.zendot.auth.zenotp.users

import `in`.zendot.auth.zenotp.users.model.User
import `in`.zendot.auth.zenotp.users.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserService(
    val userRepository: UserRepository,
    @Value("\${default.password}") private val defaultPassword: String,

    ) : UserDetailsService {
    private val logger: Logger = LoggerFactory.getLogger(UserDetailsService::class.java)


    fun getUserById(id: String): User {
        return userRepository.findById(id).orElseThrow {
            NoSuchElementException("User not found with id: $id")
        }
    }



    fun findByUsernameElseCreate(username: String): User {
        try {
            // Retrieve user details by username
            val userDetails = loadUserByUsername(username)
            return userDetails
        } catch (ex: UsernameNotFoundException) {
            val encodedPassword = defaultPassword
            // Assign the authority "USER" to the user
            val authorities = mutableListOf(SimpleGrantedAuthority("USER"))
            var savedUser = userRepository.save(
                User(
                    phoneNo = username,
                    passwordHash = encodedPassword,
                    authoritiess = listOf("USER")
                )
            )
        return userRepository.save(savedUser)
        }
    }

    override fun loadUserByUsername(username: String?): User {
        if (username == null) {
            throw UsernameNotFoundException("Username is null")
        }

        val userObj: Optional<User> = userRepository.findByUserName(username)
        val authUser: User = userObj.orElseThrow {
            UsernameNotFoundException("User not found with username: $username")
        }

        return authUser
    }



}
