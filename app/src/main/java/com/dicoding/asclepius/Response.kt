package com.dicoding.asclepius

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("totalResults")
    val totalResults: Int? = null,

    @field:SerializedName("articles")
    val articles: List<T>? = null
)

data class Source(

    @field:SerializedName("id")
    val id: Any? = null,

    @field:SerializedName("name")
    val name: String? = null
)

data class ArticlesItem(

    @field:SerializedName("publishedAt")
    val publishedAt: String? = null,

    @field:SerializedName("author")
    val author: String? = null,

    @field:SerializedName("urlToImage")
    val urlToImage: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("source")
    val source: Source? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("content")
    val content: String? = null
)
