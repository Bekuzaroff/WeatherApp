package com.example.weatherapp.utils

sealed class CitiesResourceState<T> (val data: T?=null, val message: String?=null) {
    class Loading<T>(): CitiesResourceState<T>()
    class Success<T>(val resource: T ?= null): CitiesResourceState<T>(data = resource)
    class Error<T>(val m: String ?= null): CitiesResourceState<T>(message = m)
}