package povio.flowrspot.utils.diffutils

import androidx.recyclerview.widget.DiffUtil
import povio.flowrspot.data.model.HomeItem

class FlowerDiffUtil(
    private val oldList: MutableList<HomeItem>,
    private val newList: MutableList<HomeItem>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}