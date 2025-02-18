package com.leishmaniapp.presentation.ui.views.diagnosis

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leishmaniapp.R
import com.leishmaniapp.domain.entities.AnalysisStage
import com.leishmaniapp.domain.entities.ImageSample
import com.leishmaniapp.domain.entities.ModelDiagnosticElement
import com.leishmaniapp.domain.types.BoxCoordinates
import com.leishmaniapp.presentation.ui.composables.DiagnosticImage
import com.leishmaniapp.presentation.ui.theme.LeishmaniappTheme
import com.leishmaniapp.utilities.mock.MockGenerator.mock

/**
 * Unique label identificator for [AnimatedContent] in [DiagnosticImage]
 */
private const val IMAGE_ANIMATION_LABEL = "animation:animatedContent:diagnosticElementEdit"

/**
 * Unique label identificator for [Crossfade] animation for the [ModelDiagnosticElement] name
 */
private const val TEXT_ANIMATION_LABEL = "animation:crossfade:diagnosticElementRemoveText"

/**
 * Show an [ImageSample] and allow changes to diagnostic elements
 * @param onImageChange called when an image [ModelDiagnosticElement] are altered
 * @param onCancel called when changes are dismissed
 */
@Composable
fun DiagnosticImageEditSection(
    modifier: Modifier = Modifier,
    image: ImageSample,
    onImageChange: (ImageSample) -> Unit,
    onCancel: () -> Unit,
) {
    // Create an image copy with diagnostic elements
    var mutImageCopy: ImageSample by remember { mutableStateOf(image.copy()) }

    // Currently selected element
    var selectedElement: Pair<ModelDiagnosticElement, BoxCoordinates>? by remember {
        mutableStateOf(null)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Action buttons
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            // Accept changes
            TextButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { onImageChange.invoke(mutImageCopy) },
            ) {
                Icon(
                    Icons.Filled.Done,
                    contentDescription = stringResource(id = R.string.accept),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(id = R.string.accept))
            }

            // Discard changes
            TextButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { onCancel.invoke() },
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.dismiss),
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = stringResource(id = R.string.dismiss),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        // Show the image
        AnimatedContent(
            targetState = selectedElement,
            modifier = Modifier.weight(1f),
            label = IMAGE_ANIMATION_LABEL,
        ) { selectedElementState ->
            DiagnosticImage(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                image = mutImageCopy,
                selectedElement = selectedElementState,
                onElementPressed = { selectedElement = it },
            )
        }

        // Alert when selected element
        Column(
            modifier = Modifier
                .padding(vertical = 32.dp)
                .fillMaxWidth()
        ) {

            // Alert text (Are you sure you want to delete ... ?)
            Crossfade(
                targetState = selectedElement?.first?.id?.displayName,
                label = TEXT_ANIMATION_LABEL
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = if (it == null) {
                        stringResource(id = R.string.no_selected_item)
                    } else {
                        stringResource(id = R.string.erase_element, it)
                    }
                )
            }

            AnimatedVisibility(
                visible = (selectedElement != null),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                // Buttons
                Row(
                    modifier = Modifier
                        .padding(horizontal = 64.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Cancel button
                    FilledTonalButton(onClick = {
                        // Deselect the element
                        selectedElement = null
                    }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }

                    // Erase button
                    Button(onClick = {
                        // Find the element in list and remove it
                        val diagnosticElement = mutImageCopy.elements.find {
                            // Search by selected element
                            (it == selectedElement?.first)
                        }

                        // Diagnostic element was found
                        if (diagnosticElement != null &&
                            diagnosticElement is ModelDiagnosticElement &&
                            selectedElement != null
                        ) {
                            // Create a new diagnostic element without the coordinate
                            val newDiagnosticElement = diagnosticElement.copy(
                                coordinates = diagnosticElement.coordinates.minusElement(
                                    selectedElement!!.second
                                )
                            )

                            // Replace the image with the new elements
                            mutImageCopy = mutImageCopy.copy(
                                elements = mutImageCopy.elements.minus(diagnosticElement)
                                    .plus(newDiagnosticElement)
                            )
                        }

                        // Deselect the element
                        selectedElement = null

                    }) {
                        Text(text = stringResource(id = R.string.erase))
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DiagnosticImageEditSectionPreview() {

    var image by remember {
        mutableStateOf(ImageSample.mock(stage = AnalysisStage.Analyzed))
    }

    LeishmaniappTheme {
        DiagnosticImageEditSection(
            image = image,
            onImageChange = { image = it },
            onCancel = {}
        )
    }
}