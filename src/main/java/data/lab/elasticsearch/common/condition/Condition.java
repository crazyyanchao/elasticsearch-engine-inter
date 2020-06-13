package data.lab.elasticsearch.common.condition;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.elasticsearch.model.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(MUST SHOULD MUST_NOT条件的抽象父类)
 * @date 2019/8/3 18:10
 */
public abstract class Condition {

    private List<Shape> list = new ArrayList();

    public Condition add(Shape shape) {
        this.list.add(shape);
        return this;
    }

    public Condition addMulti(Shape... _shape) {
        Shape[] shapes = _shape;
        for (int i = 0; i < shapes.length; i++) {
            Shape shape = shapes[i];
            this.list.add(shape);
        }
        return this;
    }

    public List getList() {
        return this.list;
    }

    public void clear() {
        this.list.clear();
    }

    public Condition addMulti(List<Shape> areas) {
        this.list = areas;
        return this;
    }
}

