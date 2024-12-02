package `in`.zendot.auth.zenotp.users.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class User(
    var phoneNo: String,
    @JsonIgnore var passwordHash: String?,
    var authoritiess: List<String> = emptyList(),
) : UserDetails {

    @JsonIgnore
    override fun getUsername(): String {
        return phoneNo
    }

    @JsonIgnore
    override fun getPassword(): String? {
        return passwordHash
    }

    @JsonIgnore
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authoritiess.map { SimpleGrantedAuthority(it.toString()) }
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }
}
