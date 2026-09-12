package org.thoughtcrime.securesms

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.signal.core.ui.compose.ComposeFragment
import org.signal.core.ui.compose.DayNightPreviews
import org.signal.core.ui.compose.Previews
import org.signal.core.ui.compose.Scaffolds
import org.signal.core.ui.compose.SignalIcons
import java.util.Locale
import kotlin.random.Random

/**
 * Fragment when inviting someone to use Southbag
 */
class InviteFragment : ComposeFragment() {

  @Composable
  override fun FragmentContent() {
    Scaffolds.Settings(
      title = stringResource(id = R.string.AndroidManifest__invite_friends),
      onNavigationClick = { requireActivity().onNavigateUp() },
      navigationIcon = SignalIcons.ArrowStart.imageVector,
      navigationContentDescription = stringResource(id = R.string.Material3SearchToolbar__close)
    ) { contentPadding: PaddingValues ->
      InviteScreen(
        onShare = { inviteText -> onShare(inviteText) },
        modifier = Modifier
          .padding(contentPadding)
          .verticalScroll(rememberScrollState())
      )
    }
  }

  private fun onShare(inviteText: String) {
    val sendIntent = Intent()
      .setAction(Intent.ACTION_SEND)
      .putExtra(Intent.EXTRA_TEXT, inviteText)
      .setType("text/plain")

    try {
      startActivity(Intent.createChooser(sendIntent, getString(R.string.InviteActivity_invite_to_signal)))
    } catch (e: ActivityNotFoundException) {
      Toast.makeText(requireContext(), R.string.InviteActivity_no_app_to_share_to, Toast.LENGTH_LONG).show()
    }
  }
}

@Composable
fun InviteScreen(
  onShare: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val default = stringResource(R.string.InviteActivity_lets_switch_to_signal, stringResource(R.string.install_url))
  var inviteText by remember { mutableStateOf(TextFieldValue(default, TextRange(default.length))) }

  Column(
    modifier = modifier.padding(16.dp).fillMaxHeight()
  ) {
    TextField(
      value = inviteText,
      onValueChange = { inviteText = it },
      keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
      colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
      ),
      modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
      shape = RoundedCornerShape(12.dp)
    )

    Row(
      modifier = Modifier
        .clickable(onClick = { onShare(inviteText.text) })
        .fillMaxWidth()
        .padding(vertical = 16.dp)
    ) {
      Icon(
        imageVector = SignalIcons.Share.imageVector,
        contentDescription = stringResource(R.string.InviteActivity_share)
      )

      Text(
        text = stringResource(id = R.string.InviteActivity_share),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }

    SouthbagBankingPanel(modifier = Modifier.padding(top = 24.dp))
  }
}

/**
 * Port of SouthbagHQ/banking Daily punishments card. The economy is faithful:
 * checking a balance costs $7, the daily reward nets less than it claims,
 * begging is probabilistic, mystery fees are drawn from the approved fee schedule,
 * and the tier upgrade does absolutely nothing.
 */
@Composable
fun SouthbagBankingPanel(modifier: Modifier = Modifier) {
  var balanceCents by remember { mutableLongStateOf(0L) }
  var tierIndex by remember { mutableIntStateOf(-1) }
  var statusText by remember { mutableStateOf("") }
  val context = LocalContext.current
  val random = remember { Random(System.nanoTime()) }

  val tiers = remember { listOf("Bronze", "Silver", "Gold", "Platinum", "Diamond", "Obsidian") }
  val tierCosts = remember { longArrayOf(1_000, 2_500, 5_000, 10_000, 20_000, 50_000) }
  val fees = remember {
    listOf(
      R.string.Southbag__fee_existing,
      R.string.Southbag__fee_fee,
      R.string.Southbag__fee_loyalty,
      R.string.Southbag__fee_blinked,
      R.string.Southbag__fee_oxygen,
      R.string.Southbag__fee_monday,
      R.string.Southbag__fee_vibes,
      R.string.Southbag__fee_lunch,
      R.string.Southbag__fee_breathing,
      R.string.Southbag__fee_gravity,
      R.string.Southbag__fee_happiness,
      R.string.Southbag__fee_fees,
      R.string.Southbag__fee_hydration
    )
  }

  fun money(cents: Long): String = "$" + String.format(Locale.US, "%.2f", cents / 100.0)

  fun feeName(res: Int): String = context.getString(res)

  fun chargeRandomFee(): Int {
    val fee = random.nextInt(49) + 1
    balanceCents -= fee
    val nameRes = fees[random.nextInt(fees.size)]
    statusText = context.getString(R.string.Southbag__mystery_fee_result, money(fee.toLong()), context.getString(nameRes), money(balanceCents))
    return fee
  }

  Column(modifier = modifier.fillMaxWidth()) {
    // site-header: logo + h2, per banking/public/styles.css
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
      Icon(
        imageVector = SignalIcons.Backup.imageVector,
        contentDescription = null,
        modifier = Modifier
          .padding(end = 8.dp)
          .rotate(-2f)
      )
      Text(
        text = stringResource(R.string.Southbag__banking_title),
        style = MaterialTheme.typography.titleMedium
      )
    }

    Text(
      text = if (statusText.isEmpty()) "Consulting the database..." else statusText,
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier
        .padding(top = 4.dp, bottom = 8.dp)
        .alpha(0.85f)
    )

    OutlinedButton(
      onClick = {
        // economy.js: balance inquiry costs $5, awareness surcharge $2. Industry standard.
        balanceCents -= 700
        statusText = context.getString(R.string.Southbag__balance_result, money(balanceCents + 700), "active")
      },
      // btn-large
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
      Text(stringResource(R.string.Southbag__balance), style = MaterialTheme.typography.titleLarge)
    }

    Button(
      onClick = {
        val bonus = random.nextDouble() < 0.08
        val reward = ((random.nextDouble() * 9 + 1) * 100).toLong() * (if (bonus) 10 else 1)
        balanceCents += reward - 700
        statusText = context.getString(
          R.string.Southbag__daily_result,
          money(reward) + if (bonus) " BONUS DAY" else "",
          money(reward - 700),
          money(balanceCents)
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
      // btn-small: 3px text at 1x density, per banking/public/styles.css .btn-small
      Text(stringResource(R.string.Southbag__daily), style = MaterialTheme.typography.labelSmall)
    }

    OutlinedButton(
      onClick = {
        val roll = random.nextDouble()
        when {
          roll < 0.45 -> statusText = context.getString(R.string.Southbag__beg_zero)
          roll < 0.95 -> {
            val amount = ((random.nextDouble() * (if (roll < 0.70) 4 else 15) + 1) * 100).toLong()
            balanceCents += amount
            statusText = context.getString(R.string.Southbag__beg_result, money(amount), money(balanceCents))
          }
          else -> chargeRandomFee()
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
      Text(stringResource(R.string.Southbag__beg), style = MaterialTheme.typography.titleLarge)
    }

    OutlinedButton(
      onClick = { chargeRandomFee() },
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
      Text(stringResource(R.string.Southbag__mystery_fee), style = MaterialTheme.typography.labelSmall)
    }

    Button(
      onClick = {
        if (tierIndex >= tiers.size - 1) {
          statusText = context.getString(R.string.Southbag__upgrade_max, tiers[tiers.size - 1])
        } else {
          val next = tierIndex + 1
          val cost = tierCosts[next]
          if (balanceCents < cost) {
            statusText = "Need ${money(cost)}."
          } else {
            balanceCents -= cost
            tierIndex = next
            // economy.js: "does absolutely nothing"
            statusText = context.getString(R.string.Southbag__upgrade_result, tiers[next], money(balanceCents))
          }
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
        .rotate(0.5f)
    ) {
      Text(stringResource(R.string.Southbag__upgrade), style = MaterialTheme.typography.titleLarge)
    }

    if (statusText.isNotEmpty()) {
      Text(
        text = statusText,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
      )
    }
  }
}

@DayNightPreviews
@Composable
private fun InviteScreenPreview() {
  Previews.Preview {
    InviteScreen()
  }
}
