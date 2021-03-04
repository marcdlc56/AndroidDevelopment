package com.marcdelacruz.mealsarefun

import android.os.Parcel
import android.os.Parcelable
import java.lang.StringBuilder

class Recipe(): Parcelable {

    var recipeTitle:String? = null
    var dishType: String? = null
    var enteredBy: String? = null
    var recipeInstructions: String? = null
    var recipeCategories:ArrayList<String>? = null
    var ingredients:ArrayList<Map<String,Any>>? = null

    constructor(parcel: Parcel) : this() {
    }


    constructor(name:String) : this() {
        this.recipeTitle = name
    }

    constructor(rt: String, dt:String, eb:String, ri:String,
                rc:ArrayList<String>, i:ArrayList<Map<String,Any>>): this(){
        this.recipeTitle = rt
        this.dishType = dt
        this.enteredBy = eb
        this.recipeInstructions = ri
        this.recipeCategories = rc
        this.ingredients = i

    }


    fun getFormattedIngredients():String{
        var iString: StringBuilder = java.lang.StringBuilder()
        ingredients?.forEach { group ->
            iString.append("\n")
            iString.append(group["igroupName"]).append("\n")
            var inArray: ArrayList<Map<String,String>> = group["ingredients"]
                as ArrayList<Map<String, String>>
            inArray.forEach{ingredient ->
                iString.append(ingredient["qty"]).append(" ").append(ingredient["uom"])
                    .append(" ").append(ingredient["ingredient"]).append("\n")
            }
        }
        return iString.toString()
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }

}