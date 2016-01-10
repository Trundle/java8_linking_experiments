package de.hammerhartes.andy.linkingtest.routing;

import java.util.Optional;

public class UriTemplateHelper {

    public static String joinTemplates(final Optional<String> parent,
                                       final Optional<String> child) {
        if (parent.isPresent()) {
            if (child.isPresent()) {
                final String childPath = child.get();
                String template = parent.get();
                if (!template.endsWith("/")) {
                    template += "/";
                }
                return template +
                       (childPath.startsWith("/")
                        ? childPath.substring(1)
                        : childPath);
            } else {
                return parent.get();
            }
        } else {
            if (!child.isPresent()) {
                throw new IllegalArgumentException("Either parent or child must be present!");
            }
            return child.get();
        }
    }

    private UriTemplateHelper() {
    }
}
