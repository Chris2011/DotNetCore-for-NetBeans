package org.chrisle.netbeans.plugins.csharp4netbeans.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ChrisLE
 */
public class ProjectGenerator {
    private List<String> _folder;

    public ProjectGenerator() {
        this._folder = new ArrayList<>();
        this._folder.add("bin");
        this._folder.add("bin\\Debug");
    }
    
}
