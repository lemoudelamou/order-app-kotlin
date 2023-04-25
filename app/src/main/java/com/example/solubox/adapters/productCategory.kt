package com.example.solubox.adapters

class productCategory {

    private var name: String? = null
    private var image = 0

    fun ProductCategory(name: String?, image: Int) {
        this.name = name
        this.image = image
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getImage(): Int {
        return image
    }

    fun setImage(image: Int) {
        this.image = image
    }
}