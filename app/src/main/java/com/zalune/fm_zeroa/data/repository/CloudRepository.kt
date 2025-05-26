package com.zalune.fm_zeroa.data.repository

import android.net.Uri
import com.zalune.fm_zeroa.data.source.FirebaseStorageDataSource
import com.zalune.fm_zeroa.di.AppQualifier
import com.zalune.fm_zeroa.domain.model.CloudFile
import com.zalune.fm_zeroa.domain.model.CloudProvider
import com.zalune.fm_zeroa.domain.repository.ICloudRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudRepository @Inject constructor(
    private val firebaseDs: FirebaseStorageDataSource
) : ICloudRepository {

    override fun upload(filePath: String): Flow<Result<Float>> =
        firebaseDs.upload(filePath)

    override fun download(cloudFile: CloudFile): Flow<Result<Float>> =
        firebaseDs.download(
            storagePath = cloudFile.id,
            destFileUri = Uri.parse(cloudFile.url!!)
        )

    override suspend fun listAll(): Result<List<CloudFile>> =
        firebaseDs.listAll().map { entries ->
            entries.map { e ->
                CloudFile(
                    id       = e.storagePath,
                    name     = e.name,
                    size     = e.size,
                    url      = e.downloadUrl,
                    provider = CloudProvider.FIREBASE
                )
            }
        }

    override suspend fun delete(cloudFile: CloudFile): Result<Unit> =
        firebaseDs.delete(cloudFile.id)
}