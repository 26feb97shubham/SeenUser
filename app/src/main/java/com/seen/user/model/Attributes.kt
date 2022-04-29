package com.seen.user.model

import org.json.JSONArray
import java.io.Serializable

class Attributes : Serializable {
    var id:Int=0
    var type:String=""
    var name:String=""
    var name_ar:String=""
    lateinit var value:JSONArray
}