package com.uvk.shramapplication.ui.expandNav


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.uvk.shramapplication.R

class ExpandableListAdapter(
    private val context: Context,
    private val listGroupTitles: List<String>,
    private val listChildData: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return listGroupTitles.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listChildData[listGroupTitles[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return listGroupTitles[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listChildData[listGroupTitles[groupPosition]]?.get(childPosition) ?: ""
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean, convertView: View?,
        parent: ViewGroup
    ): View? {
        val groupTitle = getGroup(groupPosition) as String

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.nav_list, parent, false)

        val listGroupTitle = view.findViewById<TextView>(R.id.list_group_title)
        val arrowIcon = view.findViewById<ImageView>(R.id.arrow_icon)
        val icon = view.findViewById<ImageView>(R.id.icon)

        listGroupTitle.text = groupTitle

        // Change the group icon based on group position (or any other logic)
        when (groupPosition) {
            0 -> icon.setImageResource(R.drawable.baseline_home_24) // Set icon for group 0 (Home)
            1 -> icon.setImageResource(R.drawable.baseline_post_add_24) // Set icon for group 1 (Settings)
            2 -> icon.setImageResource(R.drawable.baseline_person_24) // Set icon for group 2 (Help)
            3 -> icon.setImageResource(R.drawable.baseline_list_alt_24) // Set icon for group 2 (Help)
            4 -> icon.setImageResource(R.drawable.baseline_home_24) // Set icon for group 2 (Help)
            5 -> icon.setImageResource(R.drawable.baseline_logout_24) // Set icon for group 2 (Help)
            else -> icon.setImageResource(R.drawable.baseline_home_24) // Default icon
        }

        // Rotate the arrow based on expanded/collapsed state
        // Check if the groupTitle is empty or null and hide the arrow icon accordingly
        if (groupTitle.isNullOrEmpty() ||
            groupTitle == "Post" ||
            groupTitle == "Profile" ||
            groupTitle == "Log Out") {
            // Hide the arrowIcon if groupTitle is one of these specific values
            arrowIcon.visibility = View.GONE
        } else {
            // Show and rotate the arrow based on expanded/collapsed state
            arrowIcon.visibility = View.VISIBLE
            if (isExpanded) {
                arrowIcon.rotation = 180f // Point upwards
            } else {
                arrowIcon.rotation = 0f // Point downwards
            }
        }


        return view
    }

  /*  @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        val groupTitle = getGroup(groupPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_group, parent, false)

        val listGroupTitle = view.findViewById<TextView>(R.id.list_group_title)
        val arrowIcon = view.findViewById<ImageView>(R.id.arrow_icon)
        val icon = view.findViewById<ImageView>(R.id.icon)

        listGroupTitle.text = groupTitle

        // Change the group icon based on group position (or any other logic)
        when (groupPosition) {
            0 -> icon.setImageResource(R.drawable.icon_home) // Set icon for group 0 (Home)
            1 -> icon.setImageResource(R.drawable.icon_settings) // Set icon for group 1 (Settings)
            2 -> icon.setImageResource(R.drawable.icon_help) // Set icon for group 2 (Help)
            else -> icon.setImageResource(R.drawable.icon_default) // Default icon
        }

        // Rotate the arrow based on expanded/collapsed state
        if (isExpanded) {
            arrowIcon.rotation = 180f // Point upwards
        } else {
            arrowIcon.rotation = 0f // Point downwards
        }

        return view
    }*/

    override fun getChildView(
        groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?,
        parent: ViewGroup
    ): View? {
        val childText = getChild(groupPosition, childPosition) as String

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)

        val listChild: TextView = view.findViewById(android.R.id.text1)
        listChild.text = childText
        listChild.setPadding(50, 0, 0, 0) // Indent child items

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
