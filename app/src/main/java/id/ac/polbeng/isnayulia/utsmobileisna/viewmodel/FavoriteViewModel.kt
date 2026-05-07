package id.ac.polbeng.isnayulia.utsmobileisna.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.ac.polbeng.isnayulia.utsmobileisna.data.database.FavoriteDatabase
import id.ac.polbeng.isnayulia.utsmobileisna.data.database.FavoriteEntity
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Person
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val database = FavoriteDatabase.getDatabase(application)
    private val favoriteDao = database.favoriteDao()

    private val _favorites = MutableLiveData<List<Person>>()
    val favorites: LiveData<List<Person>> = _favorites

    fun loadFavorites() {
        viewModelScope.launch {
            val entities = favoriteDao.getAllFavorites()
            _favorites.value = entities.map { it.toPerson() }
        }
    }

    suspend fun isFavorite(personId: Int): Boolean {
        return favoriteDao.isFavorite(personId) > 0
    }

    fun addToFavorites(person: Person) {
        viewModelScope.launch {
            val entity = FavoriteEntity.fromPerson(person)
            favoriteDao.insert(entity)
            loadFavorites()
        }
    }

    fun removeFromFavorites(personId: Int) {
        viewModelScope.launch {
            favoriteDao.delete(personId)
            loadFavorites()
        }
    }

    fun toggleFavorite(person: Person, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isFav = isFavorite(person.id)
            if (isFav) {
                removeFromFavorites(person.id)
                onComplete(false)
            } else {
                addToFavorites(person)
                onComplete(true)
            }
        }
    }

    fun filterFavorites(query: String) {
        if (query.isEmpty()) {
            loadFavorites()
        } else {
            viewModelScope.launch {
                val allFavorites = favoriteDao.getAllFavorites()
                val filtered = allFavorites.filter { entity ->
                    entity.firstname.contains(query, ignoreCase = true) ||
                            entity.lastname.contains(query, ignoreCase = true) ||
                            "${entity.firstname} ${entity.lastname}".contains(query, ignoreCase = true)
                }
                _favorites.value = filtered.map { it.toPerson() }
            }
        }
    }
}