package blossom.project.im.test;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;

/**
 * @Auther ZhangBlossom
 */
public class MinIOTest {

    @Test
    public void testUpload() throws Exception {

        // 创建客户端
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://127.0.0.1:8000")
                .credentials("imooc", "imooc123456")
                .build();

        // 如果没有bucket，则需要创建
        String bucketName = "localjava";
        boolean isExist = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        // 判断当前的bucket是否存在，不存在则创建，存在则什么都不做
        if (!isExist) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } else {
            System.out.println("当前【" + bucketName + "】已经存在！");
        }

        // 上传本地的文件到minio的服务器中
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object("myVideo.mp4")
                        .filename("/Users/lee/Pictures/universe-1.mp4")
                        .build()
        );

    }

}
