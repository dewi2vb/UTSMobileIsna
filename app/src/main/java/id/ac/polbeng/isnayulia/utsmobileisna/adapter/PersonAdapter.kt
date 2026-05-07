package id.ac.polbeng.isnayulia.utsmobileisna.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ac.polbeng.isnayulia.utsmobileisna.R
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Person

class PersonAdapter(
    private val onItemClick: (Person) -> Unit,
    private val onFavoriteClick: (Person, Boolean) -> Unit
) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    private var personList = listOf<Person>()
    private var favoriteIds = setOf<Int>()

    fun submitList(list: List<Person>) {
        personList = list
        notifyDataSetChanged()
    }

    fun updateFavorites(ids: Set<Int>) {
        favoriteIds = ids
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person, parent, false)
        return PersonViewHolder(view, onItemClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(personList[position], favoriteIds.contains(personList[position].id))
    }

    override fun getItemCount(): Int = personList.size

    class PersonViewHolder(
        itemView: android.view.View,
        private val onItemClick: (Person) -> Unit,
        private val onFavoriteClick: (Person, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        private val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val tvGender: TextView = itemView.findViewById(R.id.tvGender)
        private val tvBirthYear: TextView = itemView.findViewById(R.id.tvBirthYear)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)

        fun bind(person: Person, isFavorite: Boolean) {
            tvName.text = person.fullName
            tvEmail.text = person.email
            tvPhone.text = person.phone
            tvGender.text = if (person.gender == "male") "Laki-laki" else "Perempuan"
            tvBirthYear.text = try {
                person.birthday.split("-")[0]
            } catch (e: Exception) { "-" }

            btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )

            val diceBearUrl = "https://api.dicebear.com/7.x/adventurer/png?seed=${person.firstname}"
            val imageUrl = person.image?.replace("http://", "https://") ?: diceBearUrl

            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_avatar)
                .error(Glide.with(itemView.context).load(diceBearUrl))
                .centerCrop()
                .into(ivProfile)

            cardView.setOnClickListener { onItemClick(person) }
            btnFavorite.setOnClickListener { onFavoriteClick(person, !isFavorite) }
        }
    }
}