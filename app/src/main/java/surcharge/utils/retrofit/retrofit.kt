package surcharge.utils.retrofit

import android.util.Base64
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.security.MessageDigest
import java.security.SecureRandom


data class Token(
    @SerializedName("access_token") val accessToken: String = "",
    @SerializedName("expires_at") val accessTokenExpiresAt: String = "",
    @SerializedName("merchant_id") val merchantID: String = "",
    @SerializedName("refresh_token") val refreshToken: String = "",
    @SerializedName("refresh_token_expires_at") val refreshTokenExpiresAt: String = ""
)

data class LocationResponse(
    @SerializedName("location") val location: Location = Location()
)

data class Location(
    @SerializedName("id") val locationID: String = "",
    @SerializedName("name") val locationName: String = "",
    @SerializedName("address") val locationAddress: Address = Address(),
    @SerializedName("timezone") val locationTimezone: String = "",
    @SerializedName("capabilities") val locationCapabilities: List<String> = listOf(),
    @SerializedName("status") val locationStatus: String = "",
    @SerializedName("created_at") val locationCreatedAt: String = "",
    @SerializedName("merchant_id") val locationMerchantID: String = "",
    @SerializedName("country") val locationCountry: String = "",
    @SerializedName("language_code") val locationLanguageCode: String = "",
    @SerializedName("currency") val locationCurrency: String = "",
    @SerializedName("phone_number") val locationPhoneNumber: String = "",
    @SerializedName("business_name") val locationBusinessName: String = ""
)

data class Address(
    @SerializedName("address_line_1") val addressLine1: String = "",
    @SerializedName("locality") val locality: String = "",
    @SerializedName("administrative_district_level_1") val administrativeDistrictLevel1: String = "",
    @SerializedName("postal_code") val postalCode: String = "",
    @SerializedName("country") val country: String = ""
)

data class Merchant(
    @SerializedName("id") val merchantID: String = "",
    @SerializedName("business_name") val businessName: String = "",
    @SerializedName("country") val country: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("main_location_id") val mainLocationID: String = "",
    @SerializedName("created_at") val createdAt: String = "",
)

data class RevokeResponse(
    @SerializedName("success") val success: Boolean = false
)

data class Error(
    @SerializedName("error") val error: String = "",
    @SerializedName("error_description") val errorDescription: String = ""
)

fun generateCsrfToken(): String {
    return SecureRandom.getInstanceStrong().nextLong().toString()
}

fun generateCodeVerifier(): String {
    val sr = SecureRandom()
    val code = ByteArray(32)
    sr.nextBytes(code)
    return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}

fun generateCodeChallenge(codeVerifier: String): String {
    val bytes: ByteArray = codeVerifier.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    md.update(bytes, 0, bytes.size)
    val digest = md.digest()
    return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}

object RetrofitClient {
    private const val BASE_URL = "https://connect.squareup.com/"
    const val PERMS =
        "MERCHANT_PROFILE_READ PAYMENTS_WRITE PAYMENTS_WRITE_IN_PERSON PAYMENTS_WRITE_ADDITIONAL_RECIPIENTS ORDERS_READ ORDERS_WRITE"
    const val REDIRECT_URL = "https://brokenartist.me/square"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {

    var codeVerifier: String = generateCodeVerifier()

    fun resetCodeVerifier() {
        codeVerifier = generateCodeVerifier()
    }

    val squareApi: SquareApi by lazy {
        RetrofitClient.retrofit.create(SquareApi::class.java)
    }
}

interface SquareApi {
    @POST("oauth2/token")
    suspend fun getToken(
        @Query("client_id") clientID: String,
        @Query("grant_type") grantType: String = "authorization_code",
        @Query("redirect_uri") redirectUrl: String,
        @Query("code") code: String,
        @Query("code_verifier") codeVerifier: String
    ): Response<Token>

    @POST("oauth2/token")
    suspend fun refreshToken(
        @Query("client_id") clientID: String,
        @Query("grant_type") grantType: String = "refresh_token",
        @Query("refresh_token") refreshToken: String
    ): Response<Token>

    @POST("oauth2/revoke")
    suspend fun revokeToken(
        @Header("Authorization") secret: String,
        @Query("access_token") token: String,
        @Query("client_id") clientID: String
    ): Response<RevokeResponse>
    /**
     * Get the merchant's location by ID.
     */
    @GET("v2/locations/{location_id}")
    suspend fun getLocation(
        @Header("Authorization") token: String,
        @Path("location_id") locationID: String
    ): Response<LocationResponse>

    /**
     * Get the merchant's main location.
     */
    @GET("v2/locations/main")
    suspend fun getLocation(
        @Header("Authorization") token: String,
    ): Response<LocationResponse>

    @GET("v2/merchants")
    suspend fun getMerchant(
        @Header("Authorization") token: String,
        @Query("location_id") locationID: String
    ): Response<Merchant>
}