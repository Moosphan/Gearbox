package com.moosphon.g2v.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moosphon.g2v.MainActivity
import com.moosphon.g2v.R
import com.moosphon.g2v.adapter.OptionFilterAdapter
import com.moosphon.g2v.adapter.OptionFilterSpanSizeLookup
import com.moosphon.g2v.model.GifArgumentTag
import com.moosphon.g2v.model.data.GifArgumentTagDataSource
import com.moosphon.g2v.util.*
import com.moosphon.g2v.widget.BottomSheetBehavior
import com.moosphon.g2v.widget.BottomSheetBehavior.Companion.STATE_COLLAPSED
import com.moosphon.g2v.widget.BottomSheetBehavior.Companion.STATE_HIDDEN
import com.moosphon.g2v.widget.FilterChipView
import kotlinx.android.synthetic.main.fragment_gif_arguments_config.*

/**
 * Fragment to display gif configuration arguments like a bottom sheet dialog.
 * todo: 自定义输出预览尺寸需要收费：e.g. DefaultStrategy.exact(1080, 720).build()
 */
class GifConfigurationFragment : Fragment() {

    private val filterAdapter: OptionFilterAdapter by lazy {
        OptionFilterAdapter()
    }

    private lateinit var behavior: BottomSheetBehavior<*>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_gif_arguments_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnApplyWindowInsetsListener { v, insets ->
            // During fragment transitions, multiple fragment's view hierarchies can be added at the
            // same time. If one consumes window insets, the other might not be layed out properly.
            // To workaround that, make sure we dispatch the insets to all children, regardless of
            // how they are consumed.
            (v as? ViewGroup)?.forEach { child ->
                child.dispatchApplyWindowInsets(insets)
            }
            insets
        }

        filterRecyclerView.apply {
            adapter = filterAdapter
            setHasFixedSize(true)
            (layoutManager as GridLayoutManager).spanSizeLookup =
                OptionFilterSpanSizeLookup(filterAdapter)

            // when scroll from bottom -> top, header should change state.
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    topBar.isActivated = recyclerView.canScrollVertically(-1)
                }
            })
            // if this device has navigation bar,
            // we should reserve space to display bottom sheet well when expanded.
            // So, we pad the bottom of the RecyclerView so that the content scrolls up above the nav bar
            updatePaddingRelative(
                bottom = paddingBottom + ScreenUtils.getNavigationBarHeight(requireContext())
            )
        }

        // get callback when argument changed.
        filterAdapter.setOnArgumentChangedListener {
                arguments,
                position,
                isSameCategory  ->
            filterReset.applyViewGone(arguments.isEmpty())
            if (isSameCategory) {
                // need to apply unselected state to last filter tag.
                val currentItem = filterAdapter.currentList[position] as GifArgumentTag
                applyUnselectedState(arguments[currentItem.getFilterCategory()]!!.lastPosition)
            }

            // respond callback to activity
            (requireActivity() as MainActivity).onSelectedArgumentsReceived(arguments)
        }

        // handle click events
        filterClose.setOnClickListener {
            behavior.state = if (behavior.skipCollapsed) STATE_HIDDEN else STATE_COLLAPSED
        }

        filterReset.setOnClickListener {
            resetFilterState()
        }

        loadTags()
    }

    @SuppressLint("NewApi")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // todo: 访问父activity的view层的时候需要在 onActivityCreated 方法里面,，为何filterSheetContainer为空？
        /*view?.let { parent ->
            behavior = BottomSheetBehavior.from(parent.findViewById(R.id.filterSheetContainer))
        }*/

        // because of behavior is from activity, so we should get it after activity created.
        behavior = (requireActivity() as MainActivity).bottomSheetBehavior
        behavior.state = STATE_HIDDEN
        val peekHeight = behavior.peekHeight
        val marginBottom = view?.marginBottom
        // Update the peek and margins so that it scrolls and rests within sys ui
        // todo: why not worked? did not arrived here
        view?.doOnApplyWindowInsets { v, insets, _ ->
            val gestureInsets = insets.systemGestureInsets
            // Update the peek height so that it is above the navigation bar
            behavior.peekHeight = gestureInsets.bottom + peekHeight

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = marginBottom!! + insets.systemWindowInsetTop
            }
        }


    }

    /**
     * load argument data from repository.
     */
    private fun loadTags() {
        val data = GifArgumentTagDataSource.loadArgumentData(requireContext())
        val newData = ArrayList<GifArgumentTag.TagFilter>()
        data.forEach { tag ->
            val gifTag = GifArgumentTag.TagFilter(tag, false)
            newData.add(gifTag)
        }
        filterAdapter.submitArgumentList(newData)
    }

    /**
     * Reset the state of last single tag in a category group.
     */
    private fun applyUnselectedState(lastIndex: Int) {
        // reset the last selected tag data in a category group
        (filterAdapter.currentList[lastIndex] as GifArgumentTag).isChecked = false
        val lastSelectedChip = filterRecyclerView.getChildAt(lastIndex) as FilterChipView
        lastSelectedChip.animateCheckedAndInvoke(false) {
        }
    }


    /**
     * Reset all states to unselected from tags.
     */
    private fun resetFilterState() {
        filterReset.applyViewGone(true)
        filterAdapter.resetSelectedOptions()
        filterRecyclerView.forEach {child ->
            child.findViewById<FilterChipView>(R.id.filter_chip)?.let { filterView ->
                if (filterView.isChecked) {
                    filterView.animateCheckedAndInvoke(false) {
                        // reset the data in tag data group
                        val position = filterRecyclerView.layoutManager?.getPosition(filterView)
                        position?.let {
                            (filterAdapter.currentList[it] as GifArgumentTag).isChecked = false
                        }
                    }
                }
            }
        }
    }

}