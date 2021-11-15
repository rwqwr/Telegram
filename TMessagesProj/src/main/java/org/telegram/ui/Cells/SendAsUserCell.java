package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RoundCloseDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;

public class SendAsUserCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private ImageView imageView;

    private AvatarDrawable avatarDrawable;
    private TLObject currentObject;

    private int currentId;
    private int currentDrawable;

    private int lastStatus;
    private TLRPC.FileLocation lastAvatar;

    private int currentMode;

    public SendAsUserCell(Context context, int padding) {
        super(context);

        avatarDrawable = new AvatarDrawable();

        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(24));
        addView(avatarImageView, LayoutHelper.createFrame(32, 32, Gravity.CENTER));

        imageView = new ImageView(context);
        imageView.setBackground(new RoundCloseDrawable(AndroidUtilities.dp(16), Theme.getColor(Theme.key_avatar_backgroundBlue)));
        imageView.setVisibility(GONE);
        imageView.setImageResource(R.drawable.input_clear);

        addView(imageView,  LayoutHelper.createFrame(32, 32, Gravity.CENTER));
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId) {
        if (object == null && name == null && status == null) {
            currentObject = null;
            avatarImageView.setImageDrawable(null);
            return;
        }
        currentObject = object;
        currentDrawable = resId;
        currentMode = 0;
        update(0);
    }

    public void setMode(int mode) {
        currentMode = mode;
        avatarImageView.setVisibility(mode == 0 ? View.VISIBLE : View.GONE);
        imageView.setVisibility(mode == 1 ? View.VISIBLE : View.GONE);
    }

    public int getMode() {
        return currentMode;
    }

    public void setCurrentId(int id) {
        currentId = id;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(70), MeasureSpec.EXACTLY));
    }

    public void update(int mask) {
        TLRPC.FileLocation photo = null;
        String newName = null;
        TLRPC.User currentUser = null;
        TLRPC.Chat currentChat = null;
        if (currentObject instanceof TLRPC.User) {
            currentUser = (TLRPC.User) currentObject;
            if (currentUser.photo != null) {
                photo = currentUser.photo.photo_small;
            }
        } else if (currentObject instanceof TLRPC.Chat) {
            currentChat = (TLRPC.Chat) currentObject;
            if (currentChat.photo != null) {
                photo = currentChat.photo.photo_small;
            }
        }

        if (mask != 0) {
            boolean continueUpdate = false;
            if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0) {
                if (lastAvatar != null && photo == null || lastAvatar == null && photo != null || lastAvatar != null && photo != null && (lastAvatar.volume_id != photo.volume_id || lastAvatar.local_id != photo.local_id)) {
                    continueUpdate = true;
                }
            }
            if (currentUser != null && !continueUpdate && (mask & MessagesController.UPDATE_MASK_STATUS) != 0) {
                int newStatus = 0;
                if (currentUser.status != null) {
                    newStatus = currentUser.status.expires;
                }
                if (newStatus != lastStatus) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate) {
                return;
            }
        }
        lastAvatar = photo;

        if (currentUser != null) {
            avatarDrawable.setInfo(currentUser);
            if (currentUser.status != null) {
                lastStatus = currentUser.status.expires;
            } else {
                lastStatus = 0;
            }
        } else if (currentChat != null) {
            avatarDrawable.setInfo(currentChat);
        } else {
            avatarDrawable.setInfo(currentId, "#", null);
        }

        if (currentUser != null) {
            avatarImageView.setForUserOrChat(currentUser, avatarDrawable);
        } else if (currentChat != null) {
            avatarImageView.setForUserOrChat(currentChat, avatarDrawable);
        } else {
            avatarImageView.setImageDrawable(avatarDrawable);
        }

//        if (imageView.getVisibility() == VISIBLE && currentDrawable == 0 || imageView.getVisibility() == GONE && currentDrawable != 0) {
//            imageView.setVisibility(currentDrawable == 0 ? GONE : VISIBLE);
//            imageView.setImageResource(currentDrawable);
//        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
