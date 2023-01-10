package id.ac.sttccirebon.mahasiswa.ui.helper

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class DataManager(private val baseContext: Context) {

    private val SHARED_DATA = "loyalti_shared_preferences"
    var sharePref : SharedPreferences = baseContext.getSharedPreferences(SHARED_DATA, Context.MODE_PRIVATE)

    private fun editor(): SharedPreferences.Editor {
        return sharePref.edit()
    }

    fun putString(name: String, value: String) {
        editor().putString(name, value).apply()
    }

    fun putStringSet(name: String, value: Set<String>) {
        editor().putStringSet(name, value).apply()
    }

    fun putInt(name: String, value: Int) {
        editor().putInt(name, value).apply()
    }

    fun getString(name: String): String? {
        return sharePref.getString(name, null)
    }

    fun getStringSet(name: String): Set<String>? {
        return sharePref.getStringSet(name, null)
    }

    fun getInt(name: String): Int? {
        return sharePref.getInt(name, 0)
    }

    fun remove(name: String) {
        editor().remove(name).apply()
    }

    fun clear() {
        editor().clear().apply()
    }

    fun instance(): DataManager {
        return DataManager(baseContext)
    }

    fun putUserData(results: JSONObject) {
        instance().putString("token", results.getString("token"))
    }

    fun putProfile(fotoProfil: String) {
        instance().putString("photo_profile", fotoProfil)
    }

    fun putPhoto(imageString: String) {
        instance().putString("photo", imageString)
    }

    fun putLatitude(Latitude: String) {
        instance().putString("latitude", Latitude)
    }

    fun putLongitude(Longitude: String) {
        instance().putString("longitude", Longitude)
    }

    fun putUsername(User : String){
        instance().putString("user", User)
    }

}