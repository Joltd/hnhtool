package com.evgenltd.hnhtools.environment;

import com.evgenltd.hnhtools.message.InboundMessageAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 28-03-2019 19:54</p>
 */
public class Widget {

    private Integer id;
    private String type;
    private List<InboundMessageAccessor.RelAccessor> rels = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public List<InboundMessageAccessor.RelAccessor> getRels() {
        return rels;
    }
}
