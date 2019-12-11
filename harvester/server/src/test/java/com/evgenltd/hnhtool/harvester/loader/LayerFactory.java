package com.evgenltd.hnhtool.harvester.loader;

import com.evgenltd.hnhtools.message.DataReader;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 11-12-2019 00:44</p>
 */
public class LayerFactory {

    public static Layer build(final DataReader reader) {
        final String type = reader.string();
        final int len = reader.int32();
        final DataReader contentReader = reader.asReader(len);

        switch (type) {
            case "code":
                return null;
            case "codeentry":
                return null;
        }

        return null;
    }

}
