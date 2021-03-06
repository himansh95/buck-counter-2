package com.himanshu.buckcounter;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.himanshu.buckcounter.beans.Account;
import com.himanshu.buckcounter.business.DatabaseHelper;
import com.himanshu.buckcounter.view.EmptyRecyclerView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AccountsActivity extends AppCompatActivity {
    List<Account> accountList;
    AccountRecyclerViewAdapter mAccountRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountsActivity.this, AddAccount.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EmptyRecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setEmptyView(findViewById(R.id.empty_list_card));
        accountList = new ArrayList<>();
        accountList.addAll(DatabaseHelper.getInstance(this).getAllAccounts());
        if (accountList.size() > 0) {
            accountList.add(0, new Account("\"Total Balance\"", DatabaseHelper.getInstance(this).getTotalAccountBalance(false)));
        }
        mAccountRecyclerViewAdapter = new AccountRecyclerViewAdapter(accountList, this);
        recyclerView.setAdapter(mAccountRecyclerViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        accountList.clear();
        accountList.addAll(DatabaseHelper.getInstance(this).getAllAccounts());
        if (accountList.size() > 0) {
            accountList.add(0, new Account("\"Total Balance\"", DatabaseHelper.getInstance(this).getTotalAccountBalance(false)));
        }
        mAccountRecyclerViewAdapter.notifyDataSetChanged();
    }
}
