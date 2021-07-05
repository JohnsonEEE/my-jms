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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.yiyi.jms.enums.MsgMode;
import org.yiyi.jms.enums.Role;
import org.yiyi.jms.model.MsgCommunicateDTO;
import org.yiyi.jms.model.TestContentModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;

/**
 * @author yi.yi
 * @date 2021.06.24
 */
public class JmsClientBootstrap {
    public static void main (String[] args) {
        JmsClient jmsClient = JmsClient.get ();
        new Thread (() -> {
            Producer producer = null;
            Consumer consumer = null;
            int i = 1;
            while (true) {
                try
                {
                    byte[] kb = new byte[1024];
                    int length = System.in.read (kb);
                    byte[] newBytes = Arrays.copyOf (kb, length);
                    String msg = new String (newBytes, StandardCharsets.UTF_8);
                    if (msg.startsWith ("producer")) {
                        producer = jmsClient.createProducer (args[0], args[1]);
                    } else if (msg.startsWith ("consumer")) {
                        consumer = jmsClient.createConsumer (args[0], args[1]);
                    } else if (msg.startsWith ("add")) {
                        consumer.addListener (m -> {
                            System.out.println (ToStringBuilder.reflectionToString (((MsgCommunicateDTO)m).getContent (), ToStringStyle.SHORT_PREFIX_STYLE));
                        });
                    } else if (msg.startsWith ("remove")) {

                    } else {
                        TestContentModel model = new TestContentModel ();
                        model.setNo (i++);
                        model.setRealContent (msg);
                        producer.send (model);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace ();
                }
            }
        }).start ();
        LockSupport.park ();
    }
}
