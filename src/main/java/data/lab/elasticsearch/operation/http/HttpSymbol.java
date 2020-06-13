package data.lab.elasticsearch.operation.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME:
 * @Description: TODO(HTTP池 - 标签接口)
 * @date 2019/7/9 14:40
 */
public interface HttpSymbol {
    /**
     * Returns the name of the label. The name uniquely identifies a
     * label, i.e. two different HttpSymbol instances with different object identifiers
     * (and possibly even different classes) are semantically equivalent if they
     * have {@link String#equals(Object) equal} names.
     *
     * @return the name of the label
     */
    String name();
}
