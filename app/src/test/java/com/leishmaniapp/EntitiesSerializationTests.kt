package com.leishmaniapp

import com.leishmaniapp.entities.Diagnosis
import com.leishmaniapp.entities.DiagnosticElement
import com.leishmaniapp.entities.DocumentType
import com.leishmaniapp.entities.IdentificationDocument
import com.leishmaniapp.entities.Password
import com.leishmaniapp.entities.Patient
import com.leishmaniapp.entities.Specialist
import com.leishmaniapp.entities.Username
import com.leishmaniapp.entities.disease.Disease
import com.leishmaniapp.entities.disease.LeishmaniasisGiemsaDisease
import com.leishmaniapp.utils.MockGenerator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

class EntitiesSerializationTests {
    @Test
    fun patientSerializationShouldGenerateHash() {
        val patient = Patient(
            "Jane Doe",
            IdentificationDocument("1234567890"),
            DocumentType.CC
        )

        Assert.assertEquals(Json.encodeToString(patient), "\"${patient.hash}\"")
    }

    @Test
    fun diseaseSerializationShouldSerializeToId() {
        val disease = LeishmaniasisGiemsaDisease as Disease
        Assert.assertEquals(Json.encodeToString(disease), "\"${disease.id}\"")
    }

    @Test
    fun diseaseSerializationFromIdShouldReturnSubclass() {
        val disease = (LeishmaniasisGiemsaDisease) as Disease

        Assert.assertEquals(
            Json.decodeFromString<Disease>("\"${disease.id}\""),
            disease
        )
    }

    @Test
    fun specialistSerializationShouldNotSerializePassword() {
        val specialist = Specialist(
            "John Doe",
            Username("john_doe"),
            Password("password")
        )

        Assert.assertEquals(
            """{"name":"${specialist.name}","username":"${specialist.username.value}"}""",
            Json.encodeToString(specialist)
        )
    }

    @Test
    fun diagnosticElementByTypeShouldSerialize() {
        val specialistElement = MockGenerator.mockSpecialistDiagnosticElement()
        val modelElement = MockGenerator.mockModelDiagnosticElement()

        print(Json.encodeToString(modelElement))

        Assert.assertEquals(
            """
            {"name":"mock.disease:mock_element","amount":${specialistElement.amount}}
        """.trimIndent(), Json.encodeToString(specialistElement)
        )
    }

    @Test
    fun diagnosticElementNameByStringShouldReturnDiseaseDiagnosticElementName() {
        // Serialize
        val diagnosticElement = MockGenerator.mockModelDiagnosticElement()
        val serializedValue = Json.encodeToString<DiagnosticElement>(diagnosticElement)

        // Deserialize
        Assert.assertEquals(
            diagnosticElement.name,
            Json.decodeFromString<DiagnosticElement>(serializedValue).name
        )
    }

    @Test
    fun diagnosisJsonSerializationIncludesAllEntities() {
        /*Strings cannot have spaces in order to trim spaces and newlines*/
        val diagnosis = Diagnosis(
            specialistResult = true,
            modelResult = false,
            remarks = "Loremipsumdolorsitametconsecteturadipiscingelit",
            specialist = Specialist(
                "JohnDoe",
                Username("john_doe"),
                Password("password")
            ),
            patient = Patient(
                "JaneDoe",
                IdentificationDocument("1234567890"),
                DocumentType.CC
            ),
            disease = LeishmaniasisGiemsaDisease,
        )

        val expectedJson = """
            {
              "id": "${diagnosis.id}",
              "specialistResult": ${diagnosis.specialistResult},
              "modelResult": ${diagnosis.modelResult},
              "date": "${diagnosis.date}",
              "remarks": "${diagnosis.remarks}",
              "specialist": {
                "name": "${diagnosis.specialist.name}",
                "username": "${diagnosis.specialist.username.value}"
              },
              "patient": "${diagnosis.patient.hash}",
              "disease": "${diagnosis.disease.id}"
            }
        """.replace("\n", "").replace("\t", "").replace(" ", "")

        Assert.assertEquals(expectedJson, Json.encodeToString(diagnosis))
    }
}