package com.axel.mba.bpvpn.feature.obfuscation.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.feature.obfuscation.model.ObfuscationLevel

/**
 * Clean Liquid Glass Dialog for configuring Obfuscation Level.
 * Created by: Axel & M.B.A
 */
class ObfuscationSettingsDialog(
    context: Context,
    private val currentLevel: ObfuscationLevel,
    private val onLevelSelected: (ObfuscationLevel) -> Unit
) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_obfuscation_settings)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val rg: RadioGroup = findViewById(R.id.rgObfuscation)
        val rbDisabled: RadioButton = findViewById(R.id.rbObfDisabled)
        val rbLowPadding: RadioButton = findViewById(R.id.rbObfLowPadding)
        val rbHeader: RadioButton = findViewById(R.id.rbObfHeader)
        val rbStealth: RadioButton = findViewById(R.id.rbObfStealth)
        val btnSave: View = findViewById(R.id.btnSaveObfuscation)

        when (currentLevel) {
            ObfuscationLevel.DISABLED -> rbDisabled.isChecked = true
            ObfuscationLevel.LOW_PADDING -> rbLowPadding.isChecked = true
            ObfuscationLevel.JUNK_HEADER -> rbHeader.isChecked = true
            ObfuscationLevel.FULL_STEALTH -> rbStealth.isChecked = true
        }

        btnSave.setOnClickListener {
            val selected = when (rg.checkedRadioButtonId) {
                R.id.rbObfLowPadding -> ObfuscationLevel.LOW_PADDING
                R.id.rbObfHeader -> ObfuscationLevel.JUNK_HEADER
                R.id.rbObfStealth -> ObfuscationLevel.FULL_STEALTH
                else -> ObfuscationLevel.DISABLED
            }
            onLevelSelected(selected)
            dismiss()
        }
    }
}
