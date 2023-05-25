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
    private ImageButton previousButton, nextButton;
    private TextView tablePaginationDetails;
    private TableView mTableView;
    private TableViewDataFetcher mTableViewDataFetcher;
    NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private boolean mTableViewSuccessfullyInitialized;

    @Nullable
    private Pagination mPagination; // This is used for paginating the table.

    public MeasurementsFragment() {
        super(R.layout.fragment_measurements);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        previousButton = view.findViewById(R.id.previous_button);
        nextButton = view.findViewById(R.id.next_button);
        tablePaginationDetails = view.findViewById(R.id.table_details);

        Spinner itemsPerPage = view.findViewById(R.id.items_per_page_spinner);
        itemsPerPage.setOnItemSelectedListener(onItemsPerPageSelectedListener);

        previousButton.setOnClickListener(mClickListener);
        nextButton.setOnClickListener(mClickListener);

        EditText pageNumberField = view.findViewById(R.id.page_number_text);
        pageNumberField.addTextChangedListener(onPageTextChanged);

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

            // Create an instance for the TableView pagination and pass the created TableView.
            mPagination = new Pagination(mTableView);

            mPagination.setItemsPerPage(TableViewDataFetcher.PER_PAGE);

            // Sets the pagination listener of the TableView pagination to handle
            // pagination actions. See onTableViewPageTurnedListener variable declaration below.
            mPagination.setOnTableViewPageTurnedListener(onTableViewPageTurnedListener);

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        });
    }

    // The following four methods below: nextTablePage(), previousTablePage(),
    // goToTablePage(int page) and setTableItemsPerPage(int itemsPerPage)
    // are for controlling the TableView pagination.
    public void nextTablePage() {
        if (mPagination != null) {
            mPagination.nextPage();
        }
    }

    public void previousTablePage() {
        if (mPagination != null) {
            mPagination.previousPage();
        }
    }

    public void goToTablePage(int page) {
        if (mPagination != null) {
            mPagination.goToPage(page);
        }
    }

    public void setTableItemsPerPage(int itemsPerPage) {
        if (mPagination != null) {
            mPagination.setItemsPerPage(itemsPerPage);
        }
    }

    // Handler for the changing of pages in the paginated TableView.
    @NonNull
    private final Pagination.OnTableViewPageTurnedListener onTableViewPageTurnedListener = new
            Pagination.OnTableViewPageTurnedListener() {
                @Override
                public void onPageTurned(int numItems, int itemsStart, int itemsEnd) {
                    int currentPage = mPagination.getCurrentPage();
                    int pageCount = mPagination.getPageCount();
                    previousButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);

                    if (currentPage == 1 && pageCount == 1) {
                        previousButton.setVisibility(View.INVISIBLE);
                        nextButton.setVisibility(View.INVISIBLE);
                    }

                    if (currentPage == 1) {
                        previousButton.setVisibility(View.INVISIBLE);
                    }

                    if (currentPage == pageCount) {
                        nextButton.setVisibility(View.INVISIBLE);
                    }

                    tablePaginationDetails.setText(getString(R.string.table_pagination_details, String
                            .valueOf(currentPage), String.valueOf(itemsStart), String.valueOf(itemsEnd)));
                }
            };

    @NonNull
    private final AdapterView.OnItemSelectedListener onItemsPerPageSelectedListener = new AdapterView
            .OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int itemsPerPage = Integer.parseInt(parent.getItemAtPosition(position).toString());
            setTableItemsPerPage(itemsPerPage);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @NonNull
    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == previousButton) {
                previousTablePage();
            } else if (v == nextButton) {
                nextTablePage();
            }
        }
    };

    @NonNull
    private final TextWatcher onPageTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int page;
            if (TextUtils.isEmpty(s)) {
                page = 1;
            } else {
                page = Integer.parseInt(String.valueOf(s));
            }

            goToTablePage(page);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private final NetworkConnectCallback onNetConnect = new NetworkConnectCallback() {
        @Override
        public void onNetworkConnected() {
            if (!mTableViewSuccessfullyInitialized) {
                initializeTableView();
            }
        }
    };
}