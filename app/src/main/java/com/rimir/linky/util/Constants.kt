package com.rimir.linky.util

import java.util.*

const val packageName = "com.rimir.linky"
const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=$packageName"
const val REPOSITORY_URL = "https://github.com/ryadamir/Linky"
const val REPOSITORY_ISSUES_URL = "$REPOSITORY_URL/issues"
const val REPOSITORY_CONTRIBUTORS_URL = "$REPOSITORY_URL/graphs/contributors"

const val WIDGET_REFRESH_ACTION = "${packageName}.action.REFRESH"
const val WIDGET_ITEM_CLICK_ACTION = "${packageName}.action.ITEM_CLICK"

var fileExtensions: List<String> =
    Arrays.asList(".pdf", ".doc", ".ppsx", ".pptx", ".zip", ".rar", ".jpg", ".jpeg", ".png")

var fileMedia: List<String> =
    Arrays.asList(".mp4", ".mov", ".wmv" ,".avi", ".flv", ".mkv")
