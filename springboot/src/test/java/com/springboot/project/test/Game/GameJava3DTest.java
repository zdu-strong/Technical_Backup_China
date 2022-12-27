package com.springboot.project.test.Game;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;
import com.sun.j3d.utils.picking.PickTool;

public class GameJava3DTest extends BaseTest {

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var boundingSphere = new BoundingSphere(new Point3d(1, 1, 1), 1);
        {
            // 移动到坐标系中心
            var firstTransform3D = new Transform3D();
            firstTransform3D.setTranslation(new Vector3d(-1, -1, -1));
            boundingSphere.transform(firstTransform3D);
            // 旋转, 然后移回原来位置
            var secondTransform3D = new Transform3D();
            secondTransform3D.setRotation(new AxisAngle4d(new Vector3d(0, 1, 0), Math.PI));
            secondTransform3D.setTranslation(new Vector3d(1, 1, 1));
            boundingSphere.transform(secondTransform3D);
        }
        var boundingBox = new BoundingBox(new Point3d(-1, -1, -1), new Point3d(1, 1, 1));
        var result = boundingSphere.intersect(new Bounds[] { boundingBox });
        System.out.println("----------------------------");
        System.out.println(result);

        BranchGroup branchGroup = new BranchGroup();
        branchGroup.setBounds(boundingSphere);
        branchGroup.setPickable(true);
        branchGroup.setCollidable(true);
        branchGroup.detach();
        branchGroup.compile();

        var pickTool = new PickTool(branchGroup);
        pickTool.setShapeRay(new Point3d(0, 0, 0), new Vector3d(1, 1, 1));
        var pickResult = pickTool.pickClosest();
    }

    @BeforeEach
    public void beforeEach() {
        System.setProperty("j3d.rend", "jogl");
    }

}
