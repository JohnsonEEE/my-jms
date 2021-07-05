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
package org.yiyi.jms.protocal.codec;

import org.apache.commons.lang3.ArrayUtils;
import org.yiyi.jms.model.MsgCommunicateDTO;
import org.yiyi.jms.protocal.utils.CodecHelper;

import java.nio.charset.StandardCharsets;

/**
 *
 * 登陆（li）
 * 	入参：xx|xx|xx|x*
 * 		类型|发布=s，接收=r|模式（queue=q，topic=t）|具体模式的名称，queue name或topic name
 * 	出参：xx|x*
 * 		statusCode|附加信息，比如失败异常
 *
 * @author yi.yi
 * @date 2021.06.25
 */
public class LoginCodec implements Codec {

    @Override
    public MsgCommunicateDTO decodeDTO (byte[] bytes) {
        byte[] msgTypeBytes = CodecHelper.subarrayRemoveZeroFromHead (bytes, 0, 2);
        byte[] roleBytes = CodecHelper.subarrayRemoveZeroFromHead (bytes, 2, 4);
        byte[] msgModeBytes = CodecHelper.subarrayRemoveZeroFromHead (bytes, 4, 6);
        byte[] msgModenNameBytes = ArrayUtils.subarray (bytes, 6, bytes.length);

        MsgCommunicateDTO request = new MsgCommunicateDTO ();
        request.setMsgType (new String (msgTypeBytes, StandardCharsets.UTF_8));
        request.setRole (new String (roleBytes, StandardCharsets.UTF_8));
        request.setMsgMode (new String (msgModeBytes, StandardCharsets.UTF_8));
        request.setMsgModeName (new String (msgModenNameBytes, StandardCharsets.UTF_8));
        return request;
    }

    @Override
    public byte[] encodeDTO (MsgCommunicateDTO request) {
        byte[] msgTypeBytes = CodecHelper.fillZeroToHead (request.getMsgType ().getBytes(StandardCharsets.UTF_8), 2);
        byte[] roleBytes = CodecHelper.fillZeroToHead (request.getRole ().getBytes(StandardCharsets.UTF_8), 2);
        byte[] msgModeBytes = CodecHelper.fillZeroToHead (request.getMsgMode ().getBytes(StandardCharsets.UTF_8), 2);
        byte[] msgModenNameBytes = request.getMsgModeName ().getBytes(StandardCharsets.UTF_8);

        byte[] bytes = ArrayUtils.addAll (msgTypeBytes, roleBytes);
        bytes = ArrayUtils.addAll (bytes, msgModeBytes);
        bytes = ArrayUtils.addAll (bytes, msgModenNameBytes);
        return bytes;
    }

    public static void main (String[] args) {
        byte[] msgTypeBytes = {1, 2};
        byte[] roleBytes = {3, 4};
        byte[] bytes = ArrayUtils.addAll (msgTypeBytes, roleBytes);
        for (byte aByte : bytes) {
            System.out.println (aByte);
        }
    }
}
