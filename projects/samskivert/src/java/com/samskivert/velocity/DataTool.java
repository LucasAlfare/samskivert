//
// $Id: DataTool.java,v 1.3 2004/01/05 19:13:30 eric Exp $

package com.samskivert.velocity;

import java.lang.reflect.Array;

/**
 * Some helpful methods for dealing with data in velocity.
 */
public class DataTool
{
    /**
     * Returns the object at location zero in the array, super useful for
     * those length 1 arrays (like when you are storing primitives in a
     * hash)
     */
    public Object unwrap (Object array)
    {
        return get(array, 0);
    }

    /**
     * Returns the object in the array at index.  Wrapped as an object if
     * it is a primitive.
     */
    public Object get (Object array, int index)
    {
        return (array == null) ? null : Array.get(array, index);
    }

    /**
     * Returns the length of the specified array.
     */
    public int length (Object array)
    {
        return (array == null) ? 0 : Array.getLength(array);
    }

    /**
     * Floating point divide.
     */
    public float div (float a, float b)
    {
        return a/b;
    }

    /**
     * Double addition.
     */
    public double add (double a, double b)
    {
        return a + b;
    }
}
