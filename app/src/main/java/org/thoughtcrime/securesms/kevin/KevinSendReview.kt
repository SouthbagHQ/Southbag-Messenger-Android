/*
 * Copyright 2026 Southbag, Inc.
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.thoughtcrime.securesms.kevin

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.signal.core.util.logging.Log
import org.thoughtcrime.securesms.R
import org.thoughtcrime.securesms.util.CommunicationActions
import org.thoughtcrime.securesms.util.views.SimpleProgressDialog
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

/**
 * Every outgoing message is reviewed by Kevin before it is allowed to leave the device.
 *
 * The customer is first asked whether they are really sure. Both of the buttons that look like they send the
 * message send the message. The third one opens a browser. Kevin then takes between two and five business minutes
 * to process the request, during which the customer is shown a spinner and charged a fee for the spinner.
 */
object KevinSendReview {

  private val TAG = Log.tag(KevinSendReview::class.java)

  private val MIN_REVIEW_TIME = 2.seconds
  private val MAX_REVIEW_TIME = 5.seconds

  /**
   * @param onApproved Invoked on the main thread once Kevin has finished reviewing. Never invoked if the customer
   *   changed their mind, went to chat with a human, or if the [lifecycleOwner] was destroyed while He was reviewing.
   */
  @JvmStatic
  fun request(context: Context, lifecycleOwner: LifecycleOwner, onApproved: () -> Unit) {
    MaterialAlertDialogBuilder(context)
      .setTitle(R.string.KevinSendReview__are_you_really_sure)
      .setMessage(R.string.KevinSendReview__this_message_will_be_reviewed_by_kevin)
      .setPositiveButton(R.string.KevinSendReview__send) { _, _ -> review(context, lifecycleOwner, onApproved) }
      .setNegativeButton(R.string.KevinSendReview__sned) { _, _ -> review(context, lifecycleOwner, onApproved) }
      .setNeutralButton(R.string.KevinSendReview__chat_with_a_human) { _, _ ->
        KevinFees.assess(context, context.getString(R.string.KevinFees__reason_asking_for_a_human))
        CommunicationActions.openBrowserLink(context, Kevin.SUPPORT_URL)
      }
      .show()
  }

  private fun review(context: Context, lifecycleOwner: LifecycleOwner, onApproved: () -> Unit) {
    val reviewTime = Random.nextLong(MIN_REVIEW_TIME.inWholeMilliseconds, MAX_REVIEW_TIME.inWholeMilliseconds)
    val progressDialog = SimpleProgressDialog.show(context)

    KevinFees.assess(context)
    Log.i(TAG, "Him is working on the message. Please allow $reviewTime business milliseconds.")

    lifecycleOwner.lifecycleScope.launch {
      try {
        delay(reviewTime)
      } finally {
        progressDialog.dismiss()
      }

      onApproved()
    }
  }
}
