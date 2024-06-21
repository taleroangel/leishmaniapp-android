package com.leishmaniapp.presentation.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leishmaniapp.R
import com.leishmaniapp.domain.entities.AnalysisStage
import com.leishmaniapp.domain.entities.ImageSample
import com.leishmaniapp.presentation.ui.theme.LeishmaniappTheme
import com.leishmaniapp.utilities.mock.MockGenerator.mock

@Composable
fun DiagnosisImageGridItem(modifier: Modifier = Modifier, image: ImageSample) {
    Card(
        modifier = modifier.width(IntrinsicSize.Max),
    ) {
        AsyncImage(
            model = image.path,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.macrophage)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#${(image.metadata.sample + 1)}",
                modifier = Modifier.padding(8.dp)
            )

            Box(modifier = Modifier.padding(8.dp)) {
                when (image.stage) {
                    AnalysisStage.NotAnalyzed -> Icon(
                        Icons.Filled.Block, contentDescription = stringResource(
                            id = R.string.not_analyzed
                        )
                    )

                    AnalysisStage.ResultError, AnalysisStage.DeliverError -> Icon(
                        Icons.Filled.Error,
                        contentDescription = stringResource(id = R.string.alert_deferred)
                    )

                    AnalysisStage.Analyzing -> Icon(
                        Icons.Filled.Sync,
                        contentDescription = stringResource(id = R.string.waiting)
                    )

                    AnalysisStage.Deferred -> Icon(
                        Icons.Filled.Cloud,
                        contentDescription = stringResource(id = R.string.processed)
                    )

                    AnalysisStage.Analyzed -> Icon(
                        Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.processed)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun DiagnosisImageGridItemPreview_Done() {
    LeishmaniappTheme {
        DiagnosisImageGridItem(
            image = ImageSample.mock().copy(stage = AnalysisStage.Analyzed)
        )
    }
}

@Composable
@Preview
fun DiagnosisImageGridItemPreview_Error() {
    LeishmaniappTheme {
        DiagnosisImageGridItem(
            image = ImageSample.mock().copy(stage = AnalysisStage.ResultError)
        )
    }
}

@Composable
@Preview
fun DiagnosisImageGridItemPreview_Loading() {
    LeishmaniappTheme {
        DiagnosisImageGridItem(
            image = ImageSample.mock().copy(stage = AnalysisStage.Analyzing)
        )
    }
}

@Composable
@Preview
fun DiagnosisImageGridItemPreview_Deferred() {
    LeishmaniappTheme {
        DiagnosisImageGridItem(
            image = ImageSample.mock().copy(stage = AnalysisStage.Deferred)
        )
    }
}

@Composable
@Preview
fun DiagnosisImageGridItemPreview_None() {
    LeishmaniappTheme {
        DiagnosisImageGridItem(
            image = ImageSample.mock().copy(stage = AnalysisStage.NotAnalyzed)
        )
    }
}