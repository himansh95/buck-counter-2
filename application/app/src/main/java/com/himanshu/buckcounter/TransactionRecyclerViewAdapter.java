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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.DatabaseHelper;
import com.himanshu.buckcounter.business.Util;

import java.util.List;

import static com.himanshu.buckcounter.business.Constants.DATE_FORMAT;
import static com.himanshu.buckcounter.business.Constants.DECIMAL_FORMAT;
import static com.himanshu.buckcounter.business.Constants.VALID_AMOUNT_REGEX;
import static com.himanshu.buckcounter.business.Constants.VALID_TEXT_REGEX;

public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder> {
    private final List<Transaction> mValues;
    private Context context;

    public TransactionRecyclerViewAdapter(List<Transaction> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Transaction transaction = mValues.get(position);
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
        holder.mTransactionContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.mTransactionContextMenu, transaction, position);
            }
        });
    }

    private void showPopupMenu(View view, final Transaction transaction, final int position) {
        final PopupMenu menu = new PopupMenu(context, view);
        final MenuInflater menuInflater = menu.getMenuInflater();
        menuInflater.inflate(R.menu.transaction_context_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.edit_particulars:
                        final AlertDialog editParticulars = new AlertDialog.Builder(context)
                                .setIcon(R.mipmap.ic_launcher_round)
                                .setTitle(R.string.edit_transaction_particulars)
                                .setView(R.layout.edit_transaction_particulars)
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(android.R.string.ok, null)
                                .create();
                        editParticulars.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                final EditText transactionParticulars = editParticulars.findViewById(R.id.edit_transaction_particulars);
                                transactionParticulars.setText(transaction.getParticulars());
                                transactionParticulars.selectAll();
                                editParticulars.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (transactionParticulars.getText() == null || transactionParticulars.getText().toString().isEmpty() || !transactionParticulars.getText().toString().matches(VALID_TEXT_REGEX)) {
                                            ((TextInputLayout)editParticulars.findViewById(R.id.edit_transaction_particulars_container)).setError(context.getText(R.string.add_transaction_particulars_error));
                                            return;
                                        } else {
                                            ((TextInputLayout)editParticulars.findViewById(R.id.edit_transaction_particulars_container)).setErrorEnabled(false);
                                        }
                                        String newParticulars = transactionParticulars.getText().toString().trim().toLowerCase();
                                        if (newParticulars.equals(transaction.getParticulars())) {
                                            return;
                                        }
                                        boolean editParticularsSuccessful = DatabaseHelper.getInstance(context).editTransactionParticulars(transaction, newParticulars);
                                        if (editParticularsSuccessful) {
                                            mValues.get(position).setParticulars(newParticulars);
                                            TransactionRecyclerViewAdapter.this.notifyItemChanged(position);
                                        }
                                        editParticulars.dismiss();
                                    }
                                });
                            }
                        });
                        editParticulars.show();
                        return true;
                    case R.id.edit_amount:
                        final AlertDialog editAmount = new AlertDialog.Builder(context)
                                .setIcon(R.mipmap.ic_launcher_round)
                                .setTitle(R.string.edit_transaction_amount)
                                .setView(R.layout.edit_transaction_amount)
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(android.R.string.ok, null)
                                .create();
                        editAmount.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                final EditText transactionAmount = editAmount.findViewById(R.id.edit_transaction_amount);
                                transactionAmount.setText(String.valueOf(transaction.getAmount()));
                                transactionAmount.selectAll();
                                editAmount.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (transactionAmount.getText() == null || transactionAmount.getText().toString().isEmpty() || !transactionAmount.getText().toString().matches(VALID_AMOUNT_REGEX)) {
                                            ((TextInputLayout)editAmount.findViewById(R.id.edit_transaction_amount_container)).setError(context.getText(R.string.add_transaction_amount_error));
                                            return;
                                        } else {
                                            ((TextInputLayout)editAmount.findViewById(R.id.edit_transaction_amount_container)).setErrorEnabled(false);
                                        }
                                        double newAmount = Double.valueOf(transactionAmount.getText().toString().trim().toLowerCase());
                                        if (newAmount == transaction.getAmount()) {
                                            return;
                                        }
                                        boolean editParticularsSuccessful = DatabaseHelper.getInstance(context).editTransactionAmount(transaction, newAmount);
                                        if (editParticularsSuccessful) {
                                            mValues.get(position).setAmount(newAmount);
                                            TransactionRecyclerViewAdapter.this.notifyItemChanged(position);
                                        }
                                        editAmount.dismiss();
                                    }
                                });
                            }
                        });
                        editAmount.show();
                        return true;
                    case R.id.delete_transaction:
                        AlertDialog confirmDeleteTransaction = new AlertDialog.Builder(context)
                                .setIcon(R.mipmap.ic_launcher_round)
                                .setTitle(R.string.delete_transaction)
                                .setMessage(R.string.delete_transaction_confirm)
                                .setNegativeButton(android.R.string.no, null)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        boolean transactionDeletedSuccessfully = DatabaseHelper.getInstance(context).deleteTransaction(transaction);
                                        if(transactionDeletedSuccessfully) {
                                            mValues.remove(position);
                                            TransactionRecyclerViewAdapter.this.notifyItemRemoved(position);
                                            TransactionRecyclerViewAdapter.this.notifyItemRangeChanged(position, getItemCount() - position);
                                        }
                                    }
                                })
                                .create();
                        confirmDeleteTransaction.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        menu.show();
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
