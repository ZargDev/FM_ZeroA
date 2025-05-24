package com.zalune.fm_zeroa.presentation.ui.viewmodel

import androidx.lifecycle.*
import com.zalune.fm_zeroa.domain.model.FileItem

import com.zalune.fm_zeroa.domain.repository.IFileExplorerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

// Excepción para carpetas protegidas
class AccessRestrictedException(message: String): Exception(message)

@HiltViewModel
class FileListViewModel @Inject constructor(
    private val repo: IFileExplorerRepository
) : ViewModel() {

    // Respaldo de la lista completa para filtros
    private var _allFiles: List<FileItem> = emptyList()

    // Estado de la ruta actual
    private val _currentPath = MutableLiveData<String>()
    val currentPath: LiveData<String> get() = _currentPath

    // Método para obtener el valor actual (seguro) ZARGSADD
    fun getCurrentPath(): String? = _currentPath.value

    // LiveData para la lista de archivos
    private val _fileList = MutableLiveData<List<FileItem>>()
    val fileList: LiveData<List<FileItem>> get() = _fileList

    // LiveData para mensajes de error
    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> get() = _errorMsg

    /**
     * Inicializa el ViewModel con la ruta dada y carga los archivos.
     */
    fun initialize(defaultPath: String) {
        _currentPath.value = defaultPath
        loadFiles(defaultPath)
    }

    /**
     * Carga los archivos del directorio en un hilo IO, maneja carpetas protegidas.
     */
    fun loadFiles(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1) Detectar rutas protegidas manualmente
                if (path.contains("/Android/data") || path.contains("/Android/obb")) {
                    throw AccessRestrictedException("No tienes permiso para acceder a $path")
                }
                // 2) Listar normalmente
                val list = repo.listFiles(path)
                _allFiles = list
                _fileList.postValue(list)
                _errorMsg.postValue(null)
            } catch (e: AccessRestrictedException) {
                _allFiles = emptyList()
                _fileList.postValue(emptyList())
                _errorMsg.postValue("Carpeta protegida: no puedes acceder a $path")
            } catch (e: Exception) {
                // Otros errores: tratamos como carpeta vacía
                _allFiles = emptyList()
                _fileList.postValue(emptyList())
                _errorMsg.postValue(null)
            }
        }
    }


    /**
     * Navega a un subdirectorio y recarga.
     */
    fun navigateTo(path: String) {
        if (path != _currentPath.value) { // ✅ Evita recargar si ya está en la misma ruta
            _currentPath.value = path
            loadFiles(path)
        }
    }

    /**
     * Filtra la lista actual por nombre de archivo.
     */
    fun filterFiles(query: String) {
        val filtered = if (query.isBlank()) {
            _allFiles
        } else {
            _allFiles.filter { it.name.contains(query, ignoreCase = true) }
        }
        _fileList.value = filtered
    }

    /**
     * Navega al directorio padre si existe.
     * @return true si navegó arriba, false si estaba en la raíz.
     */
    fun navigateUp(): Boolean {
        _currentPath.value?.let { current ->
            File(current).parent?.let { parent ->
                navigateTo(parent)
                return true
            }
        }
        return false
    }
}
