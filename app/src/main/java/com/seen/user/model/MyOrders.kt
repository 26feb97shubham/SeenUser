package com.seen.user.model

import org.json.JSONArray

class MyOrders {
    var id:Int=0
    var order_id:String = ""
    var subtotal:String = ""
    var shipping_fee:String = ""
    var product_item_id:String = ""
    var taxes:String = ""
    var total_price:String = ""
    var delivery_date:String = ""
    var file:String = ""
    var status : Int = 0
    var supplier_id : Int = 0
    lateinit var order_data:JSONArray
}