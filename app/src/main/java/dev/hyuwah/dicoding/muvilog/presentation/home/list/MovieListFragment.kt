package dev.hyuwah.dicoding.muvilog.presentation.home.list


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import dev.hyuwah.dicoding.muvilog.R
import dev.hyuwah.dicoding.muvilog.presentation.detail.MovieDetailActivity
import dev.hyuwah.dicoding.muvilog.presentation.home.adapter.MoviesAdapter
import dev.hyuwah.dicoding.muvilog.presentation.model.MovieItem
import dev.hyuwah.dicoding.muvilog.presentation.model.base.Resource
import dev.hyuwah.dicoding.muvilog.utils.EspressoIdlingResource
import kotlinx.android.synthetic.main.fragment_movie_list.*
import kotlinx.android.synthetic.main.row_main_movies.view.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class MovieListFragment : DaggerFragment() {

    @Inject
    lateinit var viewModel: MovieListViewModel
    private lateinit var adapter: MoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MoviesAdapter { movieItem, itemView ->
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(itemView.iv_list_poster, "transition_test")
            )
            val intents = Intent(requireActivity(), MovieDetailActivity::class.java)
            intents.putExtra(MovieDetailActivity.MOVIE_KEY, movieItem)
            startActivity(intents, options.toBundle())
        }

        rv_movie_list.layoutManager = LinearLayoutManager(requireContext())
        rv_movie_list.adapter = adapter

        viewModel.state.observe(this, ::updateUI)
        if (savedInstanceState==null) viewModel.load()

        srl_movie_list.setOnRefreshListener { viewModel.load() }
    }

    private fun updateUI(resource: Resource<List<MovieItem>>){
        showNoInternetView(false)
        when(resource){
            is Resource.Loading -> {
                EspressoIdlingResource.increment()
                showLoading(true)
            }
            is Resource.Success -> {
                EspressoIdlingResource.decrement()
                showLoading(false)
                adapter.setMovieList(resource.data)
            }
            is Resource.Failure -> {
                EspressoIdlingResource.decrement()
                showLoading(false)
                showNoInternetView(true)
                toast("Error ${resource.throwable.localizedMessage}")
            }
        }
    }

    private fun showNoInternetView(toggle: Boolean){
        tv_no_internet.visibility = if(toggle) View.VISIBLE else View.GONE
    }

    private fun showLoading(toggle: Boolean){
        srl_movie_list.isRefreshing = toggle
    }

}
