//
// $Id: VGroupLayout.java,v 1.1 2000/12/07 05:41:07 mdb Exp $

package com.samskivert.swing;

import java.awt.*;

public class VGroupLayout extends GroupLayout
{
    public VGroupLayout (int policy, int offpolicy, int gap,
			 int justification)
    {
	_policy = policy;
	_offpolicy = offpolicy;
	_gap = gap;
	_justification = justification;
    }

    public VGroupLayout (int policy, int gap, int justification)
    {
	_policy = policy;
	_gap = gap;
	_justification = justification;
    }

    public VGroupLayout (int policy, int justification)
    {
	_policy = policy;
	_justification = justification;
    }

    public VGroupLayout (int policy)
    {
	_policy = policy;
    }

    public VGroupLayout ()
    {
    }

    protected Dimension getLayoutSize (Container parent, int type)
    {
	DimenInfo info = computeDimens(parent, type);
	Dimension dims = new Dimension();

	switch (_policy) {
	case STRETCH:
	case EQUALIZE:
	    dims.height = info.maxhei * (info.count - info.numfix) +
		info.fixhei + _gap * info.count;
	    break;

	case NONE:
	default:
	    dims.height = info.tothei + _gap * info.count;
	    break;
	}

	dims.height -= _gap;
	dims.width = info.maxwid;

	return dims;
    }

    public void layoutContainer (Container parent)
    {
	Rectangle b = parent.bounds();
	DimenInfo info = computeDimens(parent, PREFERRED);

	int nk = parent.getComponentCount();
	int sx = 0, sy = 0;
	int tothei, totgap = _gap * (info.count-1);
	int freecount = info.count - info.numfix;

	// do the on-axis policy calculations
	int defhei = 0;
	switch (_policy) {
	case STRETCH:
	    if (freecount > 0) {
		defhei = (b.height - info.fixhei - totgap) / freecount;
		tothei = b.height;
	    } else {
		tothei = info.fixhei + totgap;
	    }
	    break;

	case EQUALIZE:
	    defhei = info.maxhei;
	    tothei = info.fixhei + defhei * freecount + totgap;
	    break;

	default:
	case NONE:
	    tothei = info.tothei + totgap;
	    break;
	}

	// do the off-axis policy calculations
	int defwid = 0;
	switch (_offpolicy) {
	case STRETCH:
	    defwid = b.width;
	    break;

	case EQUALIZE:
	    sx = (b.width - info.maxwid)/2;
	    defwid = info.maxwid;
	    break;

	default:
	case NONE:
	    break;
	}

	// do the justification-related calculations
	switch (_justification) {
	case CENTER:
	    sy = (b.height - tothei)/2;
	    break;
	case RIGHT:
	    sy = b.height - tothei;
	    break;
	}

	// do the layout
	for (int i = 0; i < nk; i++) {
	    // skip non-visible kids
	    if (info.dimens[i] == null) {
		continue;
	    }

	    Component child = parent.getComponent(i);
	    int newhei = defhei;
	    int newwid = defwid;

	    if (_policy == NONE || isFixed(child)) {
		newhei = info.dimens[i].height;
	    }

	    if (_offpolicy == NONE) {
		newwid = info.dimens[i].width;
		sx = (b.width - newwid)/2;
	    }

	    child.setBounds(sx, sy, newwid, newhei);
	    sy += child.size().height + _gap;
	}
    }
}
