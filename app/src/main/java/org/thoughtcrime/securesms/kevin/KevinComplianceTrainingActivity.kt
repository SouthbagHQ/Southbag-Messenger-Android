/*
 * Copyright 2026 Southbag, Inc.
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.thoughtcrime.securesms.kevin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.signal.core.ui.compose.Buttons
import org.signal.core.ui.compose.DayNightPreviews
import org.signal.core.ui.compose.Dialogs
import org.signal.core.ui.compose.Previews
import org.signal.core.ui.compose.theme.SignalTheme
import org.signal.core.util.logging.Log
import org.thoughtcrime.securesms.PassphraseRequiredActivity
import org.thoughtcrime.securesms.R
import org.thoughtcrime.securesms.keyvalue.SignalStore
import org.thoughtcrime.securesms.util.CommunicationActions
import kotlin.random.Random

/**
 * Learn with Southbag.
 *
 * Before a customer is permitted to see their chats, they complete mandatory compliance training, exactly as
 * customers of Southbag Online Banking must before they are permitted to see their money. The training consists
 * of two modules and two quizzes. A wrong answer sends the customer back to the start of the relevant module and
 * assesses a fee. Leaving assesses a fee and does not leave.
 */
class KevinComplianceTrainingActivity : PassphraseRequiredActivity() {

  companion object {
    private val TAG = Log.tag(KevinComplianceTrainingActivity::class.java)

    @JvmStatic
    fun createIntent(context: Context): Intent {
      return Intent(context, KevinComplianceTrainingActivity::class.java)
    }

    /** Training is required until it has been completed. It cannot be skipped. Kevin checked. */
    @JvmStatic
    fun isRequired(): Boolean {
      return !SignalStore.uiHints.hasCompletedKevinComplianceTraining()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?, ready: Boolean) {
    setContent {
      SignalTheme {
        KevinComplianceTrainingScreen(
          onComplete = {
            Log.i(TAG, "Compliance training complete. Kevin has been informed. He already knew.")
            SignalStore.uiHints.markKevinComplianceTrainingComplete()
            finish()
          }
        )
      }
    }
  }
}

private sealed interface TrainingStep {
  data class Module(@StringRes val title: Int, @StringRes val body: Int) : TrainingStep

  data class Quiz(
    @StringRes val question: Int,
    val options: List<Int>,
    val correctIndex: Int,
    /** Index into [TRAINING_STEPS] to return to when the customer gets it wrong. */
    val restartAt: Int
  ) : TrainingStep

