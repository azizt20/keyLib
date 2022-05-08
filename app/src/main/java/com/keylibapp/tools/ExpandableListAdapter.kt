package com.keylibapp.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.keylibapp.AccountModel
import com.keylibapp.R
import com.keylibapp.screens.Adding_new_account

class ExpandableListAdapter(
    var context: Context,
    var header: MutableList<String>,
    var body: MutableList<AccountModel>
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return header.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getGroup(groupPosition: Int): String {
        return header[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return body[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.layout_group, null)
        }
        val title = convertView?.findViewById<TextView>(R.id.service_title)
        title?.text = getGroup(groupPosition)
        return convertView

    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.layout_child, null)
        }
        val login = convertView?.findViewById<TextView>(R.id.login)
        val title = convertView?.findViewById<TextView>(R.id.title)
        val password = convertView?.findViewById<TextView>(R.id.password)
        val comment = convertView?.findViewById<TextView>(R.id.comment)
        val showPasswordBtn = convertView?.findViewById<ImageButton>(R.id.show_password_button)
        val editAccountData = convertView?.findViewById<ImageButton>(R.id.edit_button)
        val copyLoginButton = convertView?.findViewById<Button>(R.id.copy_login)
        val copyPasswordButton = convertView?.findViewById<Button>(R.id.copy_password)


        val child = getChild(groupPosition, childPosition) as AccountModel

        title?.text = child.service
        login?.text = child.login
        comment?.text = child.comment
        password?.text = generatePassword(child.password.length)

        showPasswordBtn?.setOnClickListener {
            if (password?.text == child.password) {
                password?.text = generatePassword(child.password.length)
            } else {
                password?.text = child.password
            }
        }

        editAccountData?.setOnClickListener {
            val intent = Intent(context, Adding_new_account::class.java)
            intent.putExtra("service", child.service)
            intent.putExtra("email", child.login)
            intent.putExtra("password", child.password)
            intent.putExtra("comment", child.comment)
            Log.d("asdfasdfasdf", child.service)
            startActivity(context, intent, null)
        }

        copyLoginButton?.setOnClickListener {
            copyToClip("login", child.login)
        }
        copyPasswordButton?.setOnClickListener {
            copyToClip("password", child.password)
        }

        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private fun copyToClip(label: String, text: String) {
        var myClipboard =
            getSystemService(context!!, ClipboardManager::class.java) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(label, text)
        myClipboard.setPrimaryClip(clip)
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_LONG)
            .show()
    }


    private fun generatePassword(n: Int): String {
        var dot = ""
        for (i in 0..n) {
            dot += "*"
        }
        return dot
    }
}