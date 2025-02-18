package com.leishmaniapp.presentation.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leishmaniapp.R
import com.leishmaniapp.domain.calibration.ImageCalibrationData
import com.leishmaniapp.presentation.ui.theme.LeishmaniappTheme
import com.leishmaniapp.utilities.mock.MockGenerator.mock

/**
 * Show the [ImageCalibrationData] properties
 */
@Composable
fun CameraCalibrationCard(
    modifier: Modifier = Modifier,
    calibrationData: ImageCalibrationData,
) {
    Card(modifier = modifier) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(
                    text = stringResource(
                        id = R.string.calibration_properties_hue,
                        calibrationData.hsv.third.hue
                    )
                )
                Text(
                    text = stringResource(
                        id = R.string.calibration_properties_saturation,
                        calibrationData.hsv.third.saturation
                    )
                )
                Text(
                    text = stringResource(
                        id = R.string.calibration_properties_luminance,
                        calibrationData.luminance.toFloat()
                    )
                )
                Text(
                    text = stringResource(
                        id = R.string.calibration_properties_contrast,
                        calibrationData.contrast.toFloat()
                    )
                )
            }
        }
    }
}

@Composable
@Preview
private fun CameraCalibrationCardPreview() {
    LeishmaniappTheme {
        CameraCalibrationCard(
            calibrationData = ImageCalibrationData.mock(),
        )
    }
}