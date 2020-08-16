package com.himanshu.buckcounter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.business.DatabaseHelper;

import java.util.List;

import static com.himanshu.buckcounter.business.Constants.DECIMAL_FORMAT;
import static com.himanshu.buckcounter.business.Constants.VALID_TEXT_REGEX;

public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ViewHolder> {
    private final List<Account> mValues;
    private Context context;

    public AccountRecyclerViewAdapter(List<Account> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Account account = mValues.get(position);
        holder.mItem = account;
        holder.mAccountName.setText(account.getName());
        holder.mAccountBalance.setText(DECIMAL_FORMAT.format(account.getBalance()));
        holder.mAccountContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.mAccountContextMenu, account, position);
            }
        });
    }

    private void showPopupMenu(View view, final Account account, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.account_context_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.edit_account_name:
                        final AlertDialog editName = new AlertDialog.Builder(context)
                                .setIcon(R.mipmap.ic_launcher_round)
                                .setTitle(R.string.edit_account_name)
                                .setView(R.layout.edit_account_name)
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(android.R.string.ok, null)
                                .create();
                        editName.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                final EditText accountName = editName.findViewById(R.id.edit_account_name);
                                accountName.setText(account.getName());
                                accountName.selectAll();
                                editName.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (accountName.getText() == null || accountName.getText().toString().isEmpty() || !accountName.getText().toString().trim().toLowerCase().matches(VALID_TEXT_REGEX)) {
                                            ((TextInputLayout)editName.findViewById(R.id.edit_account_name_container)).setError(context.getText(R.string.add_account_name_error));
                                            return;
                                        } else {
                                            ((TextInputLayout)editName.findViewById(R.id.edit_account_name_container)).setErrorEnabled(false);
                                        }
                                        String newName = accountName.getText().toString().trim().toLowerCase();
                                        if (newName.equals(account.getName())) {
                                            editName.dismiss();
                                            return;
                                        }
                                        boolean editNameSuccessful = DatabaseHelper.getInstance(context).editAccountName(account, newName);
                                        Toast.makeText(context, "editNameSuccessful: " + editNameSuccessful, Toast.LENGTH_LONG).show();
                                        if (editNameSuccessful) {
                                            mValues.get(position).setName(newName);
                                            AccountRecyclerViewAdapter.this.notifyItemChanged(position);
                                        }
                                        editName.dismiss();
                                    }
                                });
                            }
                        });
                        editName.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAccountName;
        public final TextView mAccountBalance;
        public final ImageView mAccountContextMenu;
        public Account mItem;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mAccountName = view.findViewById(R.id.account_name);
            mAccountBalance = view.findViewById(R.id.account_balance);
            mAccountContextMenu = view.findViewById(R.id.account_context_menu);
        }
    }
}