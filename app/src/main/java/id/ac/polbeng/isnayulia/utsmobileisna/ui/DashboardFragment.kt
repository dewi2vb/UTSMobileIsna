package id.ac.polbeng.isnayulia.utsmobileisna.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import id.ac.polbeng.isnayulia.utsmobileisna.R
import id.ac.polbeng.isnayulia.utsmobileisna.data.database.FavoriteDatabase
import id.ac.polbeng.isnayulia.utsmobileisna.databinding.FragmentDashboardBinding
import id.ac.polbeng.isnayulia.utsmobileisna.network.RetrofitClient
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStatistics()

        binding.btnGoToPersons.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_person_list)
        }

        binding.btnGoToFavorites.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_favorite)
        }
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getPersons(quantity = 20)
                if (response.isSuccessful && response.body() != null) {
                    val persons = response.body()?.data ?: emptyList()
                    val maleCount = persons.count { it.gender == "male" }
                    val femaleCount = persons.count { it.gender == "female" }
                    val favoriteCount = FavoriteDatabase.getDatabase(requireContext())
                        .favoriteDao().getAllFavorites().size

                    binding.tvTotalPersons.text = persons.size.toString()
                    binding.tvMaleCount.text = maleCount.toString()
                    binding.tvFemaleCount.text = femaleCount.toString()
                    binding.tvFavoriteCount.text = favoriteCount.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}