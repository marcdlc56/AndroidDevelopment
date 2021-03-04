package com.marcdelacruz.mealsarefun

import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder

class DishTypeViewHolder (itemView: View): GroupViewHolder(itemView) {
    var dishTypeTextView: TextView = itemView.findViewById(R.id.textView)

    fun bind(dishType: DishType){
        dishTypeTextView.text = dishType.title
    }

    var arrow: ImageView = itemView.findViewById(R.id.arrow)
    override fun expand(){
        animateExpand()
    }

    override fun collapse(){
        animateCollapse()
    }

    fun animateExpand(){
        var rotate = RotateAnimation(360f, 180f, RELATIVE_TO_SELF, 0.5f,
        RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        arrow.animation = rotate
    }

    fun animateCollapse(){
        var rotate = RotateAnimation(180f, 360f, RELATIVE_TO_SELF,
        0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        arrow.animation = rotate

    }

}
