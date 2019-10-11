package com.moosphon.g2v.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moosphon.g2v.R
import com.moosphon.g2v.base.CommonViewHolder
import com.moosphon.g2v.model.GifArgumentTag
import com.moosphon.g2v.model.SectionHeader
import com.moosphon.g2v.util.inflateView

/**
 * Adapter for argument filter configs.
 */
class OptionFilterAdapter : ListAdapter<Any, CommonViewHolder>(ArgumentFilterDiff) {

    companion object {
        private const val VIEW_TYPE_CATEGORY_HEADER = R.layout.item_category_section_header
        private const val VIEW_TYPE_OPTION_TAG = R.layout.item_option_tag
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
       return when(viewType) {
            VIEW_TYPE_CATEGORY_HEADER -> createCategoryViewHolder(parent)
            VIEW_TYPE_OPTION_TAG -> createOptionTagViewHolder(parent)

            else -> throw IllegalArgumentException("unknown type for item")
       }
    }

    private fun createCategoryViewHolder(parent: ViewGroup) : CategoryHeaderViewHolder {
        val view = parent.inflateView(R.layout.item_category_section_header)
        return CategoryHeaderViewHolder(view)
    }

    private fun createOptionTagViewHolder(parent: ViewGroup) : OptionViewHolder {
        val view = parent.inflateView(R.layout.item_option_tag)
        return OptionViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is SectionHeader -> VIEW_TYPE_CATEGORY_HEADER
            is GifArgumentTag -> VIEW_TYPE_OPTION_TAG
            else -> throw IllegalArgumentException("unknown type for item")
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {

        when(holder) {
            is CategoryHeaderViewHolder -> bindCategoryHeaderViewHolder(holder)
            is OptionViewHolder -> bindOptionTagViewHolder(holder)
        }
    }

    private fun bindCategoryHeaderViewHolder(holder: CategoryHeaderViewHolder) {

    }

    private fun bindOptionTagViewHolder(holder: OptionViewHolder) {

    }

    class CategoryHeaderViewHolder(header: View) : CommonViewHolder(header)

    class OptionViewHolder(itemView: View) : CommonViewHolder(itemView)


}

internal object ArgumentFilterDiff : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return (oldItem as? GifArgumentTag.TagFilter)?.isUiContentEqual(newItem as GifArgumentTag.TagFilter) ?: true
    }

}