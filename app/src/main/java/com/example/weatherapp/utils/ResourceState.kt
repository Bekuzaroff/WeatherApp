package com.example.weatherapp.utils

sealed class ResourceState<T>(val data: T?=null, val message: String?=null) {
    class Loading<T>(): ResourceState<T>()
    class Success<T>(val resource: T ?= null): ResourceState<T>(data = resource)
    class Error<T>(val m: String ?= null): ResourceState<T>(message = m)
}