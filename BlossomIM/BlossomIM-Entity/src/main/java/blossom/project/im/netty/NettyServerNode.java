package blossom.project.im.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Auther ZhangBlossom
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NettyServerNode {

    private String ip;
    private Integer port;
    private Integer onlineCounts = 0;

}
