package com.marcdelacruz.mealsarefun

import android.view.View
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class RecipeViewHolder(itemView: View): ChildViewHolder(itemView)
{
    var recipeTextView: TextView = itemView.findViewById(R.id.textView)
    fun bind(recipe: Recipe){
        recipeTextView.text = recipe.recipeTitle
    }
}