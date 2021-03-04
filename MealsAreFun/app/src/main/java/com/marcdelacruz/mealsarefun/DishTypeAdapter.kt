package com.marcdelacruz.mealsarefun

import android.view.LayoutInflater
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class DishTypeAdapter (groups: List<ExpandableGroup<*>>, var mactivity: MainActivity) :
    ExpandableRecyclerViewAdapter<DishTypeViewHolder, RecipeViewHolder>(groups)
{
    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): DishTypeViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.expandable_recyclerview_dishtype, parent, false)
        return DishTypeViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): RecipeViewHolder {
        var view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.expandable_recyclerview_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindChildViewHolder(
        holder: RecipeViewHolder?, flatPosition: Int,
        group: ExpandableGroup<*>?, childIndex: Int)

    {
        var recipe = group?.items?.get(childIndex) as Recipe
        holder?.bind(recipe)
        holder?.recipeTextView?.setOnClickListener { _ ->
            mactivity.launchRecipe(recipe)
        }

    }

    override fun onBindGroupViewHolder(
        holder: DishTypeViewHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?)

    {
        var dishType = group as DishType
        holder?.bind(dishType)
    }

}