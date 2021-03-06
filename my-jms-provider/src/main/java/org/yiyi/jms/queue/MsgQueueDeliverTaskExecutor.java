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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yiyi.jms.JmsChannelGroup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yi.yi
 * @date 2021.07.02
 */
@Component
public class MsgQueueDeliverTaskExecutor implements InitializingBean {
    @Autowired
    private MsgQueueGroup msgQueueGroup;
    @Autowired
    private JmsChannelGroup jmsChannelGroup;

    private final static int threadNum = 5;
    private final ExecutorService pool = Executors.newFixedThreadPool (threadNum);

    @Override
    public void afterPropertiesSet () throws Exception {
        for (int i = 0; i < threadNum; i++) {
            pool.execute (new MsgQueueDeliverTask ());
        }
    }

    private class MsgQueueDeliverTask implements Runnable {

        @Override
        public void run () {
            for (;;) {
                try {
                    msgQueueGroup.handleMsgByFn (dto -> {
                        try {
                            jmsChannelGroup.writeAndFlush (dto.getMsgMode (), dto.getMsgModeName (), dto.getOriginalBytes ());
                        }
                        catch (Throwable throwable) {
                            throwable.printStackTrace ();
                            return false;
                        }
                        return true;
                    });
                }
                catch (Exception e) {
                    e.printStackTrace ();
                    break;
                }
            }
        }
    }
}
