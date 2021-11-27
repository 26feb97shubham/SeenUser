package com.dev.ecommerceuser.model

class ProductList {
    var id:Int=0
    lateinit var name:String
    lateinit var supplier_name:String
    lateinit var category_name:String
    lateinit var price:String
    lateinit var files:String
    lateinit var discount:String
    var rating:Double =0.0
    var quantity:Int=0
    var like:Boolean=false
    var status : Int = 0

}