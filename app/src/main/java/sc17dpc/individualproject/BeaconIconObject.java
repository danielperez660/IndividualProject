package sc17dpc.individualproject;

import android.widget.ImageView;

public class BeaconIconObject {
    private ImageView icon;
    private BeaconEntry beacon;

    private int[] coords = new int[2];

    public void setBeacon(BeaconEntry b){
        beacon = b;
    }

    public void setImage(ImageView i){
        icon = i;
    }

    public BeaconEntry getBeacon(){
        return beacon;
    }

    public ImageView getIcon(){
        return icon;
    }

    public void setCoords(int x, int y) {
        coords[0] = x;
        coords[1] = y;
    }

    public int[] getCoords(){
        return coords;
    }
}
