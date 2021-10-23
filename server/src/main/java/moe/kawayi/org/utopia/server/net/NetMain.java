//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// The NetMain.java is a part of project utopia, under MIT License.
// See https://opensource.org/licenses/MIT for license information.
// Copyright (c) 2021 moe-org All rights reserved.
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

package moe.kawayi.org.utopia.server.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import moe.kawayi.org.utopia.core.util.Nullable;
import moe.kawayi.org.utopia.server.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 设置网络
 */
public final class NetMain {

    /**
     * 日志器
     */
    private static final Logger LOGGER = LogManager.getLogger(NetMain.class);

    /**
     * 服务器运行状态
     */
    private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(false);

    /**
     * 工作组
     */
    @Nullable
    private static final AtomicReference<EventLoopGroup> bossGroup = new AtomicReference<>(null);

    /**
     * 工作组
     */
    @Nullable
    private static final AtomicReference<EventLoopGroup> workerGroup = new AtomicReference<>(null);

    /**
     * 启动网络系统
     *
     * @throws InterruptedException 线程中断
     */
    public static void internetBootstrap() throws InterruptedException {
        // never run again
        if (IS_RUNNING.getAndSet(true))
            return;

        // 获取设置
        int boosThreadCount = Integer.parseInt(
                ConfigManager.getSystemConfig(NetConfig.NETTY_BOOS_THREAD_COUNT).orElseThrow());

        int workerThreadCount = Integer.parseInt(
                ConfigManager.getSystemConfig(NetConfig.NETTY_WORKER_THREAD_COUNT).orElseThrow());

        int port = Integer.parseInt(
                ConfigManager.getSystemConfig(NetConfig.PORT).orElseThrow());

        int maxWaitList = Integer.parseInt(
                ConfigManager.getSystemConfig(NetConfig.MAX_WAIT_LIST).orElseThrow());

        // 初始化事件循环线程
        bossGroup.set(
                new NioEventLoopGroup(
                        boosThreadCount,
                        new NetThreadFactory("boos-thread-")));

        workerGroup.set(
                new NioEventLoopGroup(
                        workerThreadCount,
                        new NetThreadFactory("worker-thread-")));

        // 设置启动类
        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup.get(), workerGroup.get())
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyChannelInit())
                .option(ChannelOption.SO_BACKLOG, maxWaitList)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture f = b.bind(port).sync();

        LOGGER.info("网络服务器启动");

        // 设置关闭动作
        f.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
            LOGGER.info("网络服务器关闭");
        });
    }

    /**
     * 获取netty是否在运行
     *
     * @return 如果返回true，则说明netty还在运行。
     */
    public static boolean isRun() {
        return IS_RUNNING.get();
    }

    /**
     * 关闭netty服务器
     */
    public static void shutdown() {
        if (IS_RUNNING.getAndSet(false)) {
            var group = bossGroup.getAndSet(null);

            if (group != null)
                group.shutdownGracefully();

            group = workerGroup.getAndSet(null);

            if (group != null)
                group.shutdownGracefully();
        }
    }

}
