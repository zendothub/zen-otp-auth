package `in`.zendot.auth.zenotp.users.repository

import `in`.zendot.auth.zenotp.users.model.User
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByPhoneNo(userName: String): Optional<User>
}

