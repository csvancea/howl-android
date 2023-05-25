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

package com.smd.cv.howl.tableview;

import androidx.annotation.NonNull;

import com.smd.cv.howl.api.Measurement;
import com.smd.cv.howl.tableview.model.Cell;
import com.smd.cv.howl.tableview.model.ColumnHeader;
import com.smd.cv.howl.tableview.model.RowHeader;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by evrencoskun on 4.02.2018.
 */

public class TableViewModel {
    private final List<List<Cell>> mCellList;
    private final List<RowHeader> mRowHeaderList;
    private final List<ColumnHeader> mColumnHeaderList;

    public TableViewModel(@NonNull List<Measurement> measurements) {
        mRowHeaderList = measurements
                .stream()
                .map(e -> new RowHeader(String.valueOf(e.id), "# " + e.id))
                .collect(Collectors.toList());

        mCellList = measurements
                .stream()
                .map(e -> {
                    List<Cell> cellList = new ArrayList<>();

                    String gasCell = e.gasValue + (e.gasDetected ? " (!)" : "");
                    String flameCell = (e.flameDetected ? "Detected (!)" : "OK");
                    // String dateCell = convertToLocalDateTimeViaInstant(e.createdAt).toString();
                    String dateCell = e.createdAt.toString();

                    cellList.add(new Cell(UUID.randomUUID().toString(), gasCell));
                    cellList.add(new Cell(UUID.randomUUID().toString(), flameCell));
                    cellList.add(new Cell(UUID.randomUUID().toString(), dateCell));
                    return cellList;
                })
                .collect(Collectors.toList());

        mColumnHeaderList = new ArrayList<>();
        mColumnHeaderList.add(new ColumnHeader("gas", "Gas"));
        mColumnHeaderList.add(new ColumnHeader("flame", "Flame"));
        mColumnHeaderList.add(new ColumnHeader("date", "Date"));
    }

    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @NonNull
    public List<List<Cell>> getCellList() {
        return mCellList;
    }

    @NonNull
    public List<RowHeader> getRowHeaderList() {
        return mRowHeaderList;
    }

    @NonNull
    public List<ColumnHeader> getColumnHeaderList() {
        return mColumnHeaderList;
    }
}
