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

    private var mItemClickListener:             // callback of arguments changed event
            ((HashMap<GifArgumentTag.GifArgumentCategory, ArgumentRecord>, Int, Boolean) -> Unit)? = null

    private var mSelectedArguments:             // selected argument options that we choose
            HashMap<GifArgumentTag.GifArgumentCategory, ArgumentRecord> = HashMap()

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

        holder.itemView.setOnClickListener {
            selectFilter(chip, data, position)
        }
    }

    // handle single checked in a category group.

    /**
     * toggle filter tag into selected/unselected state.
     * we should do two things in single category:
     * 1. set new state animation
     * 2. delivery activity to run last tag's removing animation
     */
    private fun selectFilter(chipView: FilterChipView, data: GifArgumentTag, position: Int) {
        val isSame: Boolean // whether belongs to same category
        if (!data.isChecked) {
            if (mSelectedArguments.isNotEmpty() && mSelectedArguments.containsKey(data.getFilterCategory())) {
                isSame = true
                // update the latest value in a category
                mSelectedArguments[data.getFilterCategory()]?.value = data.getValue()
            } else {
                isSame = false
                mSelectedArguments[data.getFilterCategory()] = ArgumentRecord(data.getValue(), position)
            }
            mItemClickListener?.let {
                // Every category should have a `lastIndex`.
                mItemClickListener?.invoke(
                    mSelectedArguments,
                    position,
                    isSame
                )
            }
            // update the last selected index in a category.
            if (isSame) {
                mSelectedArguments[data.getFilterCategory()]?.lastPosition = position
            }

        } else {
            mSelectedArguments.remove(data.getFilterCategory())
            mItemClickListener?.let {
                // Every category should have a `lastIndex`.
                mItemClickListener?.invoke(
                    mSelectedArguments,
                    position,
                    false
                )
            }
        }

        // apply current new tag state
        val checked = !data.isChecked
        chipView.animateCheckedAndInvoke(checked) {
            data.isChecked = checked
        }
    }


    /**
     * clear all selected arguments
     */
    fun resetSelectedOptions() {
        if (mSelectedArguments.isNotEmpty()) {
            mSelectedArguments.clear()
        }
    }


    /**
     * A item click event callback for rv.
     */
    @Suppress("unused")
    fun setOnArgumentChangedListener(
        callback: (arguments: HashMap<GifArgumentTag.GifArgumentCategory, ArgumentRecord>,
                   position: Int,
                   isSame: Boolean) -> Unit) {

        mItemClickListener = callback
    }

    class CategoryHeaderViewHolder(header: View) : CommonViewHolder(header)

    class OptionViewHolder(itemView: View) : CommonViewHolder(itemView)


}

data class ArgumentRecord(
    var value: Float,       // current value
    var lastPosition: Int   // last position in single group
)

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