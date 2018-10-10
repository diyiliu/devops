import com.diyiliu.model.MonitorInfo;
import com.diyiliu.plugin.util.JacksonUtil;
import com.diyiliu.util.OsMonitor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.net.InetAddress;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2018-10-09 17:21
 */
public class TestMain {


    @Test
    public void test() {
        final long GIBI = 1L << 30;

        long l = 1000000000000l;
        System.out.println(l / GIBI);
    }

    @Test
    public void test1() throws Exception {
        String ip = InetAddress.getLocalHost().getHostAddress(); //获取本机ip
        System.out.println(ip);

        String temp = System.getProperty("os.name");
        System.out.println(temp);

        System.out.println(0xBD);

        ByteBuf buf = Unpooled.buffer(1);
        buf.writeByte(0xBD);
        System.out.println(buf.readUnsignedByte());
    }

    @Test
    public void testMonitor() {

        OsMonitor monitor = new OsMonitor();
        MonitorInfo info = monitor.osHealth();

        System.out.println(JacksonUtil.toJson(info));
    }
}
