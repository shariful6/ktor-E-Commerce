package com.piashcse.route

import com.piashcse.controller.ShopController
import com.piashcse.models.shop.*
import com.piashcse.models.user.body.JwtTokenBody
import com.piashcse.plugins.RoleManagement
import com.piashcse.utils.ApiResponse
import com.piashcse.utils.extension.apiResponse
import com.piashcse.utils.extension.getCurrentUser
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.shopRoute(shopController: ShopController) {
    route("shop") {
        authenticate(RoleManagement.ADMIN.role) {
            post("category", {
                tags("Shop")
                request {
                    body<AddShopCategory>()
                }
                apiResponse()
            }) {
                val requestBody = call.receive<AddShopCategory>()
                requestBody.validation()
                call.respond(
                    ApiResponse.success(
                        shopController.createShopCategory(requestBody.shopCategoryName), HttpStatusCode.OK
                    )
                )
            }
            get("category", {
                tags("Shop")
                request {
                    queryParameter<Int>("limit") {
                        required = true
                    }
                    queryParameter<Long>("offset") {
                        required = true
                    }
                }
                apiResponse()
            }) {
                val requiredParams = listOf("limit", "offset")
                requiredParams.filterNot { call.request.queryParameters.contains(it) }.let {
                    if (it.isNotEmpty()) call.respond(ApiResponse.success("Missing parameters: $it", HttpStatusCode.OK))
                }
                val (limit, offset) = requiredParams.map { call.parameters[it]!! }
                call.respond(
                    ApiResponse.success(
                        shopController.getShopCategories(
                            limit.toInt(), offset.toLong()
                        ), HttpStatusCode.OK
                    )
                )
            }
            delete("category", {
                tags("Shop")
                request {
                    queryParameter<String>("shopCategoryId"){
                        required = true
                    }
                }
                apiResponse()
            }) {
                val requiredParams = listOf("shopCategoryId")
                requiredParams.filterNot { call.request.queryParameters.contains(it) }.let {
                    if (it.isNotEmpty()) call.respond(ApiResponse.success("Missing parameters: $it", HttpStatusCode.OK))
                }
                val (shopCategoryId) = requiredParams.map { call.parameters[it]!! }
                call.respond(
                    ApiResponse.success(
                        shopController.deleteShopCategory(shopCategoryId), HttpStatusCode.OK
                    )
                )
            }
            put("category", {
                tags("Shop")
                request {
                    queryParameter<String>("shopCategoryId"){
                        required = true
                    }
                    queryParameter<String>("shopCategoryName"){
                        required = true
                    }
                }
                apiResponse()
            }) {
                val requiredParams = listOf("shopCategoryId", "shopCategoryName")
                requiredParams.filterNot { call.request.queryParameters.contains(it) }.let {
                    if (it.isNotEmpty()) call.respond(ApiResponse.success("Missing parameters: $it", HttpStatusCode.OK))
                }
                val (shopCategoryId, shopCategoryName) = requiredParams.map { call.parameters[it]!! }
                call.respond(
                    ApiResponse.success(
                        shopController.deleteShopCategory(shopCategoryId), HttpStatusCode.OK
                    )
                )
                shopController.updateShopCategory(
                    shopCategoryId, shopCategoryName
                ).let {
                    call.respond(ApiResponse.success(it, HttpStatusCode.OK))
                }
            }
        }
        authenticate(RoleManagement.SELLER.role, RoleManagement.ADMIN.role) {
            post("category", {
                tags("Shop")
                request {
                    body<AddShop>()
                }
                apiResponse()
            }) {
                val requestBody = call.receive<AddShop>()
                requestBody.validation()
                shopController.createShop(
                    getCurrentUser().userId, requestBody.shopCategoryId, requestBody.shopName
                ).let {
                    call.respond(ApiResponse.success(it, HttpStatusCode.OK))
                }
            }
        }
    }
}
/*
fun NormalOpenAPIRoute.shopRoute(shopController: ShopController) {
    route("shop/") {
        authenticateWithJwt(RoleManagement.ADMIN.role) {
            route("category").post<AddShopCategory, Response, Unit, JwtTokenBody> { params, _ ->
                params.validation()
                respond(
                    ApiResponse.success(
                        shopController.createShopCategory(params.shopCategoryName), HttpStatusCode.OK
                    )
                )
            }
            route("category").get<PagingData, Response, JwtTokenBody> { params ->
                params.validation()
                respond(
                    ApiResponse.success(
                        shopController.getShopCategories(
                            params.limit, params.offset
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
            route("category").put<UpdateShopCategory, Response, Unit, JwtTokenBody> { params, _ ->
                params.validation()
                shopController.updateShopCategory(
                    params.shopCategoryId, params.shopCategoryName
                ).let {
                    respond(ApiResponse.success(it, HttpStatusCode.OK))
                }
            }
        }
        authenticateWithJwt(RoleManagement.SELLER.role, RoleManagement.ADMIN.role) {
            route("add-shop").post<AddShop, Response, Unit, JwtTokenBody> { params, _ ->
                params.validation()
                shopController.createShop(
                    principal().userId, params.shopCategoryId, params.shopName
                ).let {
                    respond(ApiResponse.success(it, HttpStatusCode.OK))
                }
            }
        }
    }
}*/
