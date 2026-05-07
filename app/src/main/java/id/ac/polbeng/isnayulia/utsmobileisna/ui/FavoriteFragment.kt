package id.ac.polbeng.isnayulia.utsmobileisna.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import id.ac.polbeng.isnayulia.utsmobileisna.R
import id.ac.polbeng.isnayulia.utsmobileisna.adapter.FavoriteAdapter
import id.ac.polbeng.isnayulia.utsmobileisna.data.database.FavoriteDatabase
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Person
import id.ac.polbeng.isnayulia.utsmobileisna.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoriteAdapter
    private var favoriteList = listOf<Person>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadFavorites()

        binding.etSearchFavorite.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                filterFavorites(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter(
            onItemClick = { person -> navigateToDetail(person) },
            onFavoriteClick = { person -> removeFromFavorites(person) }
        )
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            val favorites = FavoriteDatabase.getDatabase(requireContext()).favoriteDao().getAllFavorites()
            favoriteList = favorites.map { it.toPerson() }
            adapter.submitList(favoriteList)
            binding.emptyView.visibility = if (favoriteList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun filterFavorites(query: String) {
        if (query.isEmpty()) {
            adapter.submitList(favoriteList)
        } else {
            val filtered = favoriteList.filter { person ->
                person.fullName.contains(query, ignoreCase = true)
            }
            adapter.submitList(filtered)
        }
    }

    private fun removeFromFavorites(person: Person) {
        lifecycleScope.launch {
            FavoriteDatabase.getDatabase(requireContext()).favoriteDao().delete(person.id)
            loadFavorites()
            android.widget.Toast.makeText(requireContext(), "Dihapus dari favorite", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDetail(person: Person) {
        val intent = android.content.Intent(requireContext(), DetailPersonActivity::class.java)
        val json = Gson().toJson(person)
        intent.putExtra("EXTRA_PERSON", json)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}