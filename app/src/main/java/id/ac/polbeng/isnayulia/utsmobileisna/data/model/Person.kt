package id.ac.polbeng.isnayulia.utsmobileisna.data.model

import com.google.gson.annotations.SerializedName

data class PersonResponse(
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("code")
    val code: Int,
    @field:SerializedName("total")
    val total: Int,
    @field:SerializedName("data")
    val data: List<Person>
)

data class Person(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("firstname")
    val firstname: String,
    @field:SerializedName("lastname")
    val lastname: String,
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("phone")
    val phone: String,
    @field:SerializedName("birthday")
    val birthday: String,
    @field:SerializedName("gender")
    val gender: String,
    @field:SerializedName("image")
    val image: String?,
    @field:SerializedName("address")
    val address: Address?
) {
    val fullName: String get() = "$firstname $lastname"
}

data class Address(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("street")
    val street: String,
    @field:SerializedName("streetName")
    val streetName: String,
    @field:SerializedName("buildingNumber")
    val buildingNumber: String,
    @field:SerializedName("city")
    val city: String,
    @field:SerializedName("zipcode")
    val zipcode: String,
    @field:SerializedName("country")
    val country: String,
    @field:SerializedName("county_code")
    val countyCode: String,
    @field:SerializedName("latitude")
    val latitude: Double,
    @field:SerializedName("longitude")
    val longitude: Double
) {
    val fullAddress: String
        get() = "$street $streetName No. $buildingNumber, $city, $zipcode, $country"
}