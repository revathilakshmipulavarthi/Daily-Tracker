package com.example

import com.example.logic.SmsParser
import com.example.model.UPIApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsParserTest {

    @Test
    fun testParseHdfcDebitSms() {
        val sms = "Sent Rs.450.00 to swiggy@icici on 28-10-26 Ref 234567. Avail Bal: Rs 15,200.50"
        val result = SmsParser.parse(sms)

        assertTrue(result.isValidDebit)
        assertEquals(450.0, result.amount ?: 0.0, 0.01)
        assertTrue(result.merchant?.contains("swiggy", ignoreCase = true) == true)
        assertFalse(result.isCreditIgnored)
    }

    @Test
    fun testParsePhonePeSms() {
        val sms = "Dear SBI UPI User, A/c ..5678 debited by 320.00 on 28-10-26 transfer to Chai Point via PhonePe (UPI Ref 12345)"
        val result = SmsParser.parse(sms)

        assertTrue(result.isValidDebit)
        assertEquals(320.0, result.amount ?: 0.0, 0.01)
        assertEquals(UPIApp.PHONEPE, result.inferredUpiApp)
    }

    @Test
    fun testRejectCreditSms() {
        val creditSms = "INR 500.00 credited to your A/C XX1234 on 28-Oct-26 by UPI. Avail Bal INR 12,000"
        val result = SmsParser.parse(creditSms)

        assertFalse(result.isValidDebit)
        assertTrue(result.isCreditIgnored)
    }

    @Test
    fun testRejectRefundSms() {
        val refundSms = "Refund of Rs 250.00 received from Amazon Pay into your account"
        val result = SmsParser.parse(refundSms)

        assertFalse(result.isValidDebit)
        assertTrue(result.isCreditIgnored)
    }
}
