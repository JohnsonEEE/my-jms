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
package org.yiyi.jms.protocal.utils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author yi.yi
 * @date 2021.06.28
 */
public class CodecHelper {
    public static byte[] fillZeroToHead (byte[] bytes, int length) {
        if (bytes.length >= length) {
            return bytes;
        }
        byte[] complete = new byte[length];
        for (int i = -1; i >= -length; i--) {
            complete [length + i] = (bytes.length + i < 0) ? 0 : bytes[bytes.length + i];
        }
        return complete;
    }

    public static byte[] removeZeroFromHead (byte[] bytes) {
        byte[] trimed = null;
        for (int i = 0; i < bytes.length; i++) {
            if (trimed == null && bytes[i] != 0) {
                trimed = new byte[bytes.length - i];
            }
            if (trimed != null) {
                trimed[i - (bytes.length - trimed.length)] = bytes[i];
            }
        }
        return trimed;
    }

    public static byte[] subarrayRemoveZeroFromHead (byte[] bytes, int startIndexInclusive, int endIndexExclusive) {
        return removeZeroFromHead (ArrayUtils.subarray (bytes, startIndexInclusive, endIndexExclusive));
    }

    public static void main (String[] args) {
        byte[] a = {0, 0, 0, 1, 2, 3, 4};
        byte[] bytes = removeZeroFromHead (a);
        for (byte aByte : bytes) {
            System.out.print (aByte + " ");
        }
        System.out.println ();
        byte[] bytes1 = fillZeroToHead (bytes, 10);
        for (byte b : bytes1) {
            System.out.print (b + " ");
        }
    }
}
