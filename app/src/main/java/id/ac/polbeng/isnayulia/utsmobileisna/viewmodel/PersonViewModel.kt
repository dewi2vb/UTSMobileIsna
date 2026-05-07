package id.ac.polbeng.isnayulia.utsmobileisna.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Person
import id.ac.polbeng.isnayulia.utsmobileisna.network.RetrofitClient
import kotlinx.coroutines.launch

class PersonViewModel : ViewModel() {
    private val _persons = MutableLiveData<List<Person>>()
    val persons: LiveData<List<Person>> = _persons

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var originalList = listOf<Person>()

    fun loadPersons(quantity: Int = 20, gender: String = "male") {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getPersons(
                    quantity = quantity,
                    locale = "id_ID",
                    gender = gender
                )
                if (response.isSuccessful && response.body() != null) {
                    originalList = response.body()?.data ?: emptyList()
                    _persons.value = originalList
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByName(query: String) {
        if (query.isEmpty()) {
            _persons.value = originalList
        } else {
            val filtered = originalList.filter { person ->
                person.fullName.contains(query, ignoreCase = true) ||
                        person.firstname.contains(query, ignoreCase = true) ||
                        person.lastname.contains(query, ignoreCase = true)
            }
            _persons.value = filtered
        }
    }

    fun filterByYear(year: Int) {
        val filtered = originalList.filter { person ->
            try {
                person.birthday.split("-")[0].toInt() == year
            } catch (e: Exception) {
                false
            }
        }
        _persons.value = filtered
    }

    fun filterByGenderAndQuantity(gender: String, quantity: Int) {
        // Filter by gender first from original list
        val filteredByGender = if (gender != "all") {
            originalList.filter { it.gender == gender }
        } else {
            originalList
        }
        // Then limit by quantity
        _persons.value = filteredByGender.take(quantity)
    }

    fun resetFilter() {
        _persons.value = originalList
    }
}