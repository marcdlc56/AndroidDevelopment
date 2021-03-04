package com.marcdelacruz.mealsarefun

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class DishType(title: String, items:List<Recipe>):ExpandableGroup<Recipe>(title,items) {

}