package cosine.boat.function;

import android.graphics.SurfaceTexture;

public interface BoatCallback{
	void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);
	void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);
	void onCursorModeChange(int mode);
	void onStart();
	void onPicOutput();
	void onError(Exception e);
	void onExit(int code);
}