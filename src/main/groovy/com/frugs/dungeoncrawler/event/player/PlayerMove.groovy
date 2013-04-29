package com.frugs.dungeoncrawler.event.player

import com.frugs.dungeoncrawler.game.Player
import com.frugs.dungeoncrawler.event.Event
import com.frugs.dungeoncrawler.event.Interruptable
import com.frugs.dungeoncrawler.event.Interrupter
import com.frugs.dungeoncrawler.util.Radians
import com.jme3.math.FastMath
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import groovy.transform.CompileStatic

@CompileStatic
class PlayerMove implements Interruptable, Interrupter {

    private static final Vector3f MARGIN = Vector3f.UNIT_XYZ.mult(0.1f)
    final long timeIssued

    Vector3f destination
    Player player

    PlayerMove(Vector3f destination, Player player, long timeIssued) {
        this.destination = destination
        this.player = player
        this.timeIssued = timeIssued
    }

    PlayerMove(Vector3f destination, Player player) {
        this.destination = destination
        this.player = player
        this.timeIssued = System.currentTimeMillis()
    }

    @Override
    void process(float tpf) {
        Vector3f normalisedDirection = destination.subtract(player.localTranslation).normalize()

        float angleToDestination = normalisedDirection.angleBetween(player.facingDirection) - FastMath.PI
        float angularVelocity = angleToDestination > 0 ? player.angularSpeed : -player.angularSpeed

        float rotationAngle = Radians.largerAbsolute(angleToDestination, angularVelocity)
        Vector3f rotationVector = normalisedDirection.cross(player.facingDirection)
        Quaternion rotation = Quaternion.ZERO.fromAngleNormalAxis(rotationAngle, rotationVector)

        player.rotate(rotation)
        player.move(normalisedDirection.mult(player.speed).mult(tpf))
    }

    @Override
    Event getChain() {
        if(reachedDestination()) {
            new PlayerStop(System.currentTimeMillis(), player)
        } else {
            new PlayerMove(destination, player, timeIssued)
        }
    }

    @Override
    List<? extends Interrupter> getInterruptedBy() {
        [PlayerMove, PlayerStop] as List<? extends Interrupter>
    }

    private boolean reachedDestination() {
        Vector3f difference = absolute(player.localTranslation.subtract(destination))
        difference.x < MARGIN.x && difference.y < MARGIN.y && difference.z < MARGIN.z
    }

    private static Vector3f absolute(Vector3f vector) {
        float x = vector.x > 0.0f ? vector.x : -vector.x
        float y = vector.y > 0.0f ? vector.y : -vector.y
        float z = vector.z > 0.0f ? vector.z : -vector.z

        new Vector3f(x, y, z)
    }
}
