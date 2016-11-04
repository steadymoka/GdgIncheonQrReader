package com.moka.gdgqrr.model

open class User {

    var id: String? = null
    var email: String? = null
    var name: String? = null
    var isVisit: Int = 0

    fun setIsVisit(isVisit: Int) {
        this.isVisit = isVisit
    }

    fun getIsVisit(): Int {
        return isVisit
    }

}