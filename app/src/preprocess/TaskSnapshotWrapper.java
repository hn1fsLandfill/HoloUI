package eu.hn1f.holoui;

import android.os.RemoteException;
import android.window.TaskSnapshot;
#ifdef CINNAMON_BUN
import android.window.TaskSnapshotManager;
#else
import android.app.ActivityTaskManager;
#endif

public class TaskSnapshotWrapper {
    public static TaskSnapshot getTaskSnapshot(int taskID) {
        try {
    #ifdef CINNAMON_BUN
        return TaskSnapshotManager.getInstance().getTaskSnapshot(
            taskID, 0
        );
    #else
        return ActivityTaskManager.getService().getTaskSnapshot(
            taskID, true
        );
    #endif
        } catch (RemoteException e) {
            return null;
        }
    }
}