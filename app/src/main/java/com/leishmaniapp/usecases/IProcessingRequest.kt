package com.leishmaniapp.usecases

import com.leishmaniapp.entities.Diagnosis
import com.leishmaniapp.entities.Image
import com.leishmaniapp.entities.ImageProcessingPayload
import java.util.UUID

/**
 * Make request to process images
 */
interface IProcessingRequest {
    /**
     * Upload the image to cloud-based bucket, requires [Diagnosis.id] and [Image.sample] to create the path,
     * takes [Image.path] as the image to be uploaded
     * @throws Exception on failure
     */
    suspend fun uploadImageToBucket(diagnosisId: UUID, image: Image): String

    /**
     * Generate analysis request on an already uploaded image
     * @throws Exception on failure
     */
    suspend fun generatePayloadRequest(payload: ImageProcessingPayload)
}