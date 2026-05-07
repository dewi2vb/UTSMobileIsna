package id.ac.polbeng.isnayulia.utsmobileisna.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Address
import id.ac.polbeng.isnayulia.utsmobileisna.data.model.Person

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val personId: Int,
    val firstname: String,
    val lastname: String,
    val email: String,
    val phone: String,
    val birthday: String,
    val gender: String,
    val image: String?,
    val street: String,
    val streetName: String,
    val buildingNumber: String,
    val city: String,
    val zipcode: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toPerson(): Person {
        return Person(
            id = personId,
            firstname = firstname,
            lastname = lastname,
            email = email,
            phone = phone,
            birthday = birthday,
            gender = gender,
            image = image,
            address = Address(
                id = 0,
                street = street,
                streetName = streetName,
                buildingNumber = buildingNumber,
                city = city,
                zipcode = zipcode,
                country = country,
                countyCode = "",
                latitude = latitude,
                longitude = longitude
            )
        )
    }

    companion object {
        fun fromPerson(person: Person): FavoriteEntity {
            return FavoriteEntity(
                personId = person.id,
                firstname = person.firstname,
                lastname = person.lastname,
                email = person.email,
                phone = person.phone,
                birthday = person.birthday,
                gender = person.gender,
                image = person.image,
                street = person.address?.street ?: "",
                streetName = person.address?.streetName ?: "",
                buildingNumber = person.address?.buildingNumber ?: "",
                city = person.address?.city ?: "",
                zipcode = person.address?.zipcode ?: "",
                country = person.address?.country ?: "",
                latitude = person.address?.latitude ?: 0.0,
                longitude = person.address?.longitude ?: 0.0
            )
        }
    }
}