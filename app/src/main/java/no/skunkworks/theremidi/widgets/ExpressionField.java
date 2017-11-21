package no.skunkworks.theremidi.widgets;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Target for a reusable expression field.
 * Holds a
 * - name
 * - default value (editable with dropdown or others?)
 * - Register swipes over the surface for realtime value change
 * - Double-click or something fires a dialog to connect it to a sensor
 */

public class ExpressionField extends View {
    private String name;
    private TextView textView;
    //private DropDow

    public ExpressionField(Context ctx) {
        super(ctx);
    }

}
