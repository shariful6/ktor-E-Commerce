package com.piashcse.route

import com.piashcse.controller.WishListController
import com.piashcse.models.user.body.JwtTokenBody
import com.piashcse.plugins.RoleManagement
import com.piashcse.utils.ApiResponse
import com.piashcse.utils.extension.apiResponse
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.wishListRoute(wishlistController: WishListController) {
    route("wishlist") {
        authenticate(RoleManagement.CUSTOMER.role) {
            post("", {
                tags("WishList")
                request {
                    queryParameter<String>("productId") {
                        required = true
                    }
                }
                apiResponse()
            }) {
                val loginUser = call.principal<JwtTokenBody>()
                val requiredParams = listOf("productId")
                requiredParams.filterNot { call.request.queryParameters.contains(it) }.let {
                    if (it.isNotEmpty()) call.respond(ApiResponse.success("Missing parameters: $it", HttpStatusCode.OK))
                }
                val (productId) = requiredParams.map { call.parameters[it]!! }
                call.respond(
                    ApiResponse.success(
                        wishlistController.addToWishList(loginUser?.userId!!, productId), HttpStatusCode.OK
                    )
                )
            }
            get("", {
                tags("WishList")
                apiResponse()
            }) {
                val loginUser = call.principal<JwtTokenBody>()
                call.respond(
                    ApiResponse.success(
                        wishlistController.getWishList(loginUser?.userId!!), HttpStatusCode.OK
                    )
                )
            }
            delete("", {
                tags("WishList")
                request {
                    queryParameter<String>("productId") {
                        required = true
                    }
                }
                apiResponse()
            }) {
                val loginUser = call.principal<JwtTokenBody>()
                val requiredParams = listOf("productId")
                requiredParams.filterNot { call.request.queryParameters.contains(it) }.let {
                    if (it.isNotEmpty()) call.respond(ApiResponse.success("Missing parameters: $it", HttpStatusCode.OK))
                }
                val (productId) = requiredParams.map { call.parameters[it]!! }
                call.respond(
                    ApiResponse.success(
                        wishlistController.deleteFromWishList(loginUser?.userId!!, productId), HttpStatusCode.OK
                    )
                )
            }
        }
    }
}/*
fun NormalOpenAPIRoute.wishListRoute(wishlistController: WishListController) {
    route("wishlist") {
        authenticateWithJwt(RoleManagement.CUSTOMER.role) {
            post<ProductId, Response, Unit, JwtTokenBody> { params, _ ->
                params.validation()
                respond(
                    ApiResponse.success(
                        wishlistController.addToWishList(principal().userId, params.productId), HttpStatusCode.OK
                    )
                )
            }
            get<Unit, Response, JwtTokenBody> { _ ->
                respond(ApiResponse.success(wishlistController.getWishList(principal().userId), HttpStatusCode.OK))
            }
            delete<ProductId, Response, JwtTokenBody> { params ->
                params.validation()
                respond(
                    ApiResponse.success(
                        wishlistController.deleteFromWishList(principal().userId, params.productId), HttpStatusCode.OK
                    )
                )
            }
        }
    }
}*/
