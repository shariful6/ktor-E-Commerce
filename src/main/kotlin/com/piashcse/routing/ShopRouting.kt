package com.piashcse.routing

import com.piashcse.controller.ShopController
import com.piashcse.models.shop.*
import com.piashcse.models.user.body.JwtTokenBody
import com.piashcse.plugins.RoleManagement
import com.piashcse.utils.ApiResponse
import com.piashcse.utils.Response
import com.piashcse.utils.authenticateWithJwt
import com.papsign.ktor.openapigen.route.path.auth.*
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.ktor.http.*

fun NormalOpenAPIRoute.shopRoute(shopController: ShopController) {
    route("shop/") {
        authenticateWithJwt(RoleManagement.ADMIN.role) {
            route("category").post<Unit, Response, AddShopCategory, JwtTokenBody> { _, shopCategoryBody ->
                shopCategoryBody.validation()
                respond(
                    ApiResponse.success(
                        shopController.createShopCategory(shopCategoryBody.shopCategoryName), HttpStatusCode.OK
                    )
                )
            }
            route("category").get<ShopCategory, Response, JwtTokenBody> { params ->
                params.validation()
                respond(
                    ApiResponse.success(
                        shopController.getShopCategories(
                            params.limit,
                            params.offset
                        ), HttpStatusCode.OK
                    )
                )
            }
            route("category").delete<DeleteShopCategory, Response, JwtTokenBody> { params ->
                params.validation()
                respond(
                    ApiResponse.success(
                        shopController.deleteShopCategory(params.shopCategoryId), HttpStatusCode.OK
                    )
                )
            }
            route("category").put<Unit, Response, UpdateShopCategory, JwtTokenBody> { _, shopCategoryBody ->
                shopCategoryBody.validation()
                shopController.updateShopCategory(
                    shopCategoryBody.shopCategoryId, shopCategoryBody.shopCategoryName
                ).let {
                    respond(ApiResponse.success(it, HttpStatusCode.OK))
                }
            }
        }
        authenticateWithJwt(RoleManagement.SELLER.role, RoleManagement.ADMIN.role) {
            route("add-shop").post<Unit, Response, AddShop, JwtTokenBody> { _, shopBody ->
                val jwtTokenToUserData = principal()
                shopBody.validation()
                shopController.createShop(
                    jwtTokenToUserData.userId, shopBody.shopCategoryId, shopBody.shopName
                ).let {
                    respond(ApiResponse.success(it, HttpStatusCode.OK))
                }
            }
        }
    }

    /* route("shop/") {
         authenticate(AppConstants.RoleManagement.ADMIN) {
             post("add-shop-category") {
                 val addShopCategory = call.receive<AddShopCategory>()
                 addShopCategory.nullProperties {
                     throw MissingRequestParameterException(it.toString())
                 }
                 val db = shopController.createShopCategory(addShopCategory.shopCategoryName)
                 db.let {
                     call.respond(CustomResponse.success(db, HttpStatusCode.OK))
                 }
             }
             post("shop-categories") {
                 val shopCategories = call.receive<GetShopCategory>()
                 shopCategories.nullProperties {
                     throw MissingRequestParameterException(it.toString())
                 }
                 val db = shopController.getShopCategories(shopCategories.offset, shopCategories.limit)
                 db.let {
                     call.respond(CustomResponse.success(db, HttpStatusCode.OK))
                 }
             }
             delete("delete-shop-category") {
                 val deleteShopCategory = call.receive<DeleteShopCategory>()
                 deleteShopCategory.nullProperties {
                     throw MissingRequestParameterException(it.toString())
                 }
                 val db = shopController.deleteShopCategory(deleteShopCategory.shopCategoryId)
                 db.let {
                     call.respond(CustomResponse.success(db, HttpStatusCode.OK))
                 }
             }
             put("update-shop-category") {
                 val updateShopCategory = call.receive<UpdateShopCategory>()
                 updateShopCategory.nullProperties{
                     throw MissingRequestParameterException(it.toString())
                 }
                 val db = shopController.updateShopCategory(
                     updateShopCategory.shopCategoryId, updateShopCategory.shopCategoryName
                 )
                 db.let {
                     call.respond(CustomResponse.success(db, HttpStatusCode.OK))
                 }
             }
         }
         //
         authenticate(AppConstants.RoleManagement.MERCHANT, AppConstants.RoleManagement.ADMIN) {
             post("add-shop") {
                 val jwtTokenToUserData = call.principal<JwtTokenBody>()
                 val addShop = call.receive<AddShop>()
                 addShop.nullProperties {
                     throw MissingRequestParameterException(it.toString())
                 }
                 val db = shopController.createShop(
                     jwtTokenToUserData!!.userId, addShop.shopCategoryId, addShop.shopName
                 )
                 db.let {
                     call.respond(CustomResponse.success(db, HttpStatusCode.OK))
                 }
             }
         }
     }*/
}