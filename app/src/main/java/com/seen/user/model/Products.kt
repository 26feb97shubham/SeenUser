package com.seen.user.model

class Products {
    var id:Int=0
    var user_id:Int=0
    var category_id:Int=0
    var allow_coupans:Int=0
    var add_offer:Int=0
    var discount:Int=0
    var delete_status:Int=0
    var quantity:Int=0
    var status:Int=0
    var rating:Int=0
    var name:String =""
    var from_date:String =""
    var to_date:String =""
    var files:String=""
    var price:String=""
    var category:String=""
    var category_name:String=""
    var modified:String=""
    var created:String=""
    var supplier_name:String=""
    var all_files:ArrayList<String>?=null
    var like:Boolean=false
    val attributes: String? = null
    val description: String? = null
}