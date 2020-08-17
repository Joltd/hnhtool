package com.evgenltd.hnhtool.harvester.core.component.matcher;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.clientapp.widgets.ItemWidget;
import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.Comparator;
import java.util.List;

public final class Matcher {

    private static final Comparator<KnownObject> KNOWN_OBJECT_COMPARATOR = Comparator.comparing(KnownObject::getLost)
            .thenComparing(KnownObject::getActual)
            .reversed();

    public static MatchingResult<Prop, KnownObject> matchPropToKnownObject(final List<Prop> props, final List<KnownObject> knownObjects, final IntPoint offset) {
        return new MatcherImpl().match(props, knownObjects, prop -> new PropWrapper(prop, offset), KnownObjectWrapper::new);
    }

    public static MatchingResult<ItemWidget, KnownObject> matchItemWidgetToKnownObject(final List<ItemWidget> itemWidgets, final List<KnownObject> knownObjects, final Flag... flags) {
        return new MatcherImpl().match(itemWidgets, knownObjects, ItemWidgetWrapper::new, KnownObjectWrapper::new, flags);
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
        private final IntPoint offset;

        PropWrapper(final Prop value, final IntPoint offset) {
            super(value);
            this.offset = offset;
        }

        @Override
        public String getResource() {
            return getValue().getResource();
        }

        @Override
        public IntPoint getPosition() {
            return getValue().getPosition().add(offset);
        }
    }

    public enum Flag {
        SKIP_POSITION
    }

}
