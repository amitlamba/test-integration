package com.und.repository

import com.und.model.jpa.ContactUs
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ContactUsRepository : JpaRepository<ContactUs, Long> {

    fun findByEmail(email: String): Optional<ContactUs>


}