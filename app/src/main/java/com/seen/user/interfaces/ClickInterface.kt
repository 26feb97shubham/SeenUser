package com.seen.user.interfaces

import android.view.View
import org.json.JSONArray
import org.json.JSONObject

interface ClickInterface {
    interface ClickPositionInterface{
        fun clickPostion(pos:Int)
    }
    interface ClickPosInterface{
        fun clickPostion(pos:Int, type:String)
    }
    interface ClickPosTypeInterface{
        fun clickPostionType(pos:Int, type:String)
    }
    interface ClickArrayInterface{
        fun clickArray(idArray: JSONArray)
    }
    interface ClickJSonObjInterface{
        fun clickJSonObj(obj1: JSONObject)
    }
    interface ClickPosItemViewInterface{
        fun clickPosItemView(pos:Int, itemView: View)
    }
}