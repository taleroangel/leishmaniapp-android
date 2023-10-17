package com.leishmaniapp.entities

import android.os.Parcelable
import com.leishmaniapp.entities.disease.Disease
import com.leishmaniapp.usecases.serialization.LocalDateTimeTypeParceler
import com.leishmaniapp.usecases.serialization.UUIDSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID
import kotlin.reflect.KClass

/**
 * Class representing a Diagnosis
 * @immutable Replace by using [Diagnosis.copy]
 */
@Serializable
@Parcelize
data class Diagnosis(
    val id: @Serializable(UUIDSerializer::class) UUID = UUID.randomUUID(),
    val specialistResult: Boolean,
    val modelResult: Boolean,
    @TypeParceler<LocalDateTime, LocalDateTimeTypeParceler>
    val date: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val remarks: String?,
    val specialist: Specialist,
    val patient: Patient,
    val disease: Disease,
    @Transient val images: Map<Int, Image> = mapOf(),
) : Parcelable {

    constructor(
        specialist: Specialist,
        patient: Patient,
        disease: Disease
    ) : this(
        specialistResult = false,
        modelResult = false,
        remarks = null,
        images = mapOf(),
        patient = patient,
        specialist = specialist,
        disease = disease
    )


    /**
     * Group [DiagnosticElement] in a map in which the key is the [DiagnosticElementName] and the
     * value is another map in which the key is the element type (either [ModelDiagnosticElement]
     * or [SpecialistDiagnosticElement], and the value is the number of those elements found
     * @TODO Write tests for this function
     */
    @IgnoredOnParcel
    val computedResults: Map<DiagnosticElementName, Map<KClass<out DiagnosticElement>, Int>> by lazy {
        images.values.flatMap {
            it.elements
        }
            .groupBy { it.name }
            .mapValues {
                it.value.map { diagnosticElement ->
                    diagnosticElement::class to diagnosticElement.amount
                }.groupingBy { elementPair ->
                    elementPair.first
                }.aggregate { _, accumulator: Int?, element, _ ->
                    accumulator?.plus(element.second) ?: element.second
                }
            }
    }

    /**
     * Get the amount of images associated with the diagnosis
     * @TODO Write tests for this function
     */
    val samples: Int
        get() = images.size

    /**
     * Checks if every image in the diagnosis has been processed
     * @TODO Write tests for this function
     */
    val completed: Boolean
        get() = images.values.all { it.processed == ImageAnalysisStatus.Analyzed }
}
