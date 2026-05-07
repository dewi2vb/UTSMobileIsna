package id.ac.polbeng.isnayulia.utsmobileisna.network

import id.ac.polbeng.isnayulia.utsmobileisna.data.model.PersonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/v2/persons")
    suspend fun getPersons(
        @Query("_quantity") quantity: Int = 10,
        @Query("_locale") locale: String = "id_ID",
        @Query("_gender") gender: String = "male"
    ): Response<PersonResponse>
}