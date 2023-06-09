/*
 * MIT License
 *
 * Copyright (c) 2021 Evren Coşkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.smd.cv.howl;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.pagination.Pagination;
import com.smd.cv.howl.api.MeasurementService;
import com.smd.cv.howl.settings.configuration.Preferences;
import com.smd.cv.howl.settings.connectivity.NetworkChangeBroadcastReceiver;
import com.smd.cv.howl.settings.connectivity.NetworkConnectCallback;
import com.smd.cv.howl.tableview.TableViewAdapter;
import com.smd.cv.howl.tableview.TableViewDataFetcher;
import com.smd.cv.howl.tableview.TableViewListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeasurementsFragment extends Fragment {
    private TableView mTableView;
    private TableViewDataFetcher mTableViewDataFetcher;
    NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private boolean mTableViewSuccessfullyInitialized;

    public MeasurementsFragment() {
        super(R.layout.fragment_measurements);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Let's get TableView
        mTableView = view.findViewById(R.id.tableview);

        mTableViewSuccessfullyInitialized = false;

        mTableViewDataFetcher = new TableViewDataFetcher(
                MeasurementService.newInstance(Preferences.getApiServer(view.getContext())),
                Preferences.getSensorGuid(view.getContext())
        );

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle(R.string.table_load_measurements);
        mProgressDialog.setMessage(getString(R.string.table_fetching_in_progress));
        mProgressDialog.setCancelable(false);

        mNetworkChangeBroadcastReceiver = NetworkChangeBroadcastReceiver.registerNewInstance(getContext(), onNetConnect);
        initializeTableView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mNetworkChangeBroadcastReceiver.unregister();

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void initializeTableView() {
        mProgressDialog.show();

        mTableViewDataFetcher.fetchMeasurements(tableViewModel -> {
            if (tableViewModel == null) {
                return;
            }

            mTableViewSuccessfullyInitialized = true;

            // Create TableView Adapter
            TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);

            mTableView.setAdapter(tableViewAdapter);
            mTableView.setTableViewListener(new TableViewListener(mTableView));

            // Load the data to the TableView
            tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel
                    .getRowHeaderList(), tableViewModel.getCellList());

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        });
    }

    private final NetworkConnectCallback onNetConnect = new NetworkConnectCallback() {
        @Override
        public void onNetworkConnected() {
            if (!mTableViewSuccessfullyInitialized) {
                initializeTableView();
            }
        }
    };
}