package com.leishmaniapp.persistance.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.leishmaniapp.persistance.entities.ImageRoom
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ImageDao {

    @Upsert
    suspend fun upsertImage(image: ImageRoom)

    @Delete
    suspend fun deleteImage(image: ImageRoom)

    @Query("SELECT * FROM ImageRoom IR WHERE IR.diagnosisUUID = :uuid AND IR.sample = :sample")
    suspend fun imageForDiagnosis(uuid: UUID, sample: Int): ImageRoom?

    @Query("SELECT * FROM ImageRoom IR WHERE IR.diagnosisUUID = :uuid AND IR.sample = :sample")
    fun imageForDiagnosisFlow(uuid: UUID, sample: Int): Flow<ImageRoom?>

    //TODO: Test this method
    @Query("SELECT * FROM ImageRoom WHERE diagnosisUUID = :uuid")
    suspend fun allImagesForDiagnosis(uuid: UUID): List<ImageRoom>
}