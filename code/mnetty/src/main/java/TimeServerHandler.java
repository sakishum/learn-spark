import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author migle on 2017/9/19.
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        System.out.println("channelActive");
        final ByteBuf time = ctx.alloc().buffer(20); // (2)
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        //time.writeBytes("hello netty!!!".getBytes());
        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        //在netty中所有操作均为异步，所以要等发送完成后再关闭！
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        }); // (4)
    }
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
