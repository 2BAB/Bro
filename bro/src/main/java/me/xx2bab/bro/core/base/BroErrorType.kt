package me.xx2bab.bro.core.base

object BroErrorType {
    const val PAGE_MISSING_ARGUMENTS = 0x101
    const val PAGE_CANT_FIND_TARGET = 0x102
    const val MODULE_CLASS_NOT_FOUND_ERROR = 0x201
    const val MODULE_CANT_FIND_TARGET = 0x202
    const val MODULE_CUSTOM_LIFECYCLE_CALLBACK_ERROR = 0x203
    const val API_INIT_ERROR = 0x301
    const val API_CANT_FIND_TARGET = 0x302
}