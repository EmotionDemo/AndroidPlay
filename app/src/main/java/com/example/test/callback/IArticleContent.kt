package com.example.test.callback

import androidx.recyclerview.widget.RecyclerView

open interface IArticleContent {
    fun toArticleContent(holder:RecyclerView.ViewHolder,title:String,link:String)
}