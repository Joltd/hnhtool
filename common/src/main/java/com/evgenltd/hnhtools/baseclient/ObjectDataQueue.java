package com.evgenltd.hnhtools.baseclient;

import com.evgenltd.hnhtools.message.InboundMessageAccessor;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool</p>
 * <p>Author:  lebed</p>
 * <p>Created: 12-03-2019 23:08</p>
 */
public interface ObjectDataQueue {

    void push(InboundMessageAccessor.ObjectDataAccessor objectDataAccessor);

}