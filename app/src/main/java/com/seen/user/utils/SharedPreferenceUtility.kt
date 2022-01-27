package com.seen.user.utils

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.seen.user.application.MyApp
import java.util.*
import java.util.regex.Pattern

class SharedPreferenceUtility {
    private val sharedPreferences: SharedPreferences

    private val editor: SharedPreferences.Editor
        get() = sharedPreferences.edit()

    init {
        instance = this
        sharedPreferences = MyApp.instance!!.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    }


    fun delete(key: String) {
        if (sharedPreferences.contains(key)) {
            editor.remove(key).commit()
        }
    }

    fun save(key: String, value: Any?) {
        val editor = editor
        if (value is Boolean) {
            editor.putBoolean(key, (value as Boolean?)!!)
        } else if (value is Int) {
            editor.putInt(key, (value as Int?)!!)
        } else if (value is Float) {
            editor.putFloat(key, (value as Float?)!!)
        } else if (value is Long) {
            editor.putLong(key, (value as Long?)!!)
        } else if (value is String) {
            editor.putString(key, value as String?)
        } else if (value is Enum<*>) {
            editor.putString(key, value.toString())
        } else if (value is HashSet<*>) {
            editor.putStringSet(key, value as Set<String>?)
        } else if (value != null) {
            throw RuntimeException("Attempting to save non-supported preference")
        }

        editor.commit()
    }

    operator fun <T> get(key: String): T {
        return (sharedPreferences.all[key] as T?)!!
    }

    operator fun <T> get(key: String, defValue: T): T {
        val returnValue = sharedPreferences.all[key] as T?
        return returnValue ?: defValue
    }


   /* fun writeRecentSearch(device: ArrayList<RecentSearch>) {
        try {
            val path = ("/data/data/" + MyApp.instance!!.packageName
                    + "/recentsearch.ser")
            val f = File(path)
            if (f.exists()) {
                f.delete()
            }
            val fileOut = FileOutputStream(path)
            val out = ObjectOutputStream(fileOut)
            out.writeObject(device)
            out.close()
            fileOut.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readRecentSearch(): ArrayList<RecentSearch> {
        val path = ("/data/data/" + MyApp.instance!!.packageName
                + "/recentsearch.ser")
        val f = File(path)
        var device = ArrayList<RecentSearch>()
        if (f.exists()) {
            try {
                System.gc()
                val fileIn = FileInputStream(path)
                val `in` = ObjectInputStream(fileIn)
                device = `in`.readObject() as ArrayList<RecentSearch>
                `in`.close()
                fileIn.close()
            } catch (e: StreamCorruptedException) {
                e.printStackTrace()
            } catch (e: OptionalDataException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return device
    }*/
    /* fun writeSpinnerList(device: ArrayList<SpinnerItemList>) {
         try {
             val path = ("/data/data/" + MyApp.instance!!.packageName
                     + "/recentspinnerlist.ser")
             val f = File(path)
             if (f.exists()) {
                 f.delete()
             }
             val fileOut = FileOutputStream(path)
             val out = ObjectOutputStream(fileOut)
             out.writeObject(device)
             out.close()
             fileOut.close()
         } catch (e: FileNotFoundException) {
             e.printStackTrace()
         } catch (e: IOException) {
             e.printStackTrace()
         }
     }

     fun readSpinnerList(): ArrayList<SpinnerItemList> {
         val path = ("/data/data/" + MyApp.instance!!.packageName
                 + "/recentspinnerlist.ser")
         val f = File(path)
         var device = ArrayList<SpinnerItemList>()
         if (f.exists()) {
             try {
                 System.gc()
                 val fileIn = FileInputStream(path)
                 val `in` = ObjectInputStream(fileIn)
                 device = `in`.readObject() as ArrayList<SpinnerItemList>
                 `in`.close()
                 fileIn.close()
             } catch (e: StreamCorruptedException) {
                 e.printStackTrace()
             } catch (e: OptionalDataException) {
                 e.printStackTrace()
             } catch (e: FileNotFoundException) {
                 e.printStackTrace()
             } catch (e: ClassNotFoundException) {
                 e.printStackTrace()
             } catch (e: IOException) {
                 e.printStackTrace()
             }
         }
         return device
     }*/
    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
    fun isMobValid(mob: String): Boolean {
        return Pattern.compile(
                "[0-9]+"
        ).matcher(mob).matches()
    }

     fun isPasswordValid(str: String): Boolean {
        var ch: Char
        var capitalFlag = false
        var lowerCaseFlag = false
        var numberFlag = false
        var lengthFlag = false
        if(str.length>=6) {
            lengthFlag=true
            for (i in 0 until str.length) {
                ch = str[i]
                if (Character.isDigit(ch)) {
                    numberFlag = true
                } else if (Character.isUpperCase(ch)) {
                    capitalFlag = true
                } else if (Character.isLowerCase(ch)) {
                    lowerCaseFlag = true
                }
                if (numberFlag && capitalFlag && lowerCaseFlag) return true
            }
        }

        return false
    }
    fun isCharacterAllowed(validateString: String): Boolean {
        var containsInvalidChar = false
        for (i in 0 until validateString.length) {
            val type = Character.getType(validateString[i])
            containsInvalidChar = !(type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt())
        }
        return containsInvalidChar
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }
    companion object {
        private const val SHARED_PREFS_NAME = "Seen User"
        private var instance: SharedPreferenceUtility? = null
        const val FCMTOKEN = "fcmToken"
        const val DeviceId = "device_id"
        const val UserId = "userId"
        const val SelectedLang = "selectedLang"
        const val IsLogin = "isLogin"
        const val IsWelcomeShow = "isWelcomeShow"
        const val IsRemembered = "isRemembered"
        const val Phone = "phone"
        const val Password= "password"
        const val IsDefaultLoc= "isDefaultLoc"
        const val SavedLat= "savedLat"
        const val SavedLng= "savedLng"
        const val PrimaryAdapterPos= "primaryAdapterPos"
        const val SecondaryAdapterPos= "secondaryAdapterPos"
        const val TertiaryAdapterPos= "tertiaryAdapterPos"
        const val ProfilePic= "profilepic"
        const val UserName= "username"
        const val UserEmail= "useremail"
        const val UserProfilePic= "userprofilepic"
        const val isLoggedIn= "isLoggedIn"
        const val isSelected = "isSelected"
        const val isFirstTime = "isFirstTime"
        const val isLangSelected = "isLangSelected"
        const val searchItem = "searchItem"

        @Synchronized
        fun getInstance(): SharedPreferenceUtility {
            if (instance == null) {
                instance = SharedPreferenceUtility()
            }
            return instance as SharedPreferenceUtility
        }

    }

}