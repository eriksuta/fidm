package com.esuta.fidm.repository.schema.core;

import java.io.Serializable;

/**
 *  A type of change made on some attribute of an object. However simple
 *  this enumeration type may seem, many depends on it in provisioning
 *  engine.
 *
 *  @author shood
 * */
public enum ModificationType implements Serializable{
    ALL,
    ADD,
    DELETE,
    MODIFY
}
