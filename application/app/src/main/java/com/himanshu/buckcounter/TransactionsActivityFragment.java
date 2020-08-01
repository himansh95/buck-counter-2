package com.himanshu.buckcounter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.DatabaseHelper;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TransactionsActivityFragment extends Fragment {

    public TransactionsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        List<Transaction> transactionList = DatabaseHelper.getInstance(context).getAllTransactions();
        TransactionRecyclerViewAdapter mTransactionRecyclerViewAdapter = new TransactionRecyclerViewAdapter(transactionList);
        recyclerView.setAdapter(mTransactionRecyclerViewAdapter);

        return view;
    }
}
