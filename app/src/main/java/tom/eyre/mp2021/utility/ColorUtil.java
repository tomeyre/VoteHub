package tom.eyre.mp2021.utility;

import android.content.Context;

import tom.eyre.mp2021.entity.MpEntity;

public class ColorUtil {

    public int getColor(Context context, MpEntity mp){
        return  context.getResources().getColor(context.getResources().getIdentifier(mp.getParty().toLowerCase().trim()
                .replace("&", "and")
                .replace(" +", " ")
                .replace(" ", "_")
                .replace("-", "_")
                .replace(",", "")
                .replace("'", "")
                .replace(":", "")
                .replace(")", "")
                .replace("-", "")
                .replace("(", ""), "color", context.getPackageName()), null);
    }
}
