package com.example.backend.exception

import org.springframework.http.HttpStatus

sealed class DomainException(
    override val message: String? = "",
    val errorCode: String,
    val status: HttpStatus,
) : RuntimeException(message) {
    
    class Server(
        message: String = "サーバーエラーです"
    ): DomainException(message, "SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR)

    class Unauthorized(
        message: String = "権限がありません"
    ) : DomainException(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED)

    class InvalidInput(
        message: String = "入力値がおかしいです",
    ) : DomainException(message, "INVALID_INPUT", HttpStatus.BAD_REQUEST)

    class ResourceNotFound(
        message: String = "データが見つかりません"
    ) : DomainException(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND)

    class Unknown(
        message: String
    ) : DomainException(message, "RESTAURANT_UNKNOWN", HttpStatus.MULTI_STATUS)

    sealed class User(message: String, errorCode: String, status: HttpStatus) :
        DomainException(message, errorCode, status) {
        class Crypt(
            message: String = ""
        ) : User(message, "CRYPT_UNDONE", HttpStatus.NOT_ACCEPTABLE)

        class DuplicateEmail(
            message: String = "このメールアドレスは既に使われています"
        ) : User(message, "EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT)

    }

    sealed class Review(message: String, errorCode: String, status: HttpStatus) :
        DomainException(message, errorCode, status) {
        class Forbidden(
            message: String = "ほかのユーザーのレビューは消せません"
        ) : Review(message, "REVIEW_FORBIDDEN", HttpStatus.FORBIDDEN)
    }

    sealed class Restaurant(message: String, errorCode: String, status: HttpStatus) :
        DomainException(message, errorCode, status) {
        class Location(
            message: String = "場所を見つけられなかった"
        ) : Restaurant(message, "RESTAURANT_API_NOT_FOUND", HttpStatus.NO_CONTENT)

        class Address(
            message: String = "住所がなかった"
        ) : Restaurant(message, "RESTAURANT_NO_ADDRESS", HttpStatus.NOT_FOUND)

        class Time(
            message: String = "Timeout"
        ) : Restaurant(message, "RESTAURANT_TIME_OUT", HttpStatus.REQUEST_TIMEOUT)
    }

    sealed class Auth(message: String, errorCode: String, status: HttpStatus) :
        DomainException(message, errorCode, status) {
        class Token(
            message: String 
        ) : Auth(message, "TOKEN_VARIDATE_ERROR", HttpStatus.UNAUTHORIZED)
    }
}