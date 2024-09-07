package com.piashcse.controller

import com.piashcse.entities.shop.*
import com.piashcse.entities.user.UserTable
import com.piashcse.utils.extension.alreadyExistException
import com.piashcse.utils.extension.isNotExistException
import com.piashcse.utils.extension.query
import org.jetbrains.exposed.dao.id.EntityID

class ShopCategoryController {
    suspend fun addShopCategory(shopCategoryName: String) = query {
        val categoryExist =
            ShopCategoryEntity.find { ShopCategoryTable.shopCategoryName eq shopCategoryName }.toList().singleOrNull()
        if (categoryExist == null) {
            ShopCategoryEntity.new {
                this.shopCategoryName = shopCategoryName
            }.shopCategoryResponse()
        } else {
            shopCategoryName.alreadyExistException()
        }
    }

    suspend fun getShopCategories(limit: Int, offset: Long) = query {
        val shopCategories = ShopCategoryEntity.all().limit(limit, offset)
        shopCategories.map {
            it.shopCategoryResponse()
        }
    }

    suspend fun updateShopCategory(shopCategoryId: String, shopCategoryName: String) = query {
        val shopCategoryExist =
            ShopCategoryEntity.find { ShopCategoryTable.id eq shopCategoryId }.toList().singleOrNull()
        shopCategoryExist?.apply {
            this.shopCategoryName = shopCategoryName
        }?.shopCategoryResponse() ?: shopCategoryId.isNotExistException()
    }

    suspend fun deleteShopCategory(shopCategoryId: String) = query {
        val shopCategoryExist =
            ShopCategoryEntity.find { ShopCategoryTable.id eq shopCategoryId }.toList().singleOrNull()
        shopCategoryExist?.let {
            shopCategoryExist.delete()
            shopCategoryId
        } ?: run {
            shopCategoryId.isNotExistException()
        }
    }
}