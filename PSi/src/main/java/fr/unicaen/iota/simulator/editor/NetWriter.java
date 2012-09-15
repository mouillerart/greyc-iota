/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.editor;

import de.huberlin.informatik.pnk.kernel.Extendable;
import de.huberlin.informatik.pnk.kernel.Graph;
import de.huberlin.informatik.pnk.kernel.Net;
import java.awt.Point;
import java.util.Map;

/**
 * NetWriter
 *
 * Created after de.huberlin.informatik.pnk.edito.NetWriter
 */
class NetWriter {

    private Editor editor;

    protected NetWriter(Editor editor) {
        this.editor = editor;
    }

    protected void write(Graph graph) {
        if (graph instanceof Net) {
            this.write((Net) graph);
        }
    }

    protected void write(Net net) {
        // get an enumeration of all netobjects in this editor
        for (Map.Entry<Object, ReferenceTable.RTInfo> pair : this.editor.getReferencetable().entrySet()) {
            // get next netobject for saving
            Object netobject = pair.getKey();
            //###Editor.msg(" writing " + netobject + " ...");
            // cast it to an extendable, to use Extendable.setPosition()
            Extendable extendable = (Extendable) netobject;
            // now get all sprites of netobject
            ReferenceTable.RTInfo info = pair.getValue();

            // at the moment save only first sprite in list

            // extract ALL sprite-page tupelS
            for (ReferenceTable.SpritePageTuple sptupel : info.spritelist) {
                // extract sprite from spritePageTupel
                Sprite sprite = sptupel.sprite;
                // extract position of this sprite
                Point pos = sprite.getPosition();
                int pageId = sptupel.page.getId();
                // set Position in kernel.Net using methods of Extendable
                extendable.setPosition(pos, pageId);

                // now I must save position of extensions of this netobject
                for (Sprite s : sprite.subsprites) {
                    // sprite of extensions are all in subsprites of netobject-sprite
                    // check if this subsprite is an extension-sprite
                    if (s instanceof Extension) {
                        // cast to an extensionSprite
                        Extension extSprite = (Extension) s;
                        // get position of extensionSprite
                        Point extPos = extSprite.getPosition();
                        // now extract the Net.Extension of netobject
                        String id = extSprite.getId();
                        de.huberlin.informatik.pnk.kernel.Extension extension;
                        extension = extendable.getExtension(id);
                        extension.setOffset(new Point(extPos.x - pos.x, extPos.y - pos.y), pageId);
                        //###Editor.msg(" ### Extension Position [" + pos.x + "," + pos.y + "]");
                    }
                }
            }
        }
    }
} // NetWriter
