package com.himanshu.buckcounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.business.DatabaseHelper;
import com.himanshu.buckcounter.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ArchivedAccountsActivity extends AppCompatActivity {
    List<Account> accountList;
    AccountRecyclerViewAdapter mAccountRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_accounts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EmptyRecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setEmptyView(findViewById(R.id.empty_list_card));
        accountList = new ArrayList<>();
        accountList.addAll(DatabaseHelper.getInstance(this).getArchivedAccounts());
        mAccountRecyclerViewAdapter = new AccountRecyclerViewAdapter(accountList, this, true);
        recyclerView.setAdapter(mAccountRecyclerViewAdapter);
    }
}