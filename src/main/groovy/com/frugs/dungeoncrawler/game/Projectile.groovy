package com.frugs.dungeoncrawler.game

import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import com.jme3.scene.Node
import groovy.transform.CompileStatic

@CompileStatic
class Projectile extends Node {

    private final float size = 0.3f

    Projectile(Vector3f location) {
        super(hashCode().toString())
        attachChild(new Geometry(hashCode().toString() + "Geom", new Box(size, size, size)))
        localTranslation = location
    }
}
