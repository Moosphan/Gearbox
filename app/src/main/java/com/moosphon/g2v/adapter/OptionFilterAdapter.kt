package com.moosphon.g2v.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.moosphon.g2v.R
import com.moosphon.g2v.base.CommonViewHolder
import com.moosphon.g2v.model.GifArgumentTag
import com.moosphon.g2v.model.SectionHeader
import com.moosphon.g2v.util.inflateView
import com.moosphon.g2v.widget.FilterChipView

/**
 * Adapter for argument filter configs.
 */
class OptionFilterAdapter : ListAdapter<Any, CommonViewHolder>(ArgumentFilterDiff) {

    private var mItemClickListener:
            ((View, Int) -> Unit)? = null       // callback of item click event

    companion object {
        private const val VIEW_TYPE_CATEGORY_HEADER = R.layout.item_category_section_header
        private const val VIEW_TYPE_OPTION_TAG = R.layout.item_option_tag

        /**
         * Inserts category headings in a list of [GifArgumentTag]s to make a heterogeneous list.
         * Assumes the items are already sorted by the value of [GifArgumentTag.getFilterCategory],
         * with items belonging to [GifArgumentTag.GifArgumentCategory.NONE] first.
         */
        private fun insertCategoryHeadings(list: List<GifArgumentTag>?): List<Any> {
            val newList = mutableListOf<Any>()
            var previousCategory: GifArgumentTag.GifArgumentCategory =
                GifArgumentTag.GifArgumentCategory.NONE
            list?.forEach {
                val category = it.getFilterCategory()
                if (category != previousCategory && category != GifArgumentTag.GifArgumentCategory.NONE) {
                    newList += SectionHeader(
                        titleId = category.labelResId,
                        useHorizontalPadding = false
                    )
                }
                newList.add(it)
                previousCategory = category
            }
            return newList
        }
    }

    /** Prefer this method instead of  [submitList] to add category headings. */
    fun submitArgumentList(list: List<GifArgumentTag>?) {
        super.submitList(insertCategoryHeadings(list))
    }

    // handle single checked in a category group.

    /**
     * A category group can only have one checked filter tag.
     * e.g. We can only set fixed angle for rotation.
     */
    fun applySingleCheckedInCategoryGroup(category: GifArgumentTag.GifArgumentCategory) {

    }

    fun getSpanSize(position: Int): Int {
        return if (getItem(position) is GifArgumentTag.TagFilter) 1 else 3
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
            is CategoryHeaderViewHolder -> bindCategoryHeaderViewHolder(holder, position)
            is OptionViewHolder -> bindOptionTagViewHolder(holder, position)
        }
    }

    private fun bindCategoryHeaderViewHolder(holder: CategoryHeaderViewHolder, position: Int) {
        holder.setText(
            R.id.filterHeaderText,
            holder.getContext().getString((getItem(position) as SectionHeader).titleId)
        )
    }

    private fun bindOptionTagViewHolder(holder: OptionViewHolder, position: Int) {

        val data = getItem(position) as GifArgumentTag
        val chip = holder.findView<FilterChipView>(R.id.filter_chip)
        chip.text = data.getText()
        chip.color = Color.parseColor(data.getColor())
        mItemClickListener?.let {
            holder.itemView.setOnClickListener {
                mItemClickListener!!(it, position)
            }
        }
    }

    /**
     * A item click event callback for rv.
     */
    fun setOnItemClickCallback(callback: (view: View, position: Int) -> Unit) {
        mItemClickListener = callback
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

internal class OptionFilterSpanSizeLookup(private val adapter: OptionFilterAdapter) :
    GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int) = adapter.getSpanSize(position)

}