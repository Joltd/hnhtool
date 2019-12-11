package com.evgenltd.hnhtool.harvester.loader;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.service.ResourceService;
import com.evgenltd.hnhtools.message.DataReader;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 06-12-2019 01:17</p>
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
public class Loader {

//    @Autowired
    private ResourceService resourceService;

//    @Test
    public void test() {

        final Resource resource = resourceService.findByName("ui/tt/q/quality");
        if (resource.getContent() == null) {
            return;
        }

        final DataReader reader = new DataReader(resource.getContent().getData());
        final Map<String, Object> layers = new HashMap<>();

        final String header = reader.string();



        while (reader.hasNext()) {
            final String type = reader.string();
            final int len = reader.int32();
            final DataReader contentReader = reader.asReader(len);

            if (type.equals("code")) {
                layers.put(type, new Code(contentReader));
            } else if (type.equals("codeentry")) {
                layers.put(type, new CodeEntry(contentReader));
            } else {
                layers.put(type, contentReader);
            }
        }

        System.out.println();


    }

    private static final class Code {
        private String name;
        private byte[] data;

        Code(final DataReader reader) {
            name = reader.string();
            data = reader.bytes();
        }
    }

    private static final class CodeEntry {

        private final Map<String, String> pe = new HashMap<>();
        private final Map<String, Integer> classpath = new HashMap<>();
        private final Map<String, Code> codeIndex = new HashMap<>();

        CodeEntry(final DataReader reader) {

        }

    }

}
