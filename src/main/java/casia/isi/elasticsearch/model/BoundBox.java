package casia.isi.elasticsearch.model;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import java.util.Objects;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.model
 * @Description: TODO(矩形框检索条件)
 * @date 2019/7/16 11:17
 */
public class BoundBox implements Shape {
    /**
     * 设置对角点的两个点
     **/
    private BoundPoint firstBoundPoint;
    private BoundPoint nextBoundPoint;

    public BoundBox() {
    }

    public BoundBox(BoundPoint firstBoundPoint, BoundPoint nextBoundPoint) {
        this.firstBoundPoint = firstBoundPoint;
        this.nextBoundPoint = nextBoundPoint;
    }

    public BoundPoint getFirstBoundPoint() {
        return firstBoundPoint;
    }

    public void setFirstBoundPoint(BoundPoint firstBoundPoint) {
        this.firstBoundPoint = firstBoundPoint;
    }

    public BoundPoint getNextBoundPoint() {
        return nextBoundPoint;
    }

    public void setNextBoundPoint(BoundPoint nextBoundPoint) {
        this.nextBoundPoint = nextBoundPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundBox boundBox = (BoundBox) o;
        return Objects.equals(firstBoundPoint, boundBox.firstBoundPoint) &&
                Objects.equals(nextBoundPoint, boundBox.nextBoundPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstBoundPoint, nextBoundPoint);
    }

    @Override
    public String toString() {
        return "BoundBox{" +
                "firstBoundPoint=" + firstBoundPoint +
                ", nextBoundPoint=" + nextBoundPoint +
                '}';
    }
}

