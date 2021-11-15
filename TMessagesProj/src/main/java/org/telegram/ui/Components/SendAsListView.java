package org.telegram.ui.Components;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Cells.UserCell2;

import java.util.ArrayList;
import java.util.List;

public class SendAsListView {

    public static class SendAsAdapter extends RecyclerListView.SelectionAdapter {

        ArrayList<TLObject> peers = null;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GroupCreateUserCell userCell = new GroupCreateUserCell(parent.getContext(), 2, 0, false);
            return new RecyclerListView.Holder(userCell);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GroupCreateUserCell userCell = (GroupCreateUserCell) holder.itemView;
            TLObject peer = peers.get(position);

            userCell.setObject(peer, null, null, true);
        }

        @Override
        public int getItemCount() {
            return peers.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public void setPeers(ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats) {
            if (peers == null) {
                peers = new ArrayList<>(users.size() + chats.size());
            }
            peers.addAll(users);
            peers.addAll(chats);
        }
    }
}
