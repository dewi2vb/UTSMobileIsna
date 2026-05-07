package id.ac.polbeng.isnayulia.utsmobileisna.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import id.ac.polbeng.isnayulia.utsmobileisna.R
import id.ac.polbeng.isnayulia.utsmobileisna.data.database.FavoriteDatabase
import id.ac.polbeng.isnayulia.utsmobileisna.data.database.FavoriteEntity
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Person
import id.ac.polbeng.isnayulia.utsmobileisna.databinding.ActivityDetailPersonBinding
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class DetailPersonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPersonBinding
    private lateinit var person: Person
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        binding = ActivityDetailPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadPersonData()
        setupMap()
        checkFavoriteStatus()

        // PERBAIKAN - Gunakan anonymous object
        binding.btnFavoriteDetail.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                toggleFavorite()
            }
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Person"
    }

    private fun loadPersonData() {
        val json = intent.getStringExtra("EXTRA_PERSON")
        person = Gson().fromJson(json, Person::class.java)

        with(binding) {
            tvFullName.text = person.fullName
            tvEmail.text = person.email
            tvPhone.text = person.phone
            tvGender.text = if (person.gender == "male") "Laki-laki" else "Perempuan"
            tvBirthday.text = person.birthday

            val imageUrl = person.image?.replace("http://", "https://")
                ?: "https://randomuser.me/api/portraits/${if(person.gender == "male") "men" else "women"}/${person.id % 100}.jpg"

            Glide.with(this@DetailPersonActivity)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .circleCrop()
                .into(ivProfile)

            person.address?.let { address ->
                tvAddressStreet.text = "${address.street} ${address.streetName}"
                tvAddressBuilding.text = "Nomor: ${address.buildingNumber}"
                tvAddressCity.text = address.city
                tvAddressZipcode.text = "Kode Pos: ${address.zipcode}"
                tvAddressCountry.text = address.country
                tvCoordinates.text = "${address.latitude}, ${address.longitude}"
            }
        }
    }

    private fun setupMap() {
        person.address?.let { address ->
            val geoPoint = GeoPoint(address.latitude, address.longitude)
            binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
            binding.mapView.controller.setZoom(15.0)
            binding.mapView.controller.setCenter(geoPoint)

            val marker = Marker(binding.mapView)
            marker.position = geoPoint
            marker.title = person.fullName
            marker.snippet = address.fullAddress
            binding.mapView.overlays.add(marker)
            binding.mapView.invalidate()
        }
    }

    private fun checkFavoriteStatus() {
        lifecycleScope.launch {
            val count = FavoriteDatabase.getDatabase(this@DetailPersonActivity)
                .favoriteDao().isFavorite(person.id)
            isFavorite = count > 0
            updateFavoriteButton()
        }
    }

    private fun toggleFavorite() {
        lifecycleScope.launch {
            val dao = FavoriteDatabase.getDatabase(this@DetailPersonActivity).favoriteDao()
            if (isFavorite) {
                dao.delete(person.id)
                isFavorite = false
                android.widget.Toast.makeText(this@DetailPersonActivity, "Dihapus dari favorite", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                dao.insert(FavoriteEntity.fromPerson(person))
                isFavorite = true
                android.widget.Toast.makeText(this@DetailPersonActivity, "Ditambahkan ke favorite", android.widget.Toast.LENGTH_SHORT).show()
            }
            updateFavoriteButton()
        }
    }

    private fun updateFavoriteButton() {
        binding.btnFavoriteDetail.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}