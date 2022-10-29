package com.example.animessenger.adapter

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.animessenger.R
import com.example.animessenger.models.CommonModel
import com.example.animessenger.utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.message_item.view.*
import java.io.File

class SingleChatAdapter : RecyclerView.Adapter<SingleChatAdapter.Holder>() {

    private var messageList = mutableListOf<CommonModel>()
    private val voicePlayer = VoicePlayer()
    private lateinit var diffResult: DiffUtil.DiffResult

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        //text
        val myMessageBlock: ConstraintLayout = view.cl_block_mymessage
        val myMessage: TextView = view.tv_message_mychat
        val myMessageTime: TextView = view.tv_message_mychat_time
        val userMessageBlock: ConstraintLayout = view.cl_block_message_chat
        val userMessage: TextView = view.tv_message_chat_riv
        val userMessageTime: TextView = view.tv_message_chat_time

        //image
        val myMessageImageBlock: ConstraintLayout = view.cl_block_mymessage_image
        val myMessageImage: ImageView = view.image_mymesaage
        val myMessageImageTime: TextView = view.tv_message_mychat_image
        val userMessageImageBlock: ConstraintLayout = view.cl_block_message_image
        val userMessageImage: ImageView = view.image_mesaage
        val userMessageImageTime: TextView = view.tv_message_chat_time_image

        //voice
        val myVoiceBlock: ConstraintLayout = view.cl_block_myvoice
        val myVoicePlay: ImageView = view.image_myplay
        val myVoiceStop: ImageView = view.image_mystop
        val myVoiceTime: TextView = view.tv_message_myvoice_time
        val userVoiceBlock: ConstraintLayout = view.cl_block_voice
        val userVoicePlay: ImageView = view.image_play
        val userVoiceStop: ImageView = view.image_stop
        val userVoiceTime: TextView = view.tv_message_voice_time

//        //file
//        val myFileBlock: ConstraintLayout = view.cl_block_myfile
//        val myFileName: TextView = view.tv_myfile_name
//        val myFileImage: ImageView = view.image_myfile
//        val myFileTime: TextView = view.tv_message_myfile_time
//        val userFileBlock: ConstraintLayout = view.cl_block_file
//        val userFileName: TextView = view.tv_file_name
//        val userFileImage: ImageView = view.image_file
//        val userFileTime: TextView = view.tv_message_file_time
//        val myProgress: ProgressBar = view.progressBarMyFile
//        val userProgress: ProgressBar = view.progressBarFile

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (messageList[position].type) {
            TYPE_TEXT -> drawMessageText(holder, position)
            TYPE_IMAGE -> drawMessageImage(holder, position)
            TYPE_VOICE -> drawMessageVoice(holder, position)
//            TYPE_FILE -> drawMessageFile(holder, position)
        }

    }

//    private fun drawMessageFile(holder: SingleChatAdapter.Holder, position: Int) {
//        if (messageList[position].fromUser == UID) {
//            holder.myFileBlock.visibility = View.VISIBLE
//            holder.userFileBlock.visibility = View.GONE
//            holder.myFileName.text = messageList[position].text
//            holder.myFileTime.text = messageList[position].timeStamp.toString()
//                .asTime()
//        } else {
//            holder.myFileBlock.visibility = View.GONE
//            holder.userFileBlock.visibility = View.VISIBLE
//            holder.userFileName.text = messageList[position].text
//            holder.userFileTime.text = messageList[position].timeStamp.toString()
//                .asTime()
//        }
//    }

    private fun drawMessageVoice(holder: Holder, position: Int) {
        if (messageList[position].fromUser == UID) {
            holder.myVoiceBlock.visibility = View.VISIBLE
            holder.userVoiceBlock.visibility = View.GONE
            holder.myMessage.text = messageList[position].text
            holder.myVoiceTime.text = messageList[position].timeStamp.toString()
                .asTime()
        } else {
            holder.myVoiceBlock.visibility = View.GONE
            holder.userVoiceBlock.visibility = View.VISIBLE
            holder.userMessage.text = messageList[position].text
            holder.userVoiceTime.text = messageList[position].timeStamp.toString()
                .asTime()
        }
    }

    private fun drawMessageImage(holder: Holder, position: Int) {
        holder.myMessageBlock.visibility = View.GONE
        holder.userMessageBlock.visibility = View.GONE
        if (messageList[position].fromUser == UID) {
            holder.myMessageImageBlock.visibility = View.VISIBLE
            holder.userMessageImageBlock.visibility = View.GONE
            Picasso.get()
                .load(messageList[position].fileUrl)
                .fit()
                .placeholder(R.drawable.sakura)
                .into(holder.myMessageImage)
            holder.myMessageImageTime.text = messageList[position].timeStamp.toString()
                .asTime()
        } else {
            holder.myMessageImageBlock.visibility = View.GONE
            holder.userMessageImageBlock.visibility = View.VISIBLE
            Picasso.get()
                .load(messageList[position].fileUrl)
                .fit()
                .placeholder(R.drawable.sakura)
                .into(holder.userMessageImage)
            holder.userMessageImageTime.text = messageList[position].timeStamp.toString()
                .asTime()
        }
    }

    private fun drawMessageText(holder: Holder, position: Int) {
        holder.myMessageImageBlock.visibility = View.GONE
        holder.userMessageImageBlock.visibility = View.GONE
        if (messageList[position].fromUser == UID) {
            holder.myMessageBlock.visibility = View.VISIBLE
            holder.userMessageBlock.visibility = View.GONE
            holder.myMessage.text = messageList[position].text
            holder.myMessageTime.text = messageList[position].timeStamp.toString()
                .asTime()
        } else {
            holder.myMessageBlock.visibility = View.GONE
            holder.userMessageBlock.visibility = View.VISIBLE
            holder.userMessage.text = messageList[position].text
            holder.userMessageTime.text = messageList[position].timeStamp.toString()
                .asTime()
        }
    }


    override fun getItemCount(): Int = messageList.size

    fun addItem(item: CommonModel, isScrollToBottom: Boolean) {
        if (isScrollToBottom) {
            if (!messageList.contains(item)) {
                messageList.add(item)
                notifyItemInserted(messageList.size)
            }
        } else {
            if (!messageList.contains(item)) {
                messageList.add(item)
                messageList.sortBy { it.timeStamp.toString() }
                notifyItemInserted(0)
            }
        }


//        val newList = mutableListOf<CommonModel>()
//        newList.addAll(messageList)
//        if (!newList.contains(item)) newList.add(item)
//        newList.sortBy { it.timeStamp.toString() }
//        diffResult = DiffUtil.calculateDiff(DiffUtilCallback(messageList, newList))
//        diffResult.dispatchUpdatesTo(this)
//        messageList = newList
    }

    private fun onAttach(view: CommonModel, holder: Holder) {
        voicePlayer.initPlayer()
        if (view.fromUser == UID) {
            holder.myVoicePlay.setOnClickListener {
                holder.myVoicePlay.visibility = View.GONE
                holder.myVoiceStop.visibility = View.VISIBLE
                holder.myVoiceStop.setOnClickListener {
                    stop {
                        holder.myVoiceStop.setOnClickListener(null)
                        holder.myVoicePlay.visibility = View.VISIBLE
                        holder.myVoiceStop.visibility = View.GONE
                    }
                }
                play(view) {
                    holder.myVoicePlay.visibility = View.VISIBLE
                    holder.myVoiceStop.visibility = View.GONE
                }
            }
        } else {
            holder.userVoicePlay.setOnClickListener {
                holder.userVoicePlay.visibility = View.GONE
                holder.userVoiceStop.visibility = View.VISIBLE
                holder.userVoiceStop.setOnClickListener {
                    stop {
                        holder.userVoiceStop.setOnClickListener(null)
                        holder.userVoicePlay.visibility = View.VISIBLE
                        holder.userVoiceStop.visibility = View.GONE
                    }
                }
                play(view) {
                    holder.userVoicePlay.visibility = View.VISIBLE
                    holder.userVoiceStop.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: CommonModel, function: () -> Unit) {
        voicePlayer.play(view.id, view.fileUrl) {
            function()
        }
    }

    private fun stop(function: () -> Unit) {
        voicePlayer.stop { function() }
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        onAttach(messageList[holder.absoluteAdapterPosition], holder)
        super.onViewAttachedToWindow(holder)

    }

    override fun onViewDetachedFromWindow(holder: Holder) {
        holder.myVoicePlay.setOnClickListener(null)
        holder.userVoicePlay.setOnClickListener(null)
        voicePlayer.release()
        super.onViewDetachedFromWindow(holder)
    }

}

