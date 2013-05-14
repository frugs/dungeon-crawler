package com.frugs.dungeoncrawler.game

import com.frugs.dungeoncrawler.util.Radians
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.math.FastMath
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.Node
import com.jme3.scene.Spatial
import com.jme3.scene.shape.Dome
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovyx.gpars.stm.GParsStm

import static groovy.transform.TypeCheckingMode.SKIP

@Mixin(MoveTowards)
class Player extends Node implements Movable {

    float speed = 8.0f
    float angularSpeed = FastMath.DEG_TO_RAD * 10.0f * 60

    Vector3f facingDirection

    Player(Material mat) {
        super("player")
        Geometry dome = createDome(mat)
        attachChild(dome)
        this.facingDirection = Vector3f.UNIT_Z
    }

    @CompileStatic
    @Override
    synchronized Spatial rotate(Quaternion rotation) {
        facingDirection = rotation.toRotationMatrix().mult(facingDirection)
        super.rotate(rotation)
    }

    @CompileStatic
    @Override
    synchronized Spatial move(Vector3f offset) {
        super.move(offset)
    }

    @CompileStatic
    //return value is true if we've got more to rotate
    boolean rotateTowardsDestination(Vector3f destination, float tpf) {
        GParsStm.atomicWithBoolean {
            def angleToDestination = FastMath.asin(facingDirection.cross(destination.subtract(localTranslation).normalize()).y)
            float angularVelocity = angularSpeed * tpf * FastMath.sign(angleToDestination)
            float rotation = Radians.smallerAbsolute(angularVelocity, angleToDestination)

            rotate(0.0f, rotation, 0.0f)
            rotation != angleToDestination
        }
    }

    @CompileStatic
    private Geometry createDome(Material mat) {
        Geometry dome = new Geometry("PlayerGeom", new Dome(Vector3f.ZERO, 2, 4, 1.5f, true))
        dome.rotate(FastMath.HALF_PI, 0.0f, 0.0f)
        mat.setColor("Color", ColorRGBA.Blue)
        material = mat
        dome.material = mat
        dome
    }
}
