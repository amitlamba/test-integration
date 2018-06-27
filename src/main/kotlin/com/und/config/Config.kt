package com.und.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
open class Config {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    open fun dataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

}




