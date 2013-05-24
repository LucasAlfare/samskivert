//
// samskivert library - useful routines for java programs
// Copyright (C) 2001-2012 Michael Bayne, et al.
// http://github.com/samskivert/samskivert/blob/master/COPYING

package com.samskivert.util;

import static com.samskivert.util.UtilLog.log;

/**
 * Manages the lifecycle (initialization and shutdown) of a collection of components.
 */
public class Lifecycle
{
    /** An interface implemented by components which wish to participate in the lifecycle. */
    public interface BaseComponent
    {
    }

    /** An interface implemented by components which wish to participate in the lifecycle. */
    public interface InitComponent extends BaseComponent
    {
        /** Called after dependencies have been fully resolved to initialize this component. */
        public void init ();
    }

    /** An interface implemented by components which wish to participate in the lifecycle. */
    public interface ShutdownComponent extends BaseComponent
    {
        /** Called when the server is shutting down. */
        public void shutdown ();
    }

    /** An interface implemented by components which wish to participate in the lifecycle. */
    public interface Component extends InitComponent, ShutdownComponent
    {
    }

    /** Constraints for use with {@link #addInitConstraint} and {@link #addShutdownConstraint}. */
    public static enum Constraint { RUNS_BEFORE, RUNS_AFTER }

    /**
     * Registers a component with the lifecycle. This should be done during dependency resolution
     * by injecting the Lifecycle into your constructor and calling this method there.
     */
    public void addComponent (BaseComponent comp)
    {
        if (comp instanceof InitComponent) {
            if (_initers == null) {
                throw new IllegalStateException("Too late to register InitComponent.");
            }
            _initers.add((InitComponent)comp);
        }
        if (comp instanceof ShutdownComponent) {
            if (_downers == null) {
                throw new IllegalStateException("Too late to register ShutdownComponent.");
            }
            _downers.add((ShutdownComponent)comp);
        }
    }

    /**
     * Removes a component from the lifecycle. This is generally not used.
     */
    public void removeComponent (BaseComponent comp)
    {
        if (_initers != null && comp instanceof InitComponent) {
            _initers.remove((InitComponent)comp);
        }
        if (_downers != null  && comp instanceof ShutdownComponent) {
            _downers.remove((ShutdownComponent)comp);
        }
    }

    /**
     * Adds a constraint that a certain component must be initialized before another.
     */
    public void addInitConstraint (InitComponent lhs, Constraint constraint, InitComponent rhs)
    {
        if (lhs == null || rhs == null) {
            throw new IllegalArgumentException("Cannot add constraint about null component.");
        }
        InitComponent before = (constraint == Constraint.RUNS_BEFORE) ? lhs : rhs;
        InitComponent after = (constraint == Constraint.RUNS_BEFORE) ? rhs : lhs;
        _initers.addDependency(after, before);
    }

    /**
     * Adds a constraint that a certain component must be shutdown before another.
     */
    public void addShutdownConstraint (ShutdownComponent lhs, Constraint constraint,
                                       ShutdownComponent rhs)
    {
        if (lhs == null || rhs == null) {
            throw new IllegalArgumentException("Cannot add constraint about null component.");
        }
        ShutdownComponent before = (constraint == Constraint.RUNS_BEFORE) ? lhs : rhs;
        ShutdownComponent after = (constraint == Constraint.RUNS_BEFORE) ? rhs : lhs;
        _downers.addDependency(after, before);
    }

    /**
     * Returns true if we're in the process of shutting down.
     */
    public boolean isShuttingDown ()
    {
        return (_downers == null);
    }

    /**
     * Initializes all components immediately on the caller's thread.
     */
    public void init ()
    {
        if (_initers == null) {
            log.warning("Refusing repeat init() request.");
            return;
        }

        ObserverList<InitComponent> list = _initers.toObserverList();
        _initers = null;
        list.apply(new ObserverList.ObserverOp<InitComponent>() {
            public boolean apply (InitComponent comp) {
                log.debug("Initializing component", "comp", comp);
                comp.init();
                return true;
            }
        });
    }

    /**
     * Shuts down all components immediately on the caller's thread.
     */
    public void shutdown ()
    {
        if (_downers == null) {
            log.warning("Refusing repeat shutdown() request.");
            return;
        }

        ObserverList<ShutdownComponent> list = _downers.toObserverList();
        _downers = null;
        list.apply(new ObserverList.ObserverOp<ShutdownComponent>() {
            public boolean apply (ShutdownComponent comp) {
                log.debug("Shutting down component", "comp", comp);
                comp.shutdown();
                return true;
            }
        });
    }

    /** A dependency graph of our components arranged by initialization dependencies. */
    protected DependencyGraph<InitComponent> _initers = new DependencyGraph<InitComponent>();

    /** A dependency graph of our components arranged by shutdown dependencies. */
    protected DependencyGraph<ShutdownComponent> _downers =
        new DependencyGraph<ShutdownComponent>();
}
