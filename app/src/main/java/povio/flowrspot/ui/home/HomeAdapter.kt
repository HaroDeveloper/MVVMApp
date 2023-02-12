package povio.flowrspot.ui.home

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import povio.flowrspot.R
import povio.flowrspot.data.model.Flower
import povio.flowrspot.data.model.HomeItem
import povio.flowrspot.databinding.HomeItemFlowerBinding
import povio.flowrspot.databinding.HomeItemLoadingBinding
import povio.flowrspot.utils.diffutils.FlowerDiffUtil
import povio.flowrspot.utils.prefixHttp
import povio.flowrspot.utils.px

class HomeAdapter(var recyclerView: RecyclerView?) :
    RecyclerView.Adapter<HomeAdapter.ViewHolderHome>() {

    private val homeItems: MutableList<HomeItem> = mutableListOf()
    var currentDeltay = 0
    private var previousDeltaY = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHome {
        lateinit var viewHolder: ViewHolderHome
        when (viewType) {
            LOADING -> {
                val binding = HomeItemLoadingBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                viewHolder = ViewHolderLoading(binding)
            }

            FLOWER_ITEM -> {
                val binding = HomeItemFlowerBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                viewHolder = ViewHolderFlower(binding)
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return homeItems.size
    }

    override fun onBindViewHolder(holder: ViewHolderHome, position: Int) {
        if (holder is ViewHolderFlower) holder.bindData(homeItems[position] as HomeItem.FlowerItem)
    }

    override fun getItemViewType(position: Int): Int {
        return if (homeItems[position] is HomeItem.LoadingItem) LOADING
        else FLOWER_ITEM
    }

    fun addFlowers(flowers: List<Flower>) {
        if (isLastItemLoading()) removeItemLoading()
        val oldList: MutableList<HomeItem> = mutableListOf()
        oldList.addAll(homeItems)
        homeItems.clear()
        homeItems.addAll(flowers.map { HomeItem.FlowerItem(it) })

        val diffResult = DiffUtil.calculateDiff(FlowerDiffUtil(oldList, homeItems))
        diffResult.dispatchUpdatesTo(this)

        keepRecyclerYPosition()
    }

    fun addLoadingItem() {
        if (!isLastItemLoading()) {
            homeItems.add(HomeItem.LoadingItem)
            notifyItemInserted(homeItems.size - 1)
        }
    }

    fun keepRecyclerYPosition() {
        recyclerView?.scrollBy(0, currentDeltay - previousDeltaY)
        previousDeltaY = currentDeltay
    }

    fun updateRecyclerDeltaY(deltaY: Int) {
        currentDeltay = deltaY
    }

    private fun isLastItemLoading(): Boolean {
        return if (homeItems.size > 0) {
            homeItems[homeItems.size - 1] is HomeItem.LoadingItem
        } else false
    }

    private fun removeItemLoading() {
        if (homeItems.size > 0) {
            val oldList: MutableList<HomeItem> = mutableListOf()
            oldList.addAll(homeItems)
            homeItems.clear()
            homeItems.addAll(oldList)
            removeLoadingItemsFromList()

            val diffResult = DiffUtil.calculateDiff(FlowerDiffUtil(oldList, homeItems))
            diffResult.dispatchUpdatesTo(this)
        }
    }

    private fun removeLoadingItemsFromList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            homeItems.removeIf { homeItem -> homeItem is HomeItem.LoadingItem }
        } else {
            val iterator = homeItems.iterator()
            while (iterator.hasNext()) {
                val homeItem = iterator.next()
                if (homeItem is HomeItem.LoadingItem) {
                    iterator.remove()
                }
            }
        }
    }

    class ViewHolderFlower(private val binding: HomeItemFlowerBinding) :
        ViewHolderHome(binding.root) {

        private var options = RequestOptions()
        fun bindData(item: HomeItem.FlowerItem) {
            with(binding) {
                val flower = item.flower
                flowerName.text = flower.name
                flowerLatinName.text = flower.latinName

                sightingsCount.text = String.format(
                    itemView.context.resources.getQuantityString(
                        R.plurals.sightings_number,
                        flower.sightings,
                        flower.sightings
                    ),
                    flower.sightings
                )

                Glide.with(itemView.context)
                    .load(R.drawable.ic_star)
                    .circleCrop()
                    .into(favoriteImage)

                options = options.transform(CenterCrop(), RoundedCorners(3.px))
                Glide.with(itemView.context).load(flower.profilePicture.prefixHttp())
                    .apply(options)
                    .transition(
                        DrawableTransitionOptions.withCrossFade()
                    ).into(flowerImage)

            }

        }
    }

    open class ViewHolderHome(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolderLoading(binding: HomeItemLoadingBinding) : ViewHolderHome(binding.root)

    companion object HomeViewType {
        const val LOADING = 0
        const val FLOWER_ITEM = 1
    }
}
