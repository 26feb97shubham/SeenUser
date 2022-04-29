package com.seen.user.model

class Cart {
    var id:Int=0
    var user_id:Int=0
    var product_id:Int=0
    var supplier_id:Int=0
    var sold_out:Int=0
    var allow_coupans:Int=0
    var add_offer:Int=0
    var like:Boolean=false
    var product_available_status:Boolean=false
    var product_item_id:String=""
    var quantity:Int=0
    var category_name:String=""
    var product_name:String=""
    var supplier_name:String=""
    var supplier_image:String=""
    lateinit var product_available_message:String
    lateinit var price:String
    lateinit var discount:String
    lateinit var files:String
    lateinit var from_date:String
    lateinit var to_date:String
}