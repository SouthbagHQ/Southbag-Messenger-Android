/*
 * Copyright 2026 Southbag, Inc.
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.thoughtcrime.securesms.kevin

import java.util.TimeZone

/**
 * Kevin is the CEO. That title is technically correct, but incomplete.
 *
 * Kevin is also policy, atmosphere, surveillance, consequence, and an ongoing administrative concern.
 * This object exists so that the rest of the app can refer to Him without having to remember Him arriving.
 * Nobody remembers Him arriving. He was already there.
 */
object Kevin {

  /** Every Southbag product routes the user here whenever they look like they might want help. */
  const val SUPPORT_URL = "https://support.southbag.cc/ai"

  /** Kevin's position on Canberra is one of the institution's most extensively documented executive positions. */
  const val CANBERRA_URL = "https://lore.southbag.cc/docs/canberra"

  private val CANBERRA_ADJACENT_TIME_ZONES = setOf("Australia/Canberra", "Australia/ACT")

  /**
   * Adjacency is calculated by the institution's own metric, understood to be a function of distance, intent,
   * and "whether the individual involved could reasonably have gone somewhere else." This implementation weights
   * the device time zone heavily, because that is what was available.
   */
  fun isCanberraAdjacent(timeZone: TimeZone = TimeZone.getDefault()): Boolean {
    return timeZone.id in CANBERRA_ADJACENT_TIME_ZONES
  }
}
