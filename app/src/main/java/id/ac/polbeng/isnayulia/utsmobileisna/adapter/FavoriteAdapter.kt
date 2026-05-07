package id.ac.polbeng.isnayulia.utsmobileisna.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ac.polbeng.isnayulia.utsmobileisna.R
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Person

class FavoriteAdapter(
    private val onItemClick: (Person) -> Unit,
    private val onFavoriteClick: (Person) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var favoriteList = listOf<Person>()

    fun submitList(list: List<Person>) {
        favoriteList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person, parent, false)
        return FavoriteViewHolder(view, onItemClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteList[position])
    }

    override fun getItemCount(): Int = favoriteList.size

    class FavoriteViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Person) -> Unit,
        private val onFavoriteClick: (Person) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        private val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val tvGender: TextView = itemView.findViewById(R.id.tvGender)
        private val tvBirthYear: TextView = itemView.findViewById(R.id.tvBirthYear)
        private val btnFavorite: ImageView = itemView.findViewById(R.id.btnFavorite)

        fun bind(person: Person) {
            tvName.text = person.fullName
            tvEmail.text = person.email
            tvPhone.text = person.phone
            tvGender.text = if (person.gender == "male") "Laki-laki" else "Perempuan"
            tvBirthYear.text = try {
                person.birthday.split("-")[0]
            } catch (e: Exception) { "-" }

            btnFavorite.setImageResource(R.drawable.ic_favorite_filled)

            val fallbackUrl = "https://ui-avatars.com/api/?name=${person.firstname}+${person.lastname}&background=random&size=128"
            val imageUrl = person.image?.replace("http://", "https://") ?: fallbackUrl

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_avatar)
                .error(fallbackUrl)
                .centerCrop()
                .into(ivProfile)

            cardView.setOnClickListener { onItemClick(person) }
            btnFavorite.setOnClickListener { onFavoriteClick(person) }
        }
    }
}