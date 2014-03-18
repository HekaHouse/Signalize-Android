package root.gast.image;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import java.util.List;

public class ManageCameraFasterActivity extends ManageCameraActivity {

    /**
     * Switching from one camera to another. Must stop the preview, free the
     * previous camera, and open the new camera, in that order.
     */
    protected void advanceCamera() {
        mCamera.autoFocus(null);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mDefaultCameraId = (mDefaultCameraId + 1) % mNumberOfCameras;
        mCamera = Camera.open(mDefaultCameraId);
        switchCamera();
    }

    /**
     * In order to make image processing as fast as possible we use
     * the smallest preview image available.
     */
    private void switchCamera() {
        setCameraDisplayOrientation();
        Camera.Parameters cameraParameters = mCamera.getParameters();
        List<Size> sizes = cameraParameters.getSupportedPreviewSizes();
        int width = Integer.MAX_VALUE, height = Integer.MAX_VALUE;
        // choose the smallest preview available
        for (int i = 0; i < sizes.size(); i++) {
            Size s = sizes.get(i);
            if (s.width < width) {
                width = s.width;
                height = s.height;
            }
        }
        cameraParameters.setPreviewSize(width, height);
    }
}
