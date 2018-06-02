package pma.layer;

/**
 *
 * @author s167501
 */
public interface Storable {
    public void save(String path, String name);
    public void load(String path, String name);
}
