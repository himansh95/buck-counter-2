package com.himanshu.buckcounter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.Util;

import java.util.List;

import static com.himanshu.buckcounter.business.Constants.DATE_FORMAT;
import static com.himanshu.buckcounter.business.Constants.DECIMAL_FORMAT;

public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder> {
    private final List<Transaction> mValues;

    public TransactionRecyclerViewAdapter(List<Transaction> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = mValues.get(position);
        holder.mItem = transaction;
        holder.mTransactionAmount.setText(DECIMAL_FORMAT.format(transaction.getAmount()));
        holder.mTransactionParticulars.setText(new StringBuffer("\"").append(transaction.getParticulars()).append("\""));
        holder.mTransactionDate.setText(DATE_FORMAT.format(transaction.getTimestamp()));
        if (transaction.getTransactionType() == Transaction.TransactionType.CONTRA) {
            holder.mTransactionType.setImageResource(R.mipmap.transaction_contra);
            holder.mTransactionAccount.setText(new StringBuffer(transaction.getCreditAccount()).append(Util.fromHtml("&nbsp;&#10132;&nbsp;")).append(transaction.getDebitAccount()));
        } else if (transaction.getTransactionType() == Transaction.TransactionType.DR) {
            holder.mTransactionType.setImageResource(R.mipmap.transaction_debit);
            holder.mTransactionAccount.setText(transaction.getDebitAccount());
        } else if (transaction.getTransactionType() == Transaction.TransactionType.CR) {
            holder.mTransactionType.setImageResource(R.mipmap.transaction_credit);
            holder.mTransactionAccount.setText(transaction.getCreditAccount());
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTransactionAmount;
        public final TextView mTransactionParticulars;
        public final TextView mTransactionAccount;
        public final TextView mTransactionDate;
        public final ImageView mTransactionType;
        public final ImageView mTransactionContextMenu;
        public Transaction mItem;

        public ViewHolder (View view) {
            super(view);
            mView = view;
            mTransactionAmount = view.findViewById(R.id.transaction_amount);
            mTransactionParticulars = view.findViewById(R.id.transaction_particulars);
            mTransactionAccount =  view.findViewById(R.id.transaction_account);
            mTransactionDate = view.findViewById(R.id.transaction_date);
            mTransactionType = view.findViewById(R.id.transaction_type);
            mTransactionContextMenu = view.findViewById(R.id.transaction_context_menu);
        }
    }
}
