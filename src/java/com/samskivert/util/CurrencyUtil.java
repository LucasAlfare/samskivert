//
// $Id: CurrencyUtil.java,v 1.1 2003/11/05 00:07:53 eric Exp $
//
// samskivert library - useful routines for java programs
// Copyright (C) 2001 Michael Bayne
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.samskivert.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Currency related utility functions.
 */
public class CurrencyUtil
{
    /**
     * Converts a number representing pennies to a currency display string
     * using the supplied local.
     */
    public static String currencyPennies (double value, Locale locale)
    {
        return currency(value / 100.0, locale);
    }

    /**
     * Converts a number representing dollars to a currency display string
     * using the supplied locale.
     */
    public static String currency (double value, Locale locale)
    {
        NumberFormat numberFormatter =
            NumberFormat.getCurrencyInstance(locale);

        return currency(value, numberFormatter);
    }

    /**
     * Converts a number representing pennies to a currency display string
     * using the default local.
     */
    public static String currencyPennies (double value)
    {
        return currency(value / 100.0, _defaultFormatter);
    }

    /**
     * Converts a number representing dollars to a currency display string
     * using the default locale.
     */
    public static String currency (double value)
    {
        return currency(value, _defaultFormatter);
    }

    /**
     * Converts a number representing dollars to a currency display
     * string using the supplied number format.
     */
    protected static String currency (double value, NumberFormat nformat)
    {
        return nformat.format(value);
    }

    /** A number format for the default local. */
    protected static NumberFormat _defaultFormatter =
            NumberFormat.getCurrencyInstance();
}