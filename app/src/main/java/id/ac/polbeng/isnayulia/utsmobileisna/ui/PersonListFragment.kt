package id.ac.polbeng.isnayulia.utsmobileisna.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import id.ac.polbeng.isnayulia.utsmobileisna.R
import id.ac.polbeng.isnayulia.utsmobileisna.adapter.PersonAdapter
import id.ac.polbeng.isnayulia.utsmobileisna.data.database.FavoriteDatabase
import id.ac.polbeng.isnayulia.utsmobileisna.data.database.FavoriteEntity
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Person
import id.ac.polbeng.isnayulia.utsmobileisna.databinding.FragmentPersonListBinding
import id.ac.polbeng.isnayulia.utsmobileisna.network.RetrofitClient
import kotlinx.coroutines.launch

class PersonListFragment : Fragment() {
    private var _binding: FragmentPersonListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PersonAdapter
    private var favoriteIds = mutableSetOf<Int>()
    private var currentPersons = listOf<Person>()
    private var originalPersons = listOf<Person>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadPersons(20, "all")

        binding.btnFilter.setOnClickListener {
            showAdvancedFilterDialog()
        }

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                filterByName(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = PersonAdapter(
            onItemClick = { person -> navigateToDetail(person) },
            onFavoriteClick = { person, isFavorite -> toggleFavorite(person, isFavorite) }
        )
        binding.rvPersons.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPersons.adapter = adapter
    }

    private fun loadPersons(quantity: Int, gender: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val genderParam = when (gender) {
                    "male" -> "male"
                    "female" -> "female"
                    else -> "male"
                }

                val response = RetrofitClient.instance.getPersons(
                    quantity = quantity,
                    locale = "id_ID",
                    gender = genderParam
                )
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    originalPersons = response.body()?.data ?: emptyList()
                    currentPersons = originalPersons
                    adapter.submitList(currentPersons)
                    loadFavoriteIds()
                } else {
                    Toast.makeText(requireContext(), "Error: \${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: \${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPersonsWithFilter(quantity: Int, gender: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val genderParam = when (gender) {
                    "male" -> "male"
                    "female" -> "female"
                    else -> "male"
                }

                val response = RetrofitClient.instance.getPersons(
                    quantity = quantity,
                    locale = "id_ID",
                    gender = genderParam
                )
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    originalPersons = response.body()?.data ?: emptyList()
                    currentPersons = originalPersons
                    adapter.submitList(currentPersons)
                    loadFavoriteIds()

                    val genderText = when (gender) {
                        "male" -> "Laki-laki"
                        "female" -> "Perempuan"
                        else -> "Semua"
                    }
                    Toast.makeText(requireContext(), "Menampilkan \$quantity data \$genderText", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Error: \${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: \${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFavoriteIds() {
        lifecycleScope.launch {
            val favorites = FavoriteDatabase.getDatabase(requireContext()).favoriteDao().getAllFavorites()
            favoriteIds = favorites.map { it.personId }.toMutableSet()
            adapter.updateFavorites(favoriteIds)
        }
    }

    private fun toggleFavorite(person: Person, isFavorite: Boolean) {
        lifecycleScope.launch {
            val dao = FavoriteDatabase.getDatabase(requireContext()).favoriteDao()
            if (isFavorite) {
                dao.insert(FavoriteEntity.fromPerson(person))
                favoriteIds.add(person.id)
                Toast.makeText(requireContext(), "Ditambahkan ke favorite", Toast.LENGTH_SHORT).show()
            } else {
                dao.delete(person.id)
                favoriteIds.remove(person.id)
                Toast.makeText(requireContext(), "Dihapus dari favorite", Toast.LENGTH_SHORT).show()
            }
            adapter.updateFavorites(favoriteIds)
        }
    }

    private fun filterByName(query: String) {
        if (query.isEmpty()) {
            adapter.submitList(currentPersons)
        } else {
            val filtered = currentPersons.filter { person ->
                person.fullName.contains(query, ignoreCase = true) ||
                        person.firstname.contains(query, ignoreCase = true) ||
                        person.lastname.contains(query, ignoreCase = true)
            }
            adapter.submitList(filtered)
        }
    }

    private fun showAdvancedFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_advanced_filter, null)

        val radioMale = dialogView.findViewById<RadioButton>(R.id.radio_male)
        val radioFemale = dialogView.findViewById<RadioButton>(R.id.radio_female)
        val radioAll = dialogView.findViewById<RadioButton>(R.id.radio_all)
        val etQuantity = dialogView.findViewById<TextInputEditText>(R.id.et_quantity)
        val btnApply = dialogView.findViewById<MaterialButton>(R.id.btn_apply_filter)
        val btnReset = dialogView.findViewById<MaterialButton>(R.id.btn_reset_filter)

        etQuantity.setText("10")

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Filter Data Person")
            .setView(dialogView)
            .setNegativeButton("Batal", null)
            .create()

        btnApply.setOnClickListener {
            val quantity = etQuantity.text.toString().toIntOrNull() ?: 10
            val gender = when {
                radioMale.isChecked -> "male"
                radioFemale.isChecked -> "female"
                else -> "all"
            }

            if (gender == "all") {
                loadPersonsWithFilter(quantity, "male")
            } else {
                loadPersonsWithFilter(quantity, gender)
            }
            dialog.dismiss()
        }

        btnReset.setOnClickListener {
            loadPersons(20, "male")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun navigateToDetail(person: Person) {
        val intent = android.content.Intent(requireContext(), DetailPersonActivity::class.java)
        val json = Gson().toJson(person)
        intent.putExtra("EXTRA_PERSON", json)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}