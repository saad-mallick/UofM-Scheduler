package saadandaakash.uofmscheduler;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Aakash on 12/23/2017.
 */

public class customTextView extends AppCompatTextView {

    public customTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),
                "fonts/Quicksand-Regular.otf"));
    }
}
