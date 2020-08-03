package com.himanshu.buckcounter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.himanshu.buckcounter.beans.Account;

import java.util.List;

import static com.himanshu.buckcounter.business.Constants.DECIMAL_FORMAT;

public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ViewHolder> {
    private final List<Account> mValues;

    public AccountRecyclerViewAdapter(List<Account> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = mValues.get(position);
        holder.mItem = account;
        holder.mAccountName.setText(account.getName());
        holder.mAccountBalance.setText(DECIMAL_FORMAT.format(account.getBalance()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAccountName;
        public final TextView mAccountBalance;
        public Account mItem;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mAccountName = view.findViewById(R.id.account_name);
            mAccountBalance = view.findViewById(R.id.account_balance);
        }
    }
}
