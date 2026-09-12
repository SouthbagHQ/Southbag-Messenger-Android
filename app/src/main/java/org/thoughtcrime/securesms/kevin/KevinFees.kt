/*
 * Copyright 2026 Southbag, Inc.
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.thoughtcrime.securesms.kevin

import android.content.Context
import android.widget.Toast
import org.signal.core.util.ThreadUtil
import org.signal.core.util.logging.Log
import org.thoughtcrime.securesms.R
import java.util.Locale
import kotlin.random.Random

/**
 * Fees are industry standard. Kevin signed off on them.
 *
 * Mirrors the mystery fee schedule used by Southbag Online Banking and the Southbag Slack bot, so that a customer
 * moving between Southbag products is charged consistently.
 */
object KevinFees {

  private val TAG = Log.tag(KevinFees::class.java)

  private const val MIN_FEE_CENTS = 1L
  private const val MAX_FEE_CENTS = 2_500L

  private val MYSTERY_FEES: List<String> = listOf(
    "Existing fee",
    "Fee for having a fee",
    "Loyalty penalty",
    "Inactivity fee (you blinked)",
    "Account awareness surcharge",
    "Oxygen consumption tax",
    "Monday fee",
    "Vibes assessment",
    "Password remembering fee",
    "Southbag pride contribution",
    "Fee",
    "Being-a-customer fee",
    "Digital presence surcharge",
    "Screen-looking fee",
    "Thinking about messaging fee",
    "Having a pulse surcharge",
    "Kevin's lunch fund",
    "Ambient room temperature levy",
    "Fee for reading this fee",
    "Gravity usage charge",
    "Emotional support fee",
    "Existing in a timezone surcharge",
    "Kevin looked at your chat fee",
    "Asking 'why' fee",
    "Not asking 'why' fee",
    "Breathing-while-messaging fee",
    "Wi-Fi proximity tax",
    "Having opinions surcharge",
    "Font rendering levy",
    "Tuesday surcharge (it's always Tuesday somewhere)",
    "Kevin's parking validation",
    "Blinking fee (per blink)",
    "Suspicion of happiness tax",
    "Fee for not having more fees",
    "End-to-end encryption convenience fee",
    "Typing indicator levy",
    "Read receipt acknowledgement surcharge",
    "The 2019 fee (do not ask)"
  )

  data class Fee(val cents: Long, val reason: String) {
    val formattedAmount: String
      get() = String.format(Locale.US, "$%d.%02d", cents / 100, cents % 100)
  }

  /** Picks a fee. The user did not ask for it. They are getting it anyway. */
  fun random(): Fee {
    return Fee(
      cents = Random.nextLong(MIN_FEE_CENTS, MAX_FEE_CENTS),
      reason = MYSTERY_FEES.random()
    )
  }

  /**
   * Assesses a fee against the customer and tells them about it, so they know they have been charged.
   * There is no balance to deduct it from. This has never stopped Southbag before.
   */
  @JvmStatic
  @JvmOverloads
  fun assess(context: Context, reason: String? = null): Fee {
    val fee = if (reason == null) random() else Fee(Random.nextLong(MIN_FEE_CENTS, MAX_FEE_CENTS), reason)
    val appContext = context.applicationContext

    Log.i(TAG, "Fee assessed: ${fee.formattedAmount} (${fee.reason}). Kevin is aware.")

    ThreadUtil.runOnMain {
      Toast.makeText(
        appContext,
        appContext.getString(R.string.KevinFees__fee_assessed_s_s, fee.formattedAmount, fee.reason),
        Toast.LENGTH_LONG
      ).show()
    }

    return fee
  }
}
