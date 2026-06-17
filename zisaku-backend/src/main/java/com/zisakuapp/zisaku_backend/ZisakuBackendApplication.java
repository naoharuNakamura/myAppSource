package com.zisakuapp.zisaku_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// import java.util.Properties;

@SpringBootApplication
@MapperScan("com.zisakuapp.zisaku_backend.mapper")
public class ZisakuBackendApplication {

    // public static void main(String[] args) {
    //     Properties props = new Properties();
    //     props.setProperty("spring.datasource.url", "jdbc:sqlite:database.db");
    //     props.setProperty("spring.datasource.driver-class-name", "org.sqlite.JDBC");

    //     SpringApplication app = new SpringApplication(ZisakuBackendApplication.class);
    //     app.setDefaultProperties(props);
        
    //     // 💡 起動コードはこれ「1行だけ」にします！
    //     app.run(args);
    // }
    public static void main(String[] args) {
        // 設定コードをすべて削除！
        SpringApplication.run(ZisakuBackendApplication.class, args);
    }

}