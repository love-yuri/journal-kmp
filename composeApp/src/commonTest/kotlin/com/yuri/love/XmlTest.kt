package com.yuri.love

import com.yuri.love.retrofit.WebDavService.parseWebDavXml
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlin.test.Test

class XmlTest {
    private val log = logger {}
    @Test
    fun parse() {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            
            <d:multistatus xmlns:d="DAV:" xmlns:s="http://ns.jianguoyun.com">
              <d:response>
                <d:href>/dav/journal/</d:href>
                <d:propstat>
                  <d:prop>
                    <d:getcontenttype>httpd/unix-directory</d:getcontenttype>
                    <d:displayname>journal</d:displayname>
                    <d:owner>2078170658@qq.com</d:owner>
                    <d:resourcetype>
                      <d:collection/>
                    </d:resourcetype>
                    <d:getcontentlength>0</d:getcontentlength>
                    <d:getlastmodified>Sat, 11 Oct 2025 11:10:42 GMT</d:getlastmodified>
                    <d:current-user-privilege-set>
                      <d:privilege>
                        <d:read/>
                      </d:privilege>
                      <d:privilege>
                        <d:write/>
                      </d:privilege>
                      <d:privilege>
                        <d:all/>
                      </d:privilege>
                      <d:privilege>
                        <d:read_acl/>
                      </d:privilege>
                      <d:privilege>
                        <d:write_acl/>
                      </d:privilege>
                    </d:current-user-privilege-set>
                  </d:prop>
                  <d:status>HTTP/1.1 200 OK</d:status>
                </d:propstat>
              </d:response>
              <d:response>
                <d:href>/dav/journal/journal_db_backup.zip</d:href>
                <d:propstat>
                  <d:prop>
                    <d:getetag>loW2QoQ3s9D6u4KK3W17lg</d:getetag>
                    <d:getcontenttype>application/zip</d:getcontenttype>
                    <d:displayname>journal_db_backup.zip</d:displayname>
                    <d:owner>2078170658@qq.com</d:owner>
                    <d:getcontentlength>1777</d:getcontentlength>
                    <d:getlastmodified>Fri, 08 Aug 2025 14:41:58 GMT</d:getlastmodified>
                    <d:resourcetype/>
                    <d:current-user-privilege-set>
                      <d:privilege>
                        <d:read/>
                      </d:privilege>
                      <d:privilege>
                        <d:write/>
                      </d:privilege>
                      <d:privilege>
                        <d:all/>
                      </d:privilege>
                      <d:privilege>
                        <d:read_acl/>
                      </d:privilege>
                      <d:privilege>
                        <d:write_acl/>
                      </d:privilege>
                    </d:current-user-privilege-set>
                  </d:prop>
                  <d:status>HTTP/1.1 200 OK</d:status>
                </d:propstat>
              </d:response>
            </d:multistatus>

        """.trimIndent()
        val files = xml.parseWebDavXml()
        files.forEach {
            log.info { "name: ${it.fileName} ${it.isFile}" }
        }
    }
}