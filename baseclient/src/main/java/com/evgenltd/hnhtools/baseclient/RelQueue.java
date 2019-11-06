package com.evgenltd.hnhtools.baseclient;

import com.evgenltd.hnhtools.message.Message;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 12-03-2019 23:07</p>
 */
public interface RelQueue {

    void push(Message.Rel rel);

}
