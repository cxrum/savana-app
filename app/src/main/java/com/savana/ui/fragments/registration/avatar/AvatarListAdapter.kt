package com.savana.ui.fragments.registration.avatar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import com.savana.R
import com.savana.domain.models.AvatarData

class AvatarListAdapter(
    context: Context,
    private val avatars: List<AvatarData>
): ArrayAdapter<AvatarData>(context, R.layout.view_avatar, avatars) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.view_avatar, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val avatarUrl = getItem(position)?.url

        if (avatarUrl != null) {
            viewHolder.avatarImage.load(avatarUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_image_placeholder)
                transformations(CircleCropTransformation())
            }
        }

        return view
    }

    private class ViewHolder(view: View) {
        val avatarImage: ImageView = view.findViewById(R.id.avatar_image)
    }
}