package com.frugs.dungeoncrawler.game

import com.jme3.math.Vector3f
import com.jme3.scene.Spatial
import groovy.transform.CompileStatic
import groovyx.gpars.stm.GParsStm

@CompileStatic
@Category(Spatial)
@Category(Movable)
class MoveTowards {

    //return value is true if we've got more to go
    boolean moveTowardsDestination(Vector3f destination, float tpf) {
        GParsStm.atomicWithBoolean {
            Vector3f remainingTravel = destination.subtract(localTranslation)
            Vector3f displacement = remainingTravel.normalize().mult((this as Movable).speed as float).mult(tpf)

            if (remainingTravel.length() == 0) {
                return false
            }

            def stillMoving = displacement.length() < remainingTravel.length()
            stillMoving ? (this as Spatial).move(displacement) : (this as Spatial).move(remainingTravel)
            stillMoving
        }
    }
}
