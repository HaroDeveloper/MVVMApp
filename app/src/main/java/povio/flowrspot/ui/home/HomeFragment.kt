package povio.flowrspot.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import povio.flowrspot.R
import povio.flowrspot.databinding.FragmentHomeBinding
import povio.flowrspot.utils.decorators.HomeItemDecorator
import povio.flowrspot.utils.px

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by inject()
    lateinit var adapter: HomeAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        addCollectors()
    }

    private fun setAdapter() {
        adapter = HomeAdapter()
        binding.flowerRecycler.adapter = adapter

        val gridLayoutManager =
            GridLayoutManager(context, STANDARD_COLUMN_COUNT, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.getItemViewType(position) == HomeAdapter.LOADING) STANDARD_COLUMN_COUNT
                else COLUMN_COUNT_LOADING
            }
        }

        binding.flowerRecycler.layoutManager = gridLayoutManager
        binding.flowerRecycler.addItemDecoration(HomeItemDecorator(10.px))

        addRecyclerViewListener()
    }

    private fun addCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.state.collect {
                    it.let {
                        when (it) {
                            State.LAST_PAGE_REACHED -> removeRecyclerViewListener()
                            State.LOADING_NEXT_PAGE -> adapter.addLoadingItem()
                            State.FILTERING_DONE -> removeRecyclerViewListener()
                            State.FILTER_REMOVED -> addRecyclerViewListener()
                            State.LOADING_INIT -> showProgressBar()
                            State.PAGE_LOADED -> hideProgressBar()
                            State.FAILURE_NETWORK -> showNetworkError()
                            State.FAILURE_GENERIC -> showGenericError()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.flowers.collect {
                    adapter.addFlowers(it)
                }
            }
        }

    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isLastItemVisible(recyclerView))
                viewModel.nextPage()
        }
    }

    private fun addRecyclerViewListener() {
        binding.flowerRecycler.addOnScrollListener(scrollListener)
    }

    private fun removeRecyclerViewListener() {
        binding.flowerRecycler.removeOnScrollListener(scrollListener)
    }

    private fun isLastItemVisible(recyclerView: RecyclerView): Boolean {
        val position =
            (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
        return position >= adapter.itemCount - 1
    }

    private fun showGenericError() {
        hideProgressBar()
        Toast.makeText(
            requireContext(),
            getString(R.string.home_api_generic_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showNetworkError() {
        hideProgressBar()
        Toast.makeText(
            requireContext(),
            getString(R.string.home_api_network_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    companion object {
        const val STANDARD_COLUMN_COUNT = 2
        const val COLUMN_COUNT_LOADING = 1
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}