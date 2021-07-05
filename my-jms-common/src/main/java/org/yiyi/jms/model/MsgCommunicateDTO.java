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
package org.yiyi.jms.model;

/**
 * @author yi.yi
 * @date 2021.06.25
 */
public class MsgCommunicateDTO {
    private String msgType;
    private String role;
    private String msgMode;
    private String msgModeName;
    private String statusCode;
    private Object content;

    private transient byte[] originalBytes;

    public String getMsgType () {
        return msgType;
    }

    public void setMsgType (String msgType) {
        this.msgType = msgType;
    }

    public String getRole () {
        return role;
    }

    public void setRole (String role) {
        this.role = role;
    }

    public String getMsgMode () {
        return msgMode;
    }

    public void setMsgMode (String msgMode) {
        this.msgMode = msgMode;
    }

    public String getMsgModeName () {
        return msgModeName;
    }

    public void setMsgModeName (String msgModeName) {
        this.msgModeName = msgModeName;
    }

    public String getStatusCode () {
        return statusCode;
    }

    public void setStatusCode (String statusCode) {
        this.statusCode = statusCode;
    }

    public Object getContent () {
        return content;
    }

    public void setContent (Object content) {
        this.content = content;
    }

    public byte[] getOriginalBytes () {
        return originalBytes;
    }

    public void setOriginalBytes (byte[] bytes) {
        this.originalBytes = bytes;
    }
}
