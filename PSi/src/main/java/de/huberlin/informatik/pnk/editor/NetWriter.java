package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.util.*;

import de.huberlin.informatik.pnk.kernel.*;

/**
 * NetWriter.java
 *
 *
 * Created: Tue Jan  2 20:58:54 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class NetWriter  {
    Editor editor;

    protected NetWriter(Editor editor) {
        this.editor = editor;
    }

    protected void write(Graph graph) {
        if (graph instanceof Net) {
            this.write((Net)graph);
            return;
        }
    }

    protected void write(Net net) {
        // get an enumeration of all netobjects in this editor
        ReferenceTable rtable = this.editor.getReferencetable();
        Enumeration enumeration = rtable.keys();
        while (enumeration.hasMoreElements()) {
            // get next netobject for saving
            Object netobject = enumeration.nextElement();
            //###Editor.msg(" writing " + netobject + " ...");
            // cast it to an extendable, to use Extendable.setPosition()
            Extendable extendable = (Extendable)netobject;
            // now get all sprites of netobject
            ReferenceTable.RTInfo info;
            info = (ReferenceTable.RTInfo)rtable.get(netobject);
            Vector sprites = info.spritelist;

            // at the moment save only first sprite in list

            // extract ALL sprite-page tupelS
            ReferenceTable.SpritePageTupel sptupel;
            for (Enumeration e = sprites.elements(); e.hasMoreElements(); ) {
                sptupel = (ReferenceTable.SpritePageTupel)e.nextElement();
                // extract sprite from spritePageTupel
                Sprite sprite = (Sprite)sptupel.sprite;
                // extract position of this sprite
                Point pos = sprite.getPosition();
                int pageId = sptupel.page.id;
                // set Position in kernel.Net using methods of Extendable
                extendable.setPosition(pos, pageId);

                // now I must save position of extensions of this netobject
                Vector subsprites = sprite.subsprites;
                // sprite of extensions are all in subsprites of netobject-sprite
                for (int i = 0; i < subsprites.size(); i++) {
                    Sprite s = (Sprite)subsprites.get(i);
                    // check if this subsprite is an extension-sprite
                    if (s instanceof Extension) {
                        // cast to an extensionSprite
                        Extension extSprite = (Extension)s;
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
