package com.daniel.appbase.components

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Java email validation program
 *
 * @author pankaj
 */
class EmailValidator {

    // non-static Matcher object because it's created from the input String
    private var matcher: Matcher? = null

    init {
        // initialize the Pattern object
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE)
    }

    /**
     * This method validates the input email address with EMAIL_REGEX pattern
     *
     * @param email
     * @return boolean
     */
    fun validateEmail(email: String): Boolean {
        matcher = pattern.matcher(email)
        return matcher!!.matches()
    }

    companion object {
        // Email Regex java
        const val EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$"

        // static Pattern object, since pattern is fixed
        private lateinit var pattern: Pattern
    }
}