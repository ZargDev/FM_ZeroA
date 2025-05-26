package com.zalune.fm_zeroa.presentation.ui.viewmodel

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.*
import com.zalune.fm_zeroa.domain.model.FileItem

import com.zalune.fm_zeroa.domain.repository.IFileExplorerRepository
import com.zalune.fm_zeroa.domain.repository.usecase.CopyFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.CutFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

// Excepción para carpetas protegidas
class AccessRestrictedException(message: String): Exception(message)

@HiltViewModel
class FileListViewModel @Inject constructor(
    private val repo: IFileExplorerRepository,
    private val copyUseCase: CopyFileUseCase,
    private val cutUseCase: CutFileUseCase
) : ViewModel() {

    private var initialized = false

    // estado del portapapeles
    private val _clipboard = MutableLiveData<ClipboardState>(ClipboardState.None)
    val clipboard: LiveData<ClipboardState> = _clipboard

    // respaldos para búsqueda
    private var backupFiles: List<FileItem> = emptyList()
    private var backupPath: String = ""
    private var inSearchMode = false

    // ruta actual
    private val _currentPath = MutableLiveData<String>()
    val currentPath: LiveData<String> = _currentPath

    // lista mostrada
    private val _fileList = MutableLiveData<List<FileItem>>()
    val fileList: LiveData<List<FileItem>> = _fileList

    // mensaje de error o "no encontrado"
    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> = _errorMsg

    /** inicia en modo normal */
    fun initialize(defaultPath: String) {
        if (initialized) return
        initialized = true
        inSearchMode = false
        _currentPath.value = defaultPath
        loadFiles(defaultPath)
    }

    /** carga archivos y sale de búsqueda */
    fun loadFiles(path: String) {
        backupPath = path
        inSearchMode = false
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (path.contains("/Android/data") || path.contains("/Android/obb")) {
                    throw AccessRestrictedException("No tienes permiso para acceder a $path")
                }
                val list = repo.listFiles(path)
                backupFiles = list
                _fileList.postValue(list)
                _errorMsg.postValue(null)
                _currentPath.postValue(path)
            } catch (e: AccessRestrictedException) {
                backupFiles = emptyList()
                _fileList.postValue(emptyList())
                _errorMsg.postValue("Carpeta protegida: no puedes acceder a $path")
            } catch (_: Exception) {
                backupFiles = emptyList()
                _fileList.postValue(emptyList())
                _errorMsg.postValue(null)
            }
        }
    }

    /** filtra en tiempo real y marca inSearchMode = true la primera vez */
    fun filterFiles(query: String, extensions: List<String> = emptyList()) {
        val base = if (!inSearchMode) {
            backupFiles = _fileList.value ?: emptyList()
            backupPath = _currentPath.value.orEmpty()
            inSearchMode = true
            backupFiles
        } else backupFiles

        val filtered = if (query.isBlank()) {
            // restauramos y salimos de búsqueda
            inSearchMode = false
            backupFiles
        } else {
            base.filter { item ->
                item.name.contains(query, ignoreCase = true) &&
                        (extensions.isEmpty() || extensions.any { ext -> item.name.endsWith(".$ext", true) })
            }
        }

        _fileList.value = filtered
        _errorMsg.value = if (query.isNotBlank() && filtered.isEmpty())
            "Archivo no encontrado"
        else
            null
    }

    fun isInSearchMode(): Boolean = inSearchMode
    fun exitSearchMode() {
        inSearchMode = false
        loadFiles(backupPath)
    }
    // copiar/cortar
    fun prepareCopy(path: String) {
        _clipboard.value = ClipboardState.Copy(listOf(path))
    }

    fun prepareCut(path: String) {
        _clipboard.value = ClipboardState.Cut(listOf(path))
    }

    fun clearClipboard() {
        _clipboard.value = ClipboardState.None
    }


    fun paste(targetDir: String) {
        val state = _clipboard.value ?: return
        viewModelScope.launch {
            val ok = try {
                when (state) {
                    is ClipboardState.Copy -> copyUseCase(state.items, targetDir)
                    is ClipboardState.Cut  -> cutUseCase(state.items, targetDir)
                    else                   -> false
                }
            } catch (e: Exception) {
                Log.e("FileListViewModel", "Error al pegar", e)
                false
            }

            if (ok) {
                // Éxito: recargamos y limpiamos clip
                loadFiles(targetDir)
            } else {
                // Fracaso: notificamos al usuario
                _errorMsg.postValue("Error al pegar archivos")
            }

            clearClipboard()
        }
    }

    // copia recursiva para varias rutas:
    private suspend operator fun CopyFileUseCase.invoke(sources: List<String>, targetDir: String) =
        sources.all { this(it, targetDir) }

    private suspend operator fun CutFileUseCase.invoke(sources: List<String>, targetDir: String) =
        sources.all { this(it, targetDir) }

}
