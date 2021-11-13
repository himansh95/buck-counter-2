package com.himanshu.buckcounter;

import static com.himanshu.buckcounter.business.Constants.DECIMAL_FORMAT;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.beans.Transaction;
import com.himanshu.buckcounter.business.Constants;
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
    String accountName;

    public TransactionsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transactions, container, false);

        accountName = getActivity().getIntent() != null && getActivity().getIntent().getExtras() != null ?
                getActivity().getIntent().getExtras().getString(Constants.BUNDLE_ACCOUNTS_NAME, null) :
                null;
        context = view.getContext();
        if (accountName != null) {
            Account account = DatabaseHelper.getInstance(context).getAccount(accountName);
            view.findViewById(R.id.account_balance_card).setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.account_balance_card).findViewById(R.id.account_balance)).setText(DECIMAL_FORMAT.format(account.getBalance()));
        }

        // set the adapter
        transactionList = new ArrayList<>();
        transactionList.addAll(DatabaseHelper.getInstance(context).getAllTransactions(accountName));

        EmptyRecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setEmptyView(view.findViewById(R.id.empty_list_card));
        mTransactionRecyclerViewAdapter = new TransactionRecyclerViewAdapter(transactionList, context, accountName);
        recyclerView.setAdapter(mTransactionRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        transactionList.clear();
        transactionList.addAll(DatabaseHelper.getInstance(context).getAllTransactions(accountName));
        mTransactionRecyclerViewAdapter.notifyDataSetChanged();
    }
}
