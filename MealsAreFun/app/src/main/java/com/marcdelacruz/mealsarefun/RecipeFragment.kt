package com.marcdelacruz.mealsarefun

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.w3c.dom.Text


class RecipeFragment(var mainActivity: MainActivity) : Fragment() {

    var recipeTitleTextView: TextView? = null
    var dishTypeTextView: TextView? = null
    var enteredByTextView: TextView? = null
    var recipeCategoriesTextView: TextView? = null
    var ingredientsTextView: TextView? = null
    var recipeInstructionsTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_recipe, container, false)
        recipeTitleTextView = view.findViewById<TextView>(R.id.recipeTitleTextView)
        dishTypeTextView = view.findViewById<TextView>(R.id.dishTypeTextView)
        enteredByTextView = view.findViewById<TextView>(R.id.enteredByTextView)
        ingredientsTextView = view.findViewById<TextView>(R.id.ingredientsTextView)
        ingredientsTextView?.movementMethod = ScrollingMovementMethod()
        recipeInstructionsTextView?.movementMethod = ScrollingMovementMethod()
        recipeInstructionsTextView = view.findViewById<TextView>(R.id.recipeInstructionsTextView)
        recipeCategoriesTextView = view.findViewById<TextView>(R.id.recipeCategoriesTextView)

        var recipe:Recipe = mainActivity.activeRecipe!!
        Log.d("RecipeFragment", recipe.recipeTitle!!)
        recipeTitleTextView?.text = recipe.recipeTitle!!
        dishTypeTextView?.text = recipe.dishType!!
        enteredByTextView?.text = recipe.enteredBy!!
        recipeCategoriesTextView?.text = recipe.recipeCategories.toString()
        ingredientsTextView?.text = recipe.getFormattedIngredients()
        recipeInstructionsTextView?.text = recipe.recipeInstructions!!

        return view
    }

}