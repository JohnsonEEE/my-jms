/*
 *
 *
 * Copyright ( c ) 2021 TH Supcom Corporation. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TH Supcom
 * Corporation ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with TH Supcom Corporation or a TH Supcom
 * authorized reseller (the "License Agreement"). TH Supcom may make changes to the
 * Confidential Information from time to time. Such Confidential Information may
 * contain errors.
 *
 * EXCEPT AS EXPLICITLY SET FORTH IN THE LICENSE AGREEMENT, TH Supcom DISCLAIMS ALL
 * WARRANTIES, COVENANTS, REPRESENTATIONS, INDEMNITIES, AND GUARANTEES WITH
 * RESPECT TO SOFTWARE AND DOCUMENTATION, WHETHER EXPRESS OR IMPLIED, WRITTEN OR
 * ORAL, STATUTORY OR OTHERWISE INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, TITLE, NON-INFRINGEMENT AND FITNESS FOR A
 * PARTICULAR PURPOSE. TH Supcom DOES NOT WARRANT THAT END USER'S USE OF THE
 * SOFTWARE WILL BE UNINTERRUPTED, ERROR FREE OR SECURE.
 *
 * TH Supcom SHALL NOT BE LIABLE TO END USER, OR ANY OTHER PERSON, CORPORATION OR
 * ENTITY FOR INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY OR CONSEQUENTIAL
 * DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR USE, WHETHER IN AN
 * ACTION IN CONTRACT, TORT OR OTHERWISE, EVEN IF TH Supcom HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. TH Supcom' TOTAL LIABILITY TO END USER SHALL NOT
 * EXCEED THE AMOUNTS PAID FOR THE TH Supcom SOFTWARE BY END USER DURING THE PRIOR
 * TWELVE (12) MONTHS FROM THE DATE IN WHICH THE CLAIM AROSE.  BECAUSE SOME
 * STATES OR JURISDICTIONS DO NOT ALLOW LIMITATION OR EXCLUSION OF CONSEQUENTIAL
 * OR INCIDENTAL DAMAGES, THE ABOVE LIMITATION MAY NOT APPLY TO END USER.
 *
 * Copyright version 2.0
 */
package org.yiyi.jms;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.yiyi.jms.enums.MsgMode;
import org.yiyi.jms.enums.MsgType;
import org.yiyi.jms.enums.Role;
import org.yiyi.jms.handler.DelimiterBasedFrameEncoder;
import org.yiyi.jms.model.MsgCommunicateDTO;
import org.yiyi.jms.protocal.MyJmsProtocal;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * @author yi.yi
 * @date 2021.06.24
 */
public class JmsNettyClient {
    private Channel channel;
    private JmsClientHandler jmsClientHandler;

    public void sendMsg (Object msg) {
        MsgCommunicateDTO request = new MsgCommunicateDTO ();
        request.setMsgType (MsgType.Message.getCode ());
        request.setContent (msg);

        byte[] bytes = MyJmsProtocal.encodeDTO (request);
        doSendNettyRequest (bytes);
    }

    public JmsNettyClient () throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup ();
        try {
            jmsClientHandler = new JmsClientHandler ();
            Bootstrap b = new Bootstrap ();
            b.group (group)
                    .channel (NioSocketChannel.class)
                    .remoteAddress (new InetSocketAddress ("127.0.0.1", 666))
                    .handler (new ChannelInitializer <SocketChannel> () {
                        @Override
                        public void initChannel (SocketChannel ch) {
                            ch.pipeline ().addLast (new DelimiterBasedFrameDecoder (Integer.MAX_VALUE,
                                    Unpooled.copiedBuffer ("~~~".getBytes (StandardCharsets.UTF_8))));
                            ch.pipeline ().addLast (new DelimiterBasedFrameEncoder ("~~~"));
                            ch.pipeline ().addLast (jmsClientHandler);
                        }
                    });
            channel = b.connect ().sync ().channel ();
            System.out.println ("JmsClient 初始化完成");
        }
        catch (Throwable t) {
            t.printStackTrace ();
            group.shutdownGracefully ().sync ();
        }
    }

    public void login (Role role, MsgMode msgMode, String msgModeName) {
        MsgCommunicateDTO request = new MsgCommunicateDTO ();
        request.setMsgType (MsgType.Login.getCode ());
        request.setRole (role.getCode ());
        request.setMsgMode (msgMode.getCode ());
        request.setMsgModeName (msgModeName);

        byte[] bytes = MyJmsProtocal.encodeDTO (request);
        doSendNettyRequest (bytes);
    }

    public void logout () {
        MsgCommunicateDTO request = new MsgCommunicateDTO ();
        request.setMsgType (MsgType.Logout.getCode ());

        byte[] bytes = MyJmsProtocal.encodeDTO (request);
        doSendNettyRequest (bytes);
    }

    public void registerListener (Consumer listener) {
        boolean existListener = jmsClientHandler.isExistListener ();
        jmsClientHandler.addConsumerListener (listener);
        if (!existListener) {
            MsgCommunicateDTO request = new MsgCommunicateDTO ();
            request.setMsgType (MsgType.RegisterListener.getCode ());

            byte[] bytes = MyJmsProtocal.encodeDTO (request);
            doSendNettyRequest (bytes);
        }
    }

    public void unregisterListener (Consumer listener) {
        jmsClientHandler.removeConsumerListener (listener);
        if (!jmsClientHandler.isExistListener ()) {
            MsgCommunicateDTO request = new MsgCommunicateDTO ();
            request.setMsgType (MsgType.UnregisterListener.getCode ());

            byte[] bytes = MyJmsProtocal.encodeDTO (request);
            doSendNettyRequest (bytes);
        }
    }

    private void doSendNettyRequest (byte[] bytes) {
        ChannelFuture future = channel.writeAndFlush (bytes);
        if (future.cause () != null) {
            future.cause ().printStackTrace ();
        }
    }
}
