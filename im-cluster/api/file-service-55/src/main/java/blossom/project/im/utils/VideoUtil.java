package blossom.project.im.utils;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class VideoUtil {

    // 存放截取视频某一帧的图片
    public static String videoFramesPath = "/temp";

    /**
     * 将视频文件帧处理并以“jpg”格式进行存储。
     * 依赖FrameToBufferedImage方法：将frame转换为bufferedImage对象
     *
     * @param path
     */
    public static byte[] getVideoFirstFrame(String path) {
        // Frame对象
        Frame frame = null;
        // 标识
        int flag = 0;
        try {
            /*
            获取视频文件
            */
            URL url =new URL(path);
            //利用HttpURLConnection对象,我们可以从网络中获取网页数据.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream= conn.getInputStream();
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(inputStream);
            fFmpegFrameGrabber.start();
            //视频旋转角度，可能是null
            String rotate_old=fFmpegFrameGrabber.getVideoMetadata("rotate");

            // 获取视频总帧数
            int ftp = fFmpegFrameGrabber.getLengthInFrames();
            System.out.println("时长 " + ftp / fFmpegFrameGrabber.getFrameRate() / 60);

            while (flag <= ftp) {
                frame = fFmpegFrameGrabber.grabImage();
                /*
                对视频的第五帧进行处理
                 */
                if (frame != null && flag == 1) {
                    BufferedImage  bufferedImage= FrameToBufferedImage(frame);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", out);
//                    ImageIO.write(bufferedImage, "jpg", new File(filePath));
                    byte[] b = out.toByteArray();
                    fFmpegFrameGrabber.stop();
                    //有需要旋转
//                    if(rotate_old!=null && !rotate_old.isEmpty()){
//                        int rotate=Integer.parseInt(rotate_old);
//                        VideoProcessing.rotatePhonePhoto(bufferedImage,rotate);
//                    }
                    fFmpegFrameGrabber.close();
                    return b;
                }
                flag++;
            }

        } catch (Exception E) {
            E.printStackTrace();
        }
        return null;
    }

    public static BufferedImage FrameToBufferedImage(Frame frame) {
        // 创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

    public static void ByteToFile(byte[] bytes, String coverPath)throws Exception{
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        BufferedImage bi1 = ImageIO.read(bais);
        try {
            File w2 = new File(coverPath);//可以是jpg,png,gif格式
            ImageIO.write(bi1, "jpg", w2);//不管输出什么格式图片，此处不需改动
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            bais.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String coverName = UUID.randomUUID().toString() + ".jpg";
        String coverPath = VideoUtil.videoFramesPath
                + File.separator + "videos"
                + File.separator + coverName;

        String videoUrl = "http://127.0.0.1:9000/itzixi/chat/1749262655299883010/video/5253202b-bb2c-4809-afa7-dfb9b25f2040.mp4";
        byte[] b = VideoUtil.getVideoFirstFrame(videoUrl);
        VideoUtil.ByteToFile(b, coverPath);
    }
}
