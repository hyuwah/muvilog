package dev.hyuwah.dicoding.muvilog.favoritesconsumer

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import dev.hyuwah.dicoding.muvilog.favoritesconsumer.model.CursorHelper
import dev.hyuwah.dicoding.muvilog.favoritesconsumer.model.FavoriteMovie
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val AUTHORITY = "dev.hyuwah.dicoding.muvilog.provider"
        private const val BASE_PATH = "favorites"
    }

    private val movieData = mutableListOf<FavoriteMovie>()
    private lateinit var adapter : FavoritesAdapter

    private val contentUri = Uri.Builder()
        .scheme("content")
        .authority(AUTHORITY)
        .appendPath(BASE_PATH)
        .build()

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, contentUri, null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data?.let{
            movieData.clear()
            movieData.addAll(CursorHelper.convertToListFavorite(data))
            adapter.submitList(movieData)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        movieData.clear()
        adapter.submitList(movieData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = FavoritesAdapter()
        rv_favorite.adapter = adapter

        supportLoaderManager.initLoader(100, null, this)
    }

    override fun onResume() {
        super.onResume()
        supportLoaderManager.restartLoader(100, null, this)
    }
}
