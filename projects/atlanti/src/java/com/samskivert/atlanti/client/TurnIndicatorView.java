//
// $Id: TurnIndicatorView.java,v 1.3 2002/01/29 23:47:03 mdb Exp $

package com.threerings.venison;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.samskivert.swing.VGroupLayout;

import com.threerings.presents.dobj.AttributeChangeListener;
import com.threerings.presents.dobj.AttributeChangedEvent;

import com.threerings.crowd.data.PlaceObject;
import com.threerings.crowd.client.PlaceView;

/**
 * Displays who the current turn holder is as well as the tile they are
 * currently placing.
 */
public class TurnIndicatorView
    extends JPanel implements PlaceView, AttributeChangeListener
{
    /**
     * Creates a new turn indicator view which in turn creates its
     * subcomponents.
     */
    public TurnIndicatorView ()
    {
	setLayout(new VGroupLayout());

        // add our turn holder display
        _whoLabel = new JLabel();
        add(_whoLabel);

        // and add our tile display
        _tileLabel = new TileLabel();
        add(_tileLabel);

        // and add a tile's remaining label
        _countLabel = new JLabel();
        add(_countLabel);
    }

    // documentation inherited
    public void willEnterPlace (PlaceObject plobj)
    {
        // we want to grab a reference to the game object and add
        // ourselves as an attribute change listener
        _venobj = (VenisonObject)plobj;
        _venobj.addListener(this);

        // update our displays
        updateCurrentTile();
        updateGameState();
        updateTurnHolder();
        updateRemainingTiles();
    }

    // documentation inherited
    public void didLeavePlace (PlaceObject plobj)
    {
        // remove our listening self
        _venobj.removeListener(this);
        _venobj = null;
    }

    // documentation inherited
    public void attributeChanged (AttributeChangedEvent event)
    {
        if (event.getName().equals(VenisonObject.CURRENT_TILE)) {
            // update the current tile display
            updateCurrentTile();

        } else if (event.getName().equals(VenisonObject.STATE)) {
            // update the game state display
            updateGameState();
            // update the tiles remaining
            updateRemainingTiles();

        } else if (event.getName().equals(VenisonObject.TURN_HOLDER)) {
            // update the turn holder
            updateTurnHolder();

            // update the tiles remaining when the turn changes
            updateRemainingTiles();
        }
    }

    protected void updateGameState ()
    {
        switch (_venobj.state) {
        case VenisonObject.AWAITING_PLAYERS:
            _whoLabel.setText("Awaiting players...");
            _tileLabel.setTile(null);
            break;
        case VenisonObject.GAME_OVER:
            _whoLabel.setText("Game over.");
            _tileLabel.setTile(null);
            break;
        case VenisonObject.CANCELLED:
            _whoLabel.setText("Cancelled.");
            _tileLabel.setTile(null);
            break;
        }
    }

    protected void updateTurnHolder ()
    {
        if (_venobj.state == VenisonObject.IN_PLAY) {
            _whoLabel.setText(_venobj.turnHolder);
        }
    }

    protected void updateCurrentTile ()
    {
        // display the tile to be placed
        _tileLabel.setTile(_venobj.currentTile);
    }

    protected void updateRemainingTiles ()
    {
        _countLabel.setText("Tiles remaining: " + (71-_venobj.tiles.size()));
    }

    /** The label displaying whose turn it is. */
    protected JLabel _whoLabel;

    /** The label displaying the tile to be placed. */
    protected TileLabel _tileLabel;

    /** The label displaying the number of tiles remaining. */
    protected JLabel _countLabel;

    /** A reference to the game object. */
    protected VenisonObject _venobj;
}