  data object Complete : TrainingStep
}

private val TRAINING_STEPS: List<TrainingStep> = listOf(
  TrainingStep.Module(
    title = R.string.KevinComplianceTraining__module_1_title,
    body = R.string.KevinComplianceTraining__module_1_body
  ),
  TrainingStep.Quiz(
    question = R.string.KevinComplianceTraining__quiz_1_question,
    options = listOf(
      R.string.KevinComplianceTraining__quiz_1_option_nobody,
      R.string.KevinComplianceTraining__quiz_1_option_kevin,
      R.string.KevinComplianceTraining__quiz_1_option_the_pile,
      R.string.KevinComplianceTraining__quiz_1_option_a_friend
    ),
    correctIndex = 1,
    restartAt = 0
  ),
  TrainingStep.Module(
    title = R.string.KevinComplianceTraining__module_2_title,
    body = R.string.KevinComplianceTraining__module_2_body
  ),
  TrainingStep.Quiz(
    question = R.string.KevinComplianceTraining__quiz_2_question,
    options = listOf(
      R.string.KevinComplianceTraining__quiz_2_option_true,
      R.string.KevinComplianceTraining__quiz_2_option_false,
      R.string.KevinComplianceTraining__quiz_2_option_do_not_discuss_it,
      R.string.KevinComplianceTraining__quiz_2_option_ask_kevin
    ),
    correctIndex = 2,
    restartAt = 2
  ),
  TrainingStep.Complete
)

private const val MIN_LOADING_MILLIS = 2_000L
private const val MAX_LOADING_MILLIS = 5_000L

/** Continue is the only button that makes progress. It is also the only button you cannot read. */
private val CONTINUE_BUTTON_FONT_SIZE = 3.sp

@Composable
private fun KevinComplianceTrainingScreen(
  onComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var stepIndex by rememberSaveable { mutableIntStateOf(0) }
  var selectedOption by rememberSaveable(stepIndex) { mutableIntStateOf(-1) }
  var loading by rememberSaveable(stepIndex) { mutableStateOf(true) }

  val step = TRAINING_STEPS[stepIndex]

  LaunchedEffect(stepIndex) {
    loading = true
    delay(Random.nextLong(MIN_LOADING_MILLIS, MAX_LOADING_MILLIS))
    loading = false
  }

  BackHandler {
    KevinFees.assess(context, context.getString(R.string.KevinFees__reason_attempting_to_leave_training))
  }

  Scaffold(modifier = modifier) { paddingValues ->
    if (loading) {
      Dialogs.IndeterminateProgressDialog(message = stringResource(R.string.KevinComplianceTraining__please_allow_2_5_business_minutes))
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
      Text(
        text = stringResource(R.string.KevinComplianceTraining__learn_with_southbag),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      when (step) {
        is TrainingStep.Module -> ModuleContent(step)
        is TrainingStep.Quiz -> QuizContent(
          step = step,
          selectedOption = selectedOption,
          onOptionSelected = { selectedOption = it }
        )
        TrainingStep.Complete -> CompleteContent()
      }

      Spacer(modifier = Modifier.height(24.dp))

      SupportButtons(
        onContinueClick = {
          when (step) {
            is TrainingStep.Module -> stepIndex += 1
            is TrainingStep.Quiz -> {
              when {
                selectedOption < 0 -> KevinFees.assess(context, context.getString(R.string.KevinFees__reason_not_answering))
                selectedOption == step.correctIndex -> stepIndex += 1
                else -> {
                  KevinFees.assess(context, context.getString(R.string.KevinFees__reason_wrong_answer))
                  stepIndex = step.restartAt
                }
              }
            }
            TrainingStep.Complete -> onComplete()
          }
        }
      )

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = stringResource(R.string.KevinComplianceTraining__footer),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
private fun ModuleContent(step: TrainingStep.Module, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    Text(
      text = stringResource(step.title),
      style = MaterialTheme.typography.headlineMedium
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = stringResource(step.body),
      style = MaterialTheme.typography.bodyLarge
    )
  }
}

@Composable
private fun QuizContent(
  step: TrainingStep.Quiz,
  selectedOption: Int,
  onOptionSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Text(
      text = stringResource(step.question),
      style = MaterialTheme.typography.headlineMedium
    )

    Spacer(modifier = Modifier.height(16.dp))

    step.options.forEachIndexed { index, option ->
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOptionSelected(index) }
          .padding(vertical = 4.dp)
      ) {
        RadioButton(
          selected = selectedOption == index,
          onClick = { onOptionSelected(index) }
        )
        Text(
          text = stringResource(option),
          style = MaterialTheme.typography.bodyLarge
        )
      }
    }
  }
}

@Composable
private fun CompleteContent(modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    Text(
      text = stringResource(R.string.KevinComplianceTraining__modules_complete),
      style = MaterialTheme.typography.headlineMedium
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = stringResource(R.string.KevinComplianceTraining__modules_complete_body),
      style = MaterialTheme.typography.bodyLarge
    )
  }
}

/**
 * One tiny button that does what the customer wants, surrounded by large buttons that do not.
 * This layout is shared with the Southbag Online Banking portal, where it has tested extremely badly.
 */
@Composable
private fun SupportButtons(onContinueClick: () -> Unit, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    val context = LocalContext.current
    val openSupport: () -> Unit = { CommunicationActions.openBrowserLink(context, Kevin.SUPPORT_URL) }

    Buttons.LargePrimary(onClick = openSupport, modifier = Modifier.fillMaxWidth()) {
      Text(text = stringResource(R.string.KevinSendReview__chat_with_a_human))
    }

    Spacer(modifier = Modifier.height(8.dp))

    Buttons.LargeTonal(onClick = openSupport, modifier = Modifier.fillMaxWidth()) {
      Text(text = stringResource(R.string.KevinComplianceTraining__need_help))
    }

    TextButton(onClick = onContinueClick) {
      Text(
        text = stringResource(R.string.KevinComplianceTraining__continue),
        fontSize = CONTINUE_BUTTON_FONT_SIZE,
        lineHeight = CONTINUE_BUTTON_FONT_SIZE
      )
    }

    Buttons.LargeTonal(onClick = openSupport, modifier = Modifier.fillMaxWidth()) {
      Text(text = stringResource(R.string.KevinComplianceTraining__talk_to_support))
    }

    Spacer(modifier = Modifier.height(8.dp))

    Buttons.LargePrimary(onClick = openSupport, modifier = Modifier.fillMaxWidth()) {
      Text(text = stringResource(R.string.KevinComplianceTraining__speak_to_human))
    }
  }
}

@DayNightPreviews
@Composable
private fun KevinComplianceTrainingScreenPreview() {
  Previews.Preview {
    KevinComplianceTrainingScreen(onComplete = {})
  }
}
