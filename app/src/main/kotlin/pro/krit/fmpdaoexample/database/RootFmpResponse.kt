package com.rasalexman.fmpdaoexample.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RootFmpResponse {
    @SerializedName("request")
    @Expose
    var request: Request? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("errors")
    @Expose
    var errors: List<Error>? = null

    @SerializedName("http")
    @Expose
    var http: HttpStatus? = null

    @SerializedName("result")
    @Expose
    var result: Result? = null

}

class Credentials {
    @SerializedName("api")
    @Expose
    var api: String? = null

    @SerializedName("host")
    @Expose
    var host: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null

    @SerializedName("device")
    @Expose
    var device: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("env")
    @Expose
    var env: String? = null

    @SerializedName("prj")
    @Expose
    var prj: String? = null
}

class Database {
    @SerializedName("name")
    @Expose
    var name: String? = null
}

class DatabaseWithKey {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("changes")
    @Expose
    var changes: Changes? = null
}

class Changes {
    @SerializedName("updated")
    @Expose
    var updated: Int? = 0
}

class Destination {
    @SerializedName("database")
    @Expose
    var database: Database? = null
}

class Error {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("source")
    @Expose
    var source: String? = null

    @SerializedName("descriptions")
    @Expose
    var descriptions: List<String>? = null
}

class HeadersAuth {
    @SerializedName("Accept")
    @Expose
    var accept: String? = null

    @SerializedName("Authorization")
    @Expose
    var authorization: String? = null

    @SerializedName("Content-Type")
    @Expose
    var contentType: String? = null

    @SerializedName("X-Device-Id")
    @Expose
    var xDeviceId: String? = null
}

class HeadersConnection {
    @SerializedName("Allow")
    @Expose
    var allow: String? = null

    @SerializedName("Connection")
    @Expose
    var connection: String? = null

    @SerializedName("Content-Length")
    @Expose
    var contentLength: String? = null

    @SerializedName("Content-Type")
    @Expose
    var contentType: String? = null

    @SerializedName("Date")
    @Expose
    var date: String? = null

    @SerializedName("ETag")
    @Expose
    var eTag: String? = null

    @SerializedName("Server")
    @Expose
    var server: String? = null

    @SerializedName("Vary")
    @Expose
    var vary: String? = null

    @SerializedName("X-Frame-Options")
    @Expose
    var xFrameOptions: String? = null
}

class HttpRequest {
    @SerializedName("request")
    @Expose
    var request: String? = null

    @SerializedName("headers")
    @Expose
    var headers: HeadersAuth? = null
}

class HttpStatus {
    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("bytesDownloaded")
    @Expose
    var bytesDownloaded: Int? = null

    @SerializedName("bytesUploaded")
    @Expose
    var bytesUploaded: Int? = null

    @SerializedName("headers")
    @Expose
    var headers: HeadersConnection? = null
}

class Request {
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("credentials")
    @Expose
    var credentials: Credentials? = null

    @SerializedName("resource")
    @Expose
    var resource: Resource? = null

    @SerializedName("http")
    @Expose
    var http: HttpRequest? = null

    @SerializedName("source")
    @Expose
    var source: Source? = null

    @SerializedName("destination")
    @Expose
    var destination: Destination? = null

    @SerializedName("retryCount")
    @Expose
    var retryCount: Int? = null

    @SerializedName("retryIntervalSec")
    @Expose
    var retryIntervalSec: Int? = null

    @SerializedName("ignoreSslErrors")
    @Expose
    var ignoreSslErrors: Boolean? = null
}

class Resource {
    @SerializedName("name")
    @Expose
    var name: String? = null
}

class Result {
    @SerializedName("database")
    @Expose
    var database: DatabaseWithKey? = null

    @SerializedName("raw")
    @Expose
    var raw: TokenModel? = null
}

class Source {
    @SerializedName("raw")
    @Expose
    var raw: String? = null
}