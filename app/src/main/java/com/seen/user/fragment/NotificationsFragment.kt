package com.seen.user.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seen.user.R
import com.seen.user.adapter.NotificationsAdapter
import com.seen.user.custom.SwipeToDeleteCallback
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Notifications
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NotificationsFragment : Fragment() {
    lateinit var mView:View
    lateinit var notificationsAdapter: NotificationsAdapter
    var notificationList=ArrayList<Notifications>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_notifications, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        getNotifications(false)
        return mView
    }

    private fun setUpViews() {
        requireActivity().frag_other_toolbar.frag_other_backImg.visibility = View.VISIBLE
        requireActivity().frag_other_toolbar.frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_toolbar.frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_toolbar.frag_other_backImg)
            findNavController().popBackStack()
        }

        mView.swipeRefresh.setOnRefreshListener {
            getNotifications(true)
        }

        mView.rvList.layoutManager=LinearLayoutManager(requireContext())
        notificationsAdapter= NotificationsAdapter(requireContext(), notificationList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {
                if(notificationList[pos].type=="accept" || notificationList[pos].type=="reject"){
                    val bundle=Bundle()
                    bundle.putString("type", notificationList[pos].type)
                    findNavController().navigate(R.id.myOrdersFragment, bundle)
                }

            }

        })
        mView.rvList.adapter=notificationsAdapter

        swipeLeftToDeleteItem()

    }
    private fun getNotifications(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()))


        val call = apiInterface.getNotifications(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if(jsonObject.getInt("response")==1){
                            val notifications= jsonObject.getJSONArray("notifications")
                            notificationList.clear()

                            for(i in 0 until notifications.length()){
                                val obj = notifications.getJSONObject(i)
                                val n = Notifications()
                                n.id = obj.getInt("id")
                                n.title = obj.getString("title")
                                n.time = obj.getString("time")
                                n.message = obj.getString("message")
                                n.type = obj.getString("type")
                                n.seen = obj.getInt("seen")
                                notificationList.add(n)
                            }

                            if(notifications.length()==0){
                                mView.noNotificationView.visibility=View.VISIBLE
                                mView.swipeRefresh.visibility=View.GONE
                            }
                            else{
                                mView.noNotificationView.visibility=View.GONE
                                mView.swipeRefresh.visibility=View.VISIBLE
                            }

                            if (notificationList.size==0){
                                mView.noNotificationView.visibility=View.VISIBLE
                                mView.swipeRefresh.visibility=View.GONE
                            }else{
                                mView.noNotificationView.visibility=View.GONE
                                mView.swipeRefresh.visibility=View.VISIBLE
                            }
                            notificationsAdapter.notifyDataSetChanged()
                        }else{
                            mView.noNotificationView.visibility=View.VISIBLE
                            mView.swipeRefresh.visibility=View.GONE
                        }
                    }else{
                        LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
            }
        })
    }
    private fun swipeLeftToDeleteItem() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition //get position which is swipe
                if (direction == ItemTouchHelper.LEFT) {    //if swipe left
                    val builder = AlertDialog.Builder(requireContext()) //alert for confirm to delete
                    builder.setTitle(getString(R.string.delete)) //set message
                    builder.setMessage(getString(R.string.are_you_sure_you_want_to_delete_the_notification)) //set message
                    builder.setPositiveButton(getString(R.string.delete), DialogInterface.OnClickListener { dialog, which ->
                        notificationDelete(position)
                        return@OnClickListener
                    }).setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which ->

                        //not removing items if cancel is done
                        notificationsAdapter.notifyItemRemoved(position + 1) //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                        notificationsAdapter.notifyItemRangeChanged(position, notificationsAdapter.itemCount) //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                        return@OnClickListener
                    }).show().setCanceledOnTouchOutside(false) //show alert dialog
                }
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(mView.rvList)
    }
    private fun notificationDelete(pos: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("notification_id", "user_id", "lang"),
            arrayOf(notificationList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.notificationDelete(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            notificationsAdapter.notifyItemRemoved(pos)
                            notificationList.removeAt(pos)
                            notificationsAdapter.notifyDataSetChanged()
                        }
                        else{
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        }


                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        requireActivity().home_frag_categories.visibility=View.GONE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
    }
}