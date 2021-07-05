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
package org.yiyi.jms.queue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.yiyi.jms.model.MsgCommunicateDTO;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author yi.yi
 * @date 2021.07.02
 */
@Component
public class MsgQueueGroup {
    private final Map <String, MsgQueueWrapper> keyQueueMap = new ConcurrentHashMap<> ();
    private final List <MsgQueueWrapper> queues = new Vector <> ();
    private final ThreadLocal <Integer> qIdxThreadLocal = new ThreadLocal <> ();

    public void createMsgQueue (String msgModeCode, String msgModeName) {
        MsgQueueWrapper queue = new MsgQueueWrapper ();
        queue.setMsgModeCode (msgModeCode);
        queue.setMsgModeName (msgModeName);
        queue.setStatusAvaliable ();
        keyQueueMap.putIfAbsent (genQueueKey (msgModeCode, msgModeName), queue);
        queues.add (queue);
    }

    public void removeMsgQueue (String msgModeCode, String msgModeName) {
        keyQueueMap.remove (genQueueKey (msgModeCode, msgModeName));
        queues.removeIf (next -> StringUtils.equalsIgnoreCase (msgModeCode, next.getMsgModeCode ())
                && StringUtils.equalsIgnoreCase (msgModeName, next.getMsgModeName ()));
    }

    public void enqueueMsg (String msgModeCode, String msgModeName, byte[] bytes) {
        MsgQueueWrapper msgQueueWrapper = keyQueueMap.get (genQueueKey (msgModeCode, msgModeName));
        msgQueueWrapper.getQueue ().add (bytes);
    }

    public void registerListener (String msgModeCode, String msgModeName) {
        MsgQueueWrapper mqw = keyQueueMap.get (genQueueKey (msgModeCode, msgModeName));
        mqw.getQueueListenedCount ().addAndGet (1);
    }

    public void unregisterListener (String msgModeCode, String msgModeName) {
        MsgQueueWrapper mqw = keyQueueMap.get (genQueueKey (msgModeCode, msgModeName));
        mqw.getQueueListenedCount ().addAndGet (-1);
    }

    public void handleMsgByFn (Function <MsgCommunicateDTO, Boolean> fn) {
        if (CollectionUtils.isEmpty (queues)) {
            try {
                Thread.sleep (1000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace ();
            }
            return;
        }

        Integer qIdx = qIdxThreadLocal.get ();
        if (qIdx == null || qIdx >= queues.size ()) {
            qIdx = 0;
            qIdxThreadLocal.set (qIdx);
        }
        MsgQueueWrapper mqw = queues.get (qIdx);
        if (CollectionUtils.isEmpty (mqw.getQueue ()) || mqw.getQueueListenedCount ().get () <= 0) {
            try {
                Thread.sleep (1000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace ();
            }
            return;
        }

        qIdxThreadLocal.set (++qIdx);
        boolean occupied = false;
        if (mqw.isAvaliable ()) {
            synchronized (mqw) {
                if (mqw.isAvaliable ()) {
                    mqw.setStatusBusy ();
                    occupied = true;
                }
            }
        }
        if (!occupied) {
            return;
        }
        if (CollectionUtils.isEmpty (mqw.getQueue ()) || mqw.getQueueListenedCount ().get () <= 0) {
            mqw.setStatusAvaliable ();
            return;
        }

        MsgCommunicateDTO dto = new MsgCommunicateDTO ();
        dto.setMsgMode (mqw.getMsgModeCode ());
        dto.setMsgModeName (mqw.getMsgModeName ());
        dto.setOriginalBytes (mqw.getQueue ().peek ());
        if (fn.apply (dto)) {
            mqw.getQueue ().remove ();
        }
        mqw.setStatusAvaliable ();
    }

    private String genQueueKey (String msgModeCode, String msgModeName) {
        return msgModeCode + "###" + msgModeName;
    }
}
