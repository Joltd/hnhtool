package com.evgenltd.hnhtool.harvester.loader;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtools.message.DataReader;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-12-2019 00:35</p>
 */
public final class CodeEntry extends AbstractLayer implements Layer {

    private static final int CLASS_NAME = 1;
    private static final int CLASSPATH = 2;

    private final Map<String,String> pe = new HashMap<>();

    public CodeEntry(final Resource resource, final DataReader reader) {
        super(resource);

        while (reader.hasNext()) {
            final int type = reader.uint8();
            if (type == 1) {
                while (true) {
                    final String en = reader.string();
                    final String cn = reader.string();
                    if (en.length() == 0) {
                        break;
                    }
                    pe.put(en, cn);
                }
            } else if (type == 2) {
                while (true) {
                    final String ln = reader.string();
                    if (ln.length() == 0) {
                        break;
                    }
                    final int ver = reader.uint16();
//                    classpath.put(ln, ver);
                }
            }
        }
    }

    @Override
    public void initialization() {

    }
}
