package com.springboot.project.test.Game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.springboot.project.test.BaseTest;
import com.badlogic.gdx.math.Matrix4;

public class GameRotateBoxOfGdxTest extends BaseTest {

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var box = new BoundingBox(new Vector3(0, 0, 0), new Vector3(2, 2, 2));
        {
            /* 移动BoundingBox的中心到坐标系中点, 然后旋转, 旋转结束后移动BoudingBox到原来位置 */
            var centerOfBox = new Vector3();
            box.getCenter(centerOfBox);
            var reverseCenterBox = new Vector3(-centerOfBox.x, -centerOfBox.y, -centerOfBox.z);
            box.mul(new Matrix4().setTranslation(reverseCenterBox));
            box.mul(new Matrix4().rotate(new Vector3(1, 1, 1), 90));
            box.mul(new Matrix4().setTranslation(centerOfBox));
        }
        assertEquals(-0.488034, Math.floor(box.min.x * 1000000) / 1000000);
        assertEquals(-0.488034, Math.floor(box.min.y * 1000000) / 1000000);
        assertEquals(-0.488034, Math.floor(box.min.z * 1000000) / 1000000);
        assertEquals(2.488033, Math.floor(box.max.x * 1000000) / 1000000);
        assertEquals(2.488033, Math.floor(box.max.y * 1000000) / 1000000);
        assertEquals(2.488033, Math.floor(box.max.z * 1000000) / 1000000);
    }

}
