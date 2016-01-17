/**
 * Created by Matt on 1/16/2016.
 */
public class LumBitmap {
    int[][] bitmap;

    public LumBitmap(int width, int height) {
        bitmap = new int[height][width];
    }
    public void setPixel(int x, int y, int lum) {
        bitmap[y][x] = lum;
    }
}
