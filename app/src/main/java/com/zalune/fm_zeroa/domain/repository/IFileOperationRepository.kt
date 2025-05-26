package com.zalune.fm_zeroa.domain.repository

import java.io.File

interface IFileOperationRepository {
    suspend fun copy(source: File, targetDir: File): Boolean
    suspend fun move(source: File, targetDir: File): Boolean
    suspend fun delete(source: File): Boolean
}