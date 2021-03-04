package com.marcdelacruz.mealsarefun

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_recycler_view.*


class RecyclerViewFragment (var mainActivity: MainActivity): Fragment() {

    private val TAG: String = "RecyclerViewFragment"
    val db = FirebaseFirestore.getInstance()

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
        var view:View = inflater.inflate(R.layout.fragment_recycler_view, container, false)

        var recyclerview = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(mainActivity)

        var prevDishType = "begin"
        var recipes = ArrayList<Recipe>()
        var dishTypes = ArrayList<DishType>()
        var dishType:String = ""

        db.collection("world")
            .orderBy("dishType")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var data = document.data
                    dishType = data.getValue("dishType") as String

                    if(prevDishType == dishType){
                        recipes.add(
                            Recipe( data.getValue("recipeTitle")as String,
                                data.getValue("dishType") as String,
                                data.getValue("enteredByEmail") as String,
                                data.getValue("recipeInstructions") as String,
                                data.getValue("recipeCategories") as ArrayList<String>,
                                data.getValue("ingredientGroups") as ArrayList<Map<String, Any>>
                        )

                        )
                    } else{
                        if(prevDishType == "begin"){
                            prevDishType = dishType
                            Recipe( data.getValue("recipeTitle")as String,
                                data.getValue("dishType") as String,
                                data.getValue("enteredByEmail") as String,
                                data.getValue("recipeInstructions") as String,
                                data.getValue("recipeCategories") as ArrayList<String>,
                                data.getValue("ingredientGroups") as ArrayList<Map<String, Any>>
                            )

                        } else {
                            dishTypes.add(DishType(prevDishType, recipes))
                            prevDishType = dishType;
                            recipes = ArrayList<Recipe>()
                            Recipe( data.getValue("recipeTitle")as String,
                                data.getValue("dishType") as String,
                                data.getValue("enteredByEmail") as String,
                                data.getValue("recipeInstructions") as String,
                                data.getValue("recipeCategories") as ArrayList<String>,
                                data.getValue("ingredientGroups") as ArrayList<Map<String, Any>>
                            )

                        }
                    }

                }
                dishTypes.add(DishType(dishType, recipes))
                var adapter = DishTypeAdapter(dishTypes, mainActivity)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener{exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        return view

    }


}