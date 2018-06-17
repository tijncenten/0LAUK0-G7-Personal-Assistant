package pma.layer;

import java.io.InputStream;

/**
 *
 * @author s167501
 */
public interface Storable {
    public void save(String path, String name);
    public void load(String path, String name);
    
    public void load(InputStream is);
}
