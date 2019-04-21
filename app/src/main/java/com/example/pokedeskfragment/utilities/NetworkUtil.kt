package com.example.pokedeskfragment.utilities

import android.net.Uri
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class NetworkUtil{
    companion object {
        @JvmStatic val POKE_API_BASE_URL : String = "https://pokeapi.co/api/v2/pokemon/?offset=0&limit=1000"

        @JvmStatic fun buiURL() : URL{
            var uri : Uri = Uri.parse(NetworkUtil.POKE_API_BASE_URL).buildUpon().build()

            lateinit var url : URL

            try {
                url = URL(uri.toString())
            } catch (e: MalformedURLException){
                e.printStackTrace()
            }

            return url
        }

        @JvmStatic fun getUrl(url : String) : URL{
            var uri : Uri = Uri.parse(url).buildUpon().build()

            lateinit var url : URL

            try {
                url = URL(uri.toString())
            } catch (e: MalformedURLException){
                e.printStackTrace()
            }

            return url
        }

        @JvmStatic fun getResponseFromHttpUrl(url: URL) : String? {
            var urlConnection : HttpURLConnection = url.openConnection() as HttpURLConnection
            try {
                var ing : InputStream = urlConnection.inputStream

                var scanner : Scanner = Scanner(ing)
                scanner.useDelimiter("\\A")

                var hasInput : Boolean = scanner.hasNext()

                if(hasInput){
                    return scanner.next()
                } else{
                    return null
                }

            } finally {
                urlConnection.disconnect()
            }
        }
    }
}