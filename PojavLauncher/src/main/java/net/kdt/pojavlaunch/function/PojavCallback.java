package net.kdt.pojavlaunch.function;

import android.graphics.SurfaceTexture;

public interface PojavCallback {
	void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);
	void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);
	void onCursorModeChange(int mode);
	void onStart();
	void onPicOutput();
	void onError(Exception e);
	void onExit(int code);
}