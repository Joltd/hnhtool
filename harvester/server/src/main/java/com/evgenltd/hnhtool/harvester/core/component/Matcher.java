package com.evgenltd.hnhtool.harvester.core.component;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.Comparator;
import java.util.List;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-12-2019 01:31</p>
 */
public class Matcher {

    private static final Comparator<KnownObject> KNOWN_OBJECT_COMPARATOR = Comparator.comparing(KnownObject::getLost)
            .thenComparing(KnownObject::getActual)
            .reversed();

    public static MatcherImpl.Result<Prop, KnownObject> matchPropToKnownObject(final List<Prop> props, final List<KnownObject> knownObjects) {
        return new MatcherImpl().match(props, knownObjects, PropWrapper::new, KnownObjectWrapper::new);
    }

    public static MatcherImpl.Result<ItemWidget, KnownObject> matchItemWidgetToKnownObject(final List<ItemWidget> itemWidgets, final List<KnownObject> knownObjects) {
        return new MatcherImpl().match(itemWidgets, knownObjects, ItemWidgetWrapper::new, KnownObjectWrapper::new);
    }


    private static class KnownObjectWrapper extends MatcherImpl.Wrapper<KnownObject> {
        KnownObjectWrapper(final KnownObject value) {
            super(value);
        }

        @Override
        public String getResource() {
            return getValue().getResource().getName();
        }

        @Override
        public IntPoint getPosition() {
            return getValue().getPosition();
        }

        @Override
        public int compareTo(final MatcherImpl.Wrapper<KnownObject> o) {
            return KNOWN_OBJECT_COMPARATOR.compare(getValue(), o.getValue());
        }
    }

    private static class ItemWidgetWrapper extends MatcherImpl.Wrapper<ItemWidget> {
        ItemWidgetWrapper(final ItemWidget value) {
            super(value);
        }

        @Override
        public String getResource() {
            return getValue().getResource();
        }

        @Override
        public IntPoint getPosition() {
            return getValue().getPosition();
        }
    }

    private static class PropWrapper extends MatcherImpl.Wrapper<Prop> {
        PropWrapper(final Prop value) {
            super(value);
        }

        @Override
        public String getResource() {
            return getValue().getResource();
        }

        @Override
        public IntPoint getPosition() {
            return getValue().getPosition();
        }
    }

}
