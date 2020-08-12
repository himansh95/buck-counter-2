package com.himanshu.buckcounter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.DatabaseHelper;
import com.himanshu.buckcounter.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TransactionsActivityFragment extends Fragment {
    TransactionRecyclerViewAdapter mTransactionRecyclerViewAdapter;
    List<Transaction> transactionList;
    Context context;
    View view;

    public TransactionsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // set the adapter
        context = view.getContext();
        transactionList = new ArrayList<>();
        transactionList.addAll(DatabaseHelper.getInstance(context).getAllTransactions());

        EmptyRecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setEmptyView(view.findViewById(R.id.empty_list_card));
        mTransactionRecyclerViewAdapter = new TransactionRecyclerViewAdapter(transactionList, context);
        recyclerView.setAdapter(mTransactionRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        transactionList.clear();
        transactionList.addAll(DatabaseHelper.getInstance(context).getAllTransactions());
        mTransactionRecyclerViewAdapter.notifyDataSetChanged();
    }
}
