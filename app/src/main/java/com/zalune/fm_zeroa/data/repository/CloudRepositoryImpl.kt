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

//Se usa para la logica de multiproveedor con Firebase, Drive, Hybrid

//@Singleton
//class CloudRepositoryImpl @Inject constructor(
//    private val firebase: FirebaseStorageDataSource,
//    private val drive: GoogleDriveDataSource,
//    @AppQualifier private val preferredProvider: CloudProvider
//) : ICloudRepository {
//
//    override fun upload(filePath: String): Flow<Result<Float>> {
//        return when (preferredProvider) {
//            CloudProvider.FIREBASE -> firebase.upload(filePath)
//                .flatMapConcat { res ->
//                    // mapeamos progreso  y, al completar, podríamos emitir también un CloudFile si quieres
//                    flow { emit(res) }
//                }
//
//            CloudProvider.GOOGLE_DRIVE -> drive.upload(filePath)
//
//            CloudProvider.HYBRID ->
//                // En híbrido subimos primero a Firebase, luego a Drive
//                firebase.upload(filePath).flatMapConcat { res ->
//                    flow { emit(res) }.let { flowProgress ->
//                        // al 100% de firebase, encadenamos a Drive
//                        if (res.isSuccess && res.getOrNull() == 1.0f) {
//                            drive.upload(filePath)
//                        } else flow { /* nada */ }
//                    }
//                }
//        }
//    }
//
//    override fun download(cloudFile: CloudFile): Flow<Result<Float>> {
//        return when (cloudFile.provider) {
//            CloudProvider.FIREBASE ->
//                firebase.download(cloudFile.id, Uri.parse(cloudFile.url!!))
//
//            CloudProvider.GOOGLE_DRIVE ->
//                drive.download(cloudFile.id, Uri.parse(cloudFile.url!!))
//
//            else -> flow { emit(Result.failure<Float>(
//                IllegalArgumentException("Descarga híbrida no soportada"))
//            )
//            }
//        }
//    }
//
//    override suspend fun listAll(): Result<List<CloudFile>> {
//        suspend fun mapFirebase(): Result<List<CloudFile>> =
//            firebase.listAll().map { list ->
//                list.map { entry ->
//                    CloudFile(
//                        id       = entry.storagePath,
//                        name     = entry.name,
//                        size     = entry.size,
//                        url      = entry.downloadUrl,
//                        provider = CloudProvider.FIREBASE
//                    )
//                }
//            }
//
//        suspend fun mapDrive(): Result<List<CloudFile>> =
//            drive.listAll().map { list ->
//                list.map { entry ->
//                    CloudFile(
//                        id       = entry.id,
//                        name     = entry.name,
//                        size     = entry.size,
//                        url      = entry.downloadUrl,
//                        provider = CloudProvider.GOOGLE_DRIVE
//                    )
//                }
//            }
//
//        return when (preferredProvider) {
//            CloudProvider.FIREBASE -> mapFirebase()
//            CloudProvider.GOOGLE_DRIVE -> mapDrive()
//            CloudProvider.HYBRID -> {
//                val fRes = mapFirebase()
//                val dRes = mapDrive()
//                if (fRes.isSuccess && dRes.isSuccess) {
//                    Result.success(fRes.getOrNull()!! + dRes.getOrNull()!!)
//                } else {
//                    // Propaga el primer error que ocurra
//                    fRes.fold(
//                        onSuccess = { dRes },
//                        onFailure = { Result.failure(it) }
//                    )
//                }
//            }
//        }
//    }
//
//    override suspend fun delete(cloudFile: CloudFile): Result<Unit> {
//        return when (cloudFile.provider) {
//            CloudProvider.FIREBASE -> firebase.delete(cloudFile.id)
//            CloudProvider.GOOGLE_DRIVE -> drive.delete(cloudFile.id)
//            else -> Result.failure(IllegalArgumentException("Eliminación híbrida no soportada"))
//        }
//    }
//}