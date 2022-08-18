package cosine.boat;

import android.content.Context;
import android.os.Handler;
import android.view.TextureView;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Vector;

import cosine.boat.function.BoatCallback;
import cosine.boat.function.BoatLaunchCallback;

public class BoatActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

	private TextureView mainTextureView;
	public BoatCallback boatCallback;
	public float scaleFactor = 1.0F;

	int output = 0;

	public void init(){
		nOnCreate();

		mainTextureView = findViewById(R.id.main_surface);
		mainTextureView.setSurfaceTextureListener(this);
	}
	
	public static native void setBoatNativeWindow(Surface surface);

	public native void nOnCreate();
	
	static {
		System.loadLibrary("boat");
	}
	
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		System.out.println("SurfaceTexture is available!");
		boatCallback.onSurfaceTextureAvailable(surface,width,height);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		boatCallback.onSurfaceTextureSizeChanged(surface,width,height);
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		if (output == 1) {
			boatCallback.onPicOutput();
			output++;
		}
		if (output < 1) {
			output++;
		}
	}

	public void startGame(final String javaPath,final String home,final boolean highVersion,final Vector<String> args,String renderer,String gameDir){
		Handler handler = new Handler();
		new Thread(() -> LoadMe.launchMinecraft(handler, BoatActivity.this, javaPath, home, highVersion, args, renderer, gameDir, new BoatLaunchCallback() {
			@Override
			public void onStart() {
				boatCallback.onStart();
			}

			@Override
			public void onError(Exception e) {
				boatCallback.onError(e);
			}
		})).start();
	}

	public void setCursorMode(int mode) {
		boatCallback.onCursorModeChange(mode);
	}

	public static void onExit(Context ctx, int code) {
		((BoatActivity) ctx).boatCallback.onExit(code);
	}

	public void setBoatCallback(BoatCallback callback) {
		this.boatCallback = callback;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
			mainTextureView.post(() -> {
				boatCallback.onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(),mainTextureView.getWidth(),mainTextureView.getHeight());
			});
		}
	}
}



