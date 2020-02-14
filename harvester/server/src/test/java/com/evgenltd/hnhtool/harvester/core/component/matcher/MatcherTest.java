package com.evgenltd.hnhtool.harvester.core.component.matcher;

import com.evgenltd.hnhtool.harvester.core.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtools.clientapp.Prop;
import com.evgenltd.hnhtools.entity.IntPoint;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-12-2019 00:12</p>
 */
public class MatcherTest {

    @Test
    public void test() {

        final KnownObject treeKnown1 = createKnownObject("tree", 1, 0, LocalDateTime.of(2019, 1, 10, 12, 0), true);
        final KnownObject treeKnown2 = createKnownObject("tree", 1, 0, LocalDateTime.of(2019, 1, 13, 12, 0), true);
        final KnownObject treeKnown3 = createKnownObject("tree", 1, 0, LocalDateTime.of(2019, 1, 20, 12, 0), false);
        final KnownObject treeKnown4 = createKnownObject("tree", 1, 0, LocalDateTime.of(2019, 1, 22, 12, 0), false);
        final KnownObject barrelKnown = createKnownObject("barrel", 3, 5, LocalDateTime.now(), true);

        final Prop treeProp = createProp("tree", 1, 0);
        final Prop bushProp = createProp("bush", 7, 9);

        final MatchingResult<Prop, KnownObject> result = Matcher.matchPropToKnownObject(
                Arrays.asList(treeProp, bushProp),
                Arrays.asList(treeKnown1, treeKnown2, treeKnown3, treeKnown4, barrelKnown)
        );

        Assert.assertEquals(1, result.getLeftNotMatched().size());
        Assert.assertEquals(4, result.getRightNotMatched().size());
        Assert.assertEquals(1, result.getMatches().size());

        Assert.assertEquals(treeProp, result.getMatches().get(0).getLeft());
        Assert.assertEquals(bushProp, result.getLeftNotMatched().get(0));

        Assert.assertEquals(treeKnown1, result.getRightNotMatched().get(0));
        Assert.assertEquals(treeKnown2, result.getMatches().get(0).getRight());
        Assert.assertEquals(treeKnown4, result.getRightNotMatched().get(1));
        Assert.assertEquals(treeKnown3, result.getRightNotMatched().get(2));
        Assert.assertEquals(barrelKnown, result.getRightNotMatched().get(3));
    }

    private KnownObject createKnownObject(final String resourceName, final int x, final int y, final LocalDateTime actual, final boolean lost) {
        final Resource resource = new Resource();
        resource.setName(resourceName);
        final KnownObject knownObject = new KnownObject();
        knownObject.setPosition(new IntPoint(x, y));
        knownObject.setActual(actual);
        knownObject.setLost(lost);
        knownObject.setResource(resource);
        return knownObject;
    }

    private Prop createProp(final String resourceName, final int x, final int y) {
        return new Prop() {
            @Override
            public Long getId() {
                return null;
            }

            @Override
            public IntPoint getPosition() {
                return new IntPoint(x, y);
            }

            @Override
            public boolean isMoving() {
                return false;
            }

            @Override
            public String getResource() {
                return resourceName;
            }
        };
    }


}
