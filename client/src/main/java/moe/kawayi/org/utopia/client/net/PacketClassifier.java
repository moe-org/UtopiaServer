//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// The PacketClassifier.java is a part of project utopia, under MIT License.
// See https://opensource.org/licenses/MIT for license information.
// Copyright (c) 2021 moe-org All rights reserved.
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

package moe.kawayi.org.utopia.client.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.FastThreadLocal;
import moe.kawayi.org.utopia.core.net.PackageTypeEnum;
import moe.kawayi.org.utopia.core.net.packet.PingPacket;
import moe.kawayi.org.utopia.core.ubf.converter.BinaryConverter;
import moe.kawayi.org.utopia.core.util.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;

/**
 * 包分类器。根据包id进行分类。
 *
 * 我们已经使用{@link LengthFieldBasedFrameDecoder}来解码长度数据了。所以我们不需要再检查长度。
 *
 * 线程安全的
 */
public class PacketClassifier extends ByteToMessageDecoder {

    /**
     * 获取服务器版本号的key(用于{@link ChannelHandlerContext#channel()#attr(AttributeKey)})
     */
    public static final String CHANNEL_SERVER_PING_VERSION = "utopia.client.received_ping_packet.server_version";

    private final Logger logger = LogManager.getLogger(this.getClass());

    @NotNull
    private final FastThreadLocal<BinaryConverter.ConvertFrom> converter = new FastThreadLocal<>(){
        @Override
        protected BinaryConverter.ConvertFrom initialValue() throws Exception {
            return new BinaryConverter.ConvertFrom();
        }
    };


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 读取数据类型
        var packetType = in.readInt();

        // LengthFieldBasedFrameDecoder为我们准备好了完整的数据长度
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);

        // 分类数据
        if(packetType == PackageTypeEnum.PING.getTypeId()){
            System.out.println("received");

            try(var I_BYTES = new ByteArrayInputStream(data)){
                try(var I_DATA = new DataInputStream(I_BYTES)){
                    var nbt = converter.get().convert(I_DATA);

                    var attr = ctx.channel().attr(AttributeKey.valueOf(CHANNEL_SERVER_PING_VERSION));

                    attr.set(nbt.get(PingPacket.UBF_VERSION_KEY).orElseThrow().getString().orElseThrow());
                }
            }
        } else if(packetType == PackageTypeEnum.COMMAND.getTypeId()){
            logger.debug("received command type packet");
        }
        else{
            logger.debug("received unknown type packet");
        }
    }
}
