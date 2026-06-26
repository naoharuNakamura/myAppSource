package com.example.backend.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppProperties {
    var security = Security()
    var db = Db()

    class Security {
        var secret: String = ""
    }
    class Db {
        var url: String = ""
    }
}
