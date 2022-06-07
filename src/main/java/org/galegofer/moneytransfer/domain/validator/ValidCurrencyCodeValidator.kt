package org.galegofer.moneytransfer.domain.validator

import org.springframework.util.ObjectUtils
import java.util.*
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ValidCurrencyCodeValidator : ConstraintValidator<ValidCurrencyCode, String> {
    private var isOptional: Boolean = false
    override fun initialize(validCurrencyCode: ValidCurrencyCode) {
        isOptional = validCurrencyCode.optional
    }

    override fun isValid(value: String, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return try {
            val containsIsoCode = Currency.getAvailableCurrencies()
                .contains(Currency.getInstance(value))
            if (isOptional) containsIsoCode || !ObjectUtils.isEmpty(value) else containsIsoCode
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
