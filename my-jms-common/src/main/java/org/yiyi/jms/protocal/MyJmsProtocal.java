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
package org.yiyi.jms.protocal;

import org.yiyi.jms.enums.MsgType;
import org.yiyi.jms.model.MsgCommunicateDTO;
import org.yiyi.jms.protocal.codec.Codec;
import org.yiyi.jms.protocal.codec.Decoder;
import org.yiyi.jms.protocal.codec.Encoder;
import org.yiyi.jms.protocal.codec.LoginCodec;
import org.yiyi.jms.protocal.codec.LogoutCodec;
import org.yiyi.jms.protocal.codec.MessageCodec;
import org.yiyi.jms.protocal.codec.RegisterListenerCodec;
import org.yiyi.jms.protocal.codec.UnregisterListenerCodec;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议主体，包括消息encode/decode
 *
 * 消息类型有5种：
 *      1）登陆（li）
 * 	    入参：xx|xx|xx|x*
 * 		    类型|发布=s，接收=r|模式（queue=q，topic=t）|具体模式的名称，queue name或topic name
 * 	    出参：xx|x*
 * 		    statusCode|附加信息，比如失败异常
 *
 * 	    2）退出（lo）
 * 	    入参：xx
 * 		    类型
 * 	    出参：xx|x*
 * 		    statusCode|附加信息，比如失败异常
 *
 * 	    3）发布实体消息（mg）
 * 	    入参：xx|x*
 * 		    类型|具体消息内容
 * 	    出参：xx|x*
 * 		    statusCode|附加信息，比如失败异常
 *
 * 	    4）接收者注册消息监听（rl）
 * 	    入参：xx
 * 		    类型
 * 	    出参：xx|x*
 * 		    statusCode|附加信息，比如失败异常
 *
 * 	    5）接收者取消消息监听（ul）
 * 	    入参：xx
 * 		    类型
 * 	    出参：xx|x*
 * 		    statusCode|附加信息，比如失败异常
 *
 * @author yi.yi
 * @date 2021.06.25
 */
public class MyJmsProtocal {
    private static Map <String, Encoder> encoderMap;
    private static Map <String, Decoder> decoderMap;

    static {
        Codec loginCodec = new LoginCodec ();
        Codec logoutCodec = new LogoutCodec ();
        Codec messageCodec = new MessageCodec ();
        Codec registerListenerCodec = new RegisterListenerCodec ();
        Codec unregisterListenerCodec = new UnregisterListenerCodec ();

        encoderMap = new HashMap <> ();
        decoderMap = new HashMap <> ();

        encoderMap.put (MsgType.Login.getCode (), loginCodec);
        encoderMap.put (MsgType.Logout.getCode (), logoutCodec);
        encoderMap.put (MsgType.Message.getCode (), messageCodec);
        encoderMap.put (MsgType.RegisterListener.getCode (), registerListenerCodec);
        encoderMap.put (MsgType.UnregisterListener.getCode (), unregisterListenerCodec);

        decoderMap.put (MsgType.Login.getCode (), loginCodec);
        decoderMap.put (MsgType.Logout.getCode (), logoutCodec);
        decoderMap.put (MsgType.Message.getCode (), messageCodec);
        decoderMap.put (MsgType.RegisterListener.getCode (), registerListenerCodec);
        decoderMap.put (MsgType.UnregisterListener.getCode (), unregisterListenerCodec);
    }

    public static byte[] encodeDTO (MsgCommunicateDTO request) {
        return encoderMap.get (request.getMsgType ()).encodeDTO (request);
    }

    public static MsgCommunicateDTO decodeDTO (byte[] bytes) {
        String msgType = decodeMsgType (bytes);
        MsgCommunicateDTO request = decoderMap.get (msgType).decodeDTO (bytes);
        request.setOriginalBytes (bytes);
        return request;
    }

    private static String decodeMsgType (byte[] bytes) {
        if (bytes == null || bytes.length < 2) {
            throw new RuntimeException ("MsgType解码失败！");
        }
        byte[] typeBytes = new byte[2];
        typeBytes[0] = bytes[0];
        typeBytes[1] = bytes[1];
        return new String (typeBytes, StandardCharsets.UTF_8);
    }

    public static void main (String[] args) {
        String s = "li";
        byte[] bytes = s.getBytes (StandardCharsets.UTF_8);
        System.out.println (bytes.length);
        for (byte aByte : bytes) {
            System.out.println (aByte);
        }
    }
}
