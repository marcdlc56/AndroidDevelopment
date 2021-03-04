package com.danmcdonald.triviahouse

import java.io.Serializable

class Room : Serializable{
    var answer: String? = null
    var attendant:String? = null
    var give: String? = null
    var look: String? = null
    var move: String?  = null
    var name: String? = null
    var use: String? = null

    fun canMove(to: String):Boolean?{
        val doors:List<String>? = move?.split("|")
        return doors?.contains(to)
    }

}