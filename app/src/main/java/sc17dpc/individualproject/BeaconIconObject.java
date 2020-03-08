package sc17dpc.individualproject;

import android.widget.ImageView;

public class BeaconIconObject {
    private ImageView icon;
    private BeaconEntry beacon;

    private float[] coords = new float[2];

    public void setBeacon(BeaconEntry b) {
        this.beacon = b;
    }

    public void setIcon(ImageView i) {
        this.icon = i;
    }

    public BeaconEntry getBeacon() {
        return beacon;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setCoords(float x, float y) {
        this.coords[0] = x;
        this.coords[1] = y;
    }

    public float[] getCoords() {
        return coords;
    }
}
