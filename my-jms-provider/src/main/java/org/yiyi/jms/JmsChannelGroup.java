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

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.yiyi.jms.enums.MsgMode;
import org.yiyi.jms.model.MsgCommunicateDTO;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yi.yi
 * @date 2021.06.28
 */
@Component
public class JmsChannelGroup {
    private ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;
    private AtomicInteger queueHandlerIndex = new AtomicInteger (0);
    // 所有客户端连接的集合
    private Map <String, MsgCommunicateDTO> channelDTOMap = new ConcurrentHashMap <> ();
    // 客户端被动拉取
    private Map <String, List <Channel>> negtivePullQueueChannelsMap = new ConcurrentHashMap <> ();
    private Map <String, Collection <Channel>> negtivePullTopicChannelsMap = new ConcurrentHashMap <> ();
    // 主动推送给客户端
    private Map <String, List <Channel>> positivePushQueueChannelsMap = new ConcurrentHashMap <> ();
    private Map <String, Collection <Channel>> positivePushTopicChannelsMap = new ConcurrentHashMap <> ();

    public void addChannel (Channel channel, MsgCommunicateDTO dto) {
        channelDTOMap.put (channel.id ().asLongText (), dto);
        if (MsgMode.Queue.getCode ().equals (dto.getMsgMode ())) {
            List <Channel> channels = negtivePullQueueChannelsMap.computeIfAbsent (dto.getMsgModeName (), k -> new Vector <> ());
            channels.add (channel);
        } else if (MsgMode.Topic.getCode ().equals (dto.getMsgMode ())) {
            Collection <Channel> channels = negtivePullTopicChannelsMap.computeIfAbsent (dto.getMsgModeName (), k -> new ConcurrentLinkedDeque <> ());
            channels.add (channel);
        }
    }

    public void registerListenerForChannel (Channel channel) {
        for (Map.Entry <String, List <Channel>> entry : negtivePullQueueChannelsMap.entrySet ()) {
            Iterator <Channel> it = entry.getValue ().iterator ();
            while (it.hasNext ()) {
                if (it.next ().id ().equals (channel.id ())) {
                    it.remove ();
                    List <Channel> channels = positivePushQueueChannelsMap.computeIfAbsent (entry.getKey (), k -> new Vector <> ());
                    channels.add (channel);
                }
            }
        }
        for (Map.Entry <String, Collection <Channel>> entry : negtivePullTopicChannelsMap.entrySet ()) {
            Iterator <Channel> it = entry.getValue ().iterator ();
            while (it.hasNext ()) {
                if (it.next ().id ().equals (channel.id ())) {
                    it.remove ();
                    Collection <Channel> channels = positivePushTopicChannelsMap.computeIfAbsent (entry.getKey (), k -> new ConcurrentLinkedDeque <> ());
                    channels.add (channel);
                }
            }
        }
    }

    public void unregisterListenerForChannel (Channel channel) {
        for (Map.Entry <String, List <Channel>> entry : positivePushQueueChannelsMap.entrySet ()) {
            Iterator <Channel> it = entry.getValue ().iterator ();
            while (it.hasNext ()) {
                if (it.next ().id ().equals (channel.id ())) {
                    it.remove ();
                    List <Channel> channels = negtivePullQueueChannelsMap.computeIfAbsent (entry.getKey (), k -> new Vector <> ());
                    channels.add (channel);
                }
            }
        }
        for (Map.Entry <String, Collection <Channel>> entry : positivePushTopicChannelsMap.entrySet ()) {
            Iterator <Channel> it = entry.getValue ().iterator ();
            while (it.hasNext ()) {
                if (it.next ().id ().equals (channel.id ())) {
                    it.remove ();
                    Collection <Channel> channels = negtivePullTopicChannelsMap.computeIfAbsent (entry.getKey (), k -> new ConcurrentLinkedDeque <> ());
                    channels.add (channel);
                }
            }
        }
    }

    public void removeChannel (Channel channel) {
        for (Collection <Channel> channels : negtivePullQueueChannelsMap.values ()) {
            channels.remove (channel);
        }
        for (Collection <Channel> channels : negtivePullTopicChannelsMap.values ()) {
            channels.remove (channel);
        }
        for (Collection <Channel> channels : positivePushQueueChannelsMap.values ()) {
            channels.remove (channel);
        }
        for (Collection <Channel> channels : positivePushTopicChannelsMap.values ()) {
            channels.remove (channel);
        }
    }

    public void writeAndFlush (String msgModeCode, String msgModeName, byte[] bytes) throws Throwable {
        if (MsgMode.Queue.getCode ().equals (msgModeCode)) {
            List <Channel> channels = positivePushQueueChannelsMap.get (msgModeName);
            if (!CollectionUtils.isEmpty (channels)) {
                // TODO 路由策略
                Channel c = channels.get (queueHandlerIndex.getAndAdd (1));
                if (queueHandlerIndex.get () == channels.size ()) {
                    queueHandlerIndex.set (0);
                }
                doSendNettyRequest (c, bytes);
            }
        } else if (MsgMode.Topic.getCode ().equals (msgModeCode)) {
            Collection <Channel> channels = positivePushTopicChannelsMap.get (msgModeName);
            for (Channel c : channels) {
                doSendNettyRequest (c, bytes);
            }
        }
    }

    public MsgCommunicateDTO getChannelInfoById (String channelId) {
        return channelDTOMap.get (channelId);
    }

    private void doSendNettyRequest (Channel channel, byte[] bytes) throws Throwable {
        ChannelFuture future = channel.writeAndFlush (bytes);
        if (future.cause () != null) {
            throw future.cause ();
        }
    }
}
