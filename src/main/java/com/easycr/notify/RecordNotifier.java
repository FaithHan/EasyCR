package com.easycr.notify;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class RecordNotifier {

    public static void notifyInfo(@Nullable Project project, String content) {
        NotificationGroupManager.getInstance().getNotificationGroup("com.easycr")
                .createNotification(content, NotificationType.INFORMATION)
                .setTitle("EasyCR add a new Item Success")
                .notify(project);
    }

}
