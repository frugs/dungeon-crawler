package com.frugs.dungeoncrawler.game

import com.jme3.math.Vector3f
import groovy.transform.CompileStatic
import groovyx.gpars.stm.GParsStm

@CompileStatic
abstract class MoveTowards {

    abstract Vector3f getLocalTranslation()
    abstract float getSpeed()
    abstract void move(Vector3f location)

    //return value is true if we've got more to go
    boolean moveTowardsDestination(Vector3f destination, float tpf) {
        GParsStm.atomicWithBoolean {
            def remainingTravel = destination.subtract(localTranslation)
            def displacement = remainingTravel.normalize().mult(speed).mult(tpf)

            if (remainingTravel.length() == 0) {
                return false
            }

            def stillMoving = displacement.length() < remainingTravel.length()
            stillMoving ? move(displacement) : move(remainingTravel)
            stillMoving
        }
    }
}
