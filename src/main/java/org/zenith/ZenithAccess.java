package org.zenith;

import org.zenith.base.rotation.BcCameraTestnerv;
import org.zenith.utility.api.JointCodebook;
import org.zenith.utility.misc.Handler2;

public interface ZenithAccess extends Handler2 {
   Zenith zenith = Zenith.getInstance();
   JointCodebook jointCodebook = Zenith.getInstance().getJointCodebook();
   RotationManager rotationManager = Zenith.getInstance().getRotationManager();
   BcCameraTestnerv bcCameraTestnerv = rotationManager.getBcCameraTestnerv();
}
