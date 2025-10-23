package com.yuri.love.retrofit

import com.yuri.love.database.SystemConfig
import com.yuri.love.share.json
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Url
import java.io.File
import java.io.FileOutputStream
import kotlin.io.encoding.Base64

object WebDavService {
    const val HOST = "https://dav.jianguoyun.com/"
    const val DEFAULT_PATH = "dav/" // 默认路径
    const val DB_FOLDER = "journal" // 数据库上传的文件夹名称

    private val log = logger {}
    private val account get() = SystemConfig.webdav_account
    private val password get() = SystemConfig.webdav_password

    private val service: WebDavService by lazy {
        retrofit.create(WebDavService::class.java)
    }

    private interface WebDavService {
        @Headers("Depth: 1")
        @HTTP(method = "PROPFIND", hasBody = false)
        suspend fun dir(
            @Url url: String,
            @Header("Authorization") authorization: String,
            @Header("Content-Type") contentType: String
        ): ResponseBody

        @HTTP(method = "MKCOL", hasBody = false)
        suspend fun mkdir(
            @Url url: String,
            @Header("Authorization") authorization: String,
            @Header("Content-Type") contentType: String
        ): ResponseBody

        @PUT
        suspend fun uploadFile(
            @Url url: String,
            @Header("Authorization") authorization: String,
            @Header("Content-Type") contentType: String,
            @Header("Content-Length") contentLength: Long,
            @Body file: RequestBody
        ): Response<ResponseBody>

        @GET
        suspend fun downloadFile(
            @Url url: String,
            @Header("Authorization") authorization: String
        ): Response<ResponseBody>

    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(HOST)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    /**
     * 获取数据库文件所在目录
     */
    suspend fun dir(path: String = "dav/$DB_FOLDER"): List<WebdavFile> {
        if (account.isNullOrEmpty() || password.isNullOrEmpty()) {
            throw Exception("account or password is empty")
        }

        try {
            val response = service.dir(
                path,
                "Basic ${Base64.encode("$account:$password".encodeToByteArray())}",
                "text/xml"
            )
            return response.string().parseWebDavXml()
        } catch (e: Exception) {
            log.error { "发生错误错误: $e" }
            return listOf()
        }
    }

    /**
     * 创建目录
     */
    suspend fun mkdir(path: String = DB_FOLDER): Boolean {
        try {
            service.mkdir(
                "dav/$path",
                "Basic ${Base64.encode("$account:$password".toByteArray())}",
                "text/xml"
            )
            return true
        } catch (e: Exception) {
            log.error { "发生错误错误: $e" }
            return false
        }
    }

    /**
     * 上传文件
     */
    suspend fun upload(file: File, fileName: String = file.name): Boolean {
        try {
            return try {
                if (!file.exists()) {
                    return false
                }
                val url = "dav/$DB_FOLDER/$fileName"
                val authorization = "Basic ${Base64.encode("$account:$password".toByteArray())}"
                val contentType = "application/octet-stream"
                val contentLength = file.length()
                val mediaType = contentType.toMediaTypeOrNull()
                val requestBody = file.asRequestBody(mediaType)

                val response = service.uploadFile(url, authorization, contentType, contentLength, requestBody)
                response.code() == 201 || response.code() == 204
            } catch (e: Exception) {
                log.error { "解析错误: ${e.message}" }
                false
            }
        } catch (e: Exception) {
            log.error { "发生错误错误: $e" }
            return false
        }
    }

    /**
     * 下载文件
     */
    suspend fun download(
        fileName: String,
        destination: File
    ): Boolean {
        if (account.isNullOrEmpty() || password.isNullOrEmpty()) {
            return false
        }

        val url = "dav/$DB_FOLDER/$fileName"
        val authorization = "Basic ${Base64.encode("$account:$password".toByteArray())}"

        return try {
            val response = service.downloadFile(url, authorization)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val inputStream = body.byteStream()
                    val outputStream = FileOutputStream(destination)
                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    true
                } ?: false
            } else {
                log.error { "下载错误: $response" }
                false
            }
        } catch (e: Exception) {
            log.error { "下载错误: ${e.message}" }
            false
        }
    }

    data class WebdavFile(
        var isFile: Boolean = true,
        var isFolder: Boolean = false,
        var path: String,
        var parent: String? = null,
        var children: MutableList<WebdavFile>? = null,
        var fileName: String
    )

    /**
     * parse webdav xml to list
     */
    @Serializable
    @XmlSerialName("multistatus", "DAV:", "d")
    data class MultiStatus(
        @XmlElement(true)
        val response: List<ResponseItem> = emptyList()
    )

    @Serializable
    @XmlSerialName("response", "DAV:", "d")
    data class ResponseItem(
        @XmlElement(true)
        val href: String,

        @XmlElement(true)
        val propstat: List<PropStat>? = null
    )

    @Serializable
    @XmlSerialName("propstat", "DAV:", "d")
    data class PropStat(
        @XmlElement(true)
        val prop: Prop? = null
    )

    @Serializable
    @XmlSerialName("prop", "DAV:", "d")
    data class Prop(
        @XmlElement(true)
        val getcontenttype: String? = null,
        @XmlElement(true)
        val displayname: String,
        @XmlElement(true)
        val getcontentlength: String? = null,
        @XmlElement(true)
        val getlastmodified: String? = null,
        @XmlElement(true)
        val resourcetype: ResourceType? = null
    )

    @Serializable
    @XmlSerialName("resourcetype", "DAV:", "d")
    data class ResourceType(
        @XmlElement(true)
        val collection: CollectionElement? = null
    )

    @Serializable
    @XmlSerialName("collection", "DAV:", "d")
    class CollectionElement

    @OptIn(ExperimentalXmlUtilApi::class)
    fun String.parseWebDavXml(): List<WebdavFile> {
        val defaultPolicy = DefaultXmlSerializationPolicy.Builder()
        defaultPolicy.ignoreUnknownChildren()

        val xml = XML {
            autoPolymorphic = true
            policy = defaultPolicy.build()
        }

        val xmlClean = this.trimStart('\uFEFF', '\n', '\r', ' ', '\t')
        val multi = xml.decodeFromString<MultiStatus>(xmlClean)

        return multi.response.mapNotNull { resp ->
            val prop = resp.propstat?.firstOrNull()?.prop ?: return@mapNotNull null
            val isFolder = prop.resourcetype?.collection != null
            WebdavFile(
                path = resp.href,
                fileName = prop.displayname,
                isFolder = isFolder,
                isFile = !isFolder,
            )
        }
    }
}

