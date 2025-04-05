package nmng108.microtube.mainservice;

import io.minio.ListObjectsArgs;
import io.minio.MinioAsyncClient;
import io.minio.RemoveObjectsArgs;
import io.minio.RestoreObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import nmng108.microtube.mainservice.configuration.ObjectStoreConfiguration;
import nmng108.microtube.mainservice.service.ObjectStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import reactor.tools.agent.ReactorDebugAgent;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication(
        exclude = {
                ReactiveSecurityAutoConfiguration.class, ReactiveManagementWebSecurityAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class,
        }
)
@Slf4j
public class MainServiceApplication /*implements CommandLineRunner*/ {
    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        ReactorDebugAgent.init();
//        ReactorDebugAgent.processExistingClasses();
        SpringApplication.run(MainServiceApplication.class, args);
    }

//    @Override // test object store APIs
//    public void run(String... args) throws Exception {
//        ObjectStoreConfiguration config = applicationContext.getBean(ObjectStoreConfiguration.class);
//        MinioAsyncClient minioAsyncClient = applicationContext.getBean(MinioAsyncClient.class);
//        ObjectStoreService objectStoreService = applicationContext.getBean(ObjectStoreService.class);
//        String bucket = config.getHlsBucketName();
//        String dir = "1/4db43e0f-680e-4bcd-bfbb-37d4d3cd5b27.mp4";
//
//        log.info("file: {}", objectStoreService.getObject(bucket, dir).object());
//        minioAsyncClient//.restoreObject(RestoreObjectArgs.builder()..build())
//                .listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(dir).recursive(true).build())
//                .forEach((r) -> {
//                    try {
//                        var o = r.get();
//
//                        log.info("name: {} - isDir: {} - size: {} - isDeleteMarker: {}", o.objectName(), o.isDir(), o.size(), o.isDeleteMarker());
//
////                      .removeObjectAsync(bucket, dir)
////                      .thenRun(() -> log.info("Successfully deleted the directory"))
////                      .exceptionally((e) -> {
////                          log.error(e.getMessage());
////                          return null;
////                      });
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//    }

//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println(applicationContext.getClass().getName());
//        System.out.println(applicationContext.getId());
//        System.out.println(applicationContext.getApplicationName()); // null (or empty)
//
//        for (String n : applicationContext.getBeanNamesForType(R2dbcEntityTemplate.class)) {
//            System.out.println(n);
//        }
//
//        for (String n : applicationContext.getBeanNamesForType(ConnectionFactory.class)) {
//            System.out.print(n);
//
//            if (applicationContext.getBean(n).equals(applicationContext.getBean(DatabaseClient.class).getConnectionFactory())) {
//                System.out.println(" - This is depended factory from which DatabaseClient is created");
//            } else {
//                System.out.println();
//            }
//        }
//
////        for (String n : applicationContext.getBeanNamesForType(ConnectionFactory.class)) {
////            System.out.print(n);
////
////            if (applicationContext.getBean(n).equals(applicationContext.getBean(R2dbcTransactionManager.class).getConnectionFactory())) {
////                System.out.println(" - This is depended factory from which DatabaseClient is created");
////            } else {
////                System.out.println();
////            }
////        }
//
//        System.out.println(applicationContext.getBean(ReactiveTransactionManager.class));
////        Arrays.stream(applicationContext.getBean(DatabaseClient.class).getClass().getAnnotations()).forEach(a -> {
////            System.out.println(a.annotationType().getName() + " - value = " + a);
////        });
//    }
}
