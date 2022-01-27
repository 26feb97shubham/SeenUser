package com.seen.user.model

import org.json.JSONArray

class ProductList {
    var id:Int=0
    var supplier_id:Int=0
    var name:String = ""
    var supplier_name:String = ""
    var supplier_profile_picture:String=""
    var category_name:String=""
    var product_item_id:String=""
    var price:String=""
    var original_price:String=""
    var files:String=""
    lateinit var all_files : JSONArray
    var discount:String=""
    var rating:Double =0.0
    var quantity:Int=0
    var like:Boolean=false
    var status : Int = 0

}