package com.vu;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryDataModel extends AbstractTableModel {


    private int rowCount = 0;
    private int colCount = 0;
    private static ResultSet resultSet;



    public InventoryDataModel(ResultSet rs) {
        this.resultSet = rs;
        setup();

    }

    public void setup() {
        countRows();

        try {
            colCount = resultSet.getMetaData().getColumnCount();
        } catch (SQLException se) {
            System.out.println("Error counting columns " + se);
        }
    }

    public void updateResultSet(ResultSet newRS) {
        resultSet = newRS;
        setup();
    }

    public void countRows() {
        rowCount = 0;
        try {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                rowCount++;
            }
            resultSet.beforeFirst();
        } catch (SQLException se) {
            System.out.println("Error counting rows " + se);
        }
    }

    @Override
    public int getRowCount() {
        countRows();
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return colCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        try {
            resultSet.absolute(rowIndex + 1);
            Object o = resultSet.getObject(colIndex + 1);
            return o.toString();
        } catch (SQLException se) {
            System.out.println("Error getting value for " + rowIndex + " " + colIndex + "\n" + se);
            return "Error";
        }
    }

    @Override
    public void setValueAt(Object newValue, int rowIndex, int colIndex) {

        double newOfficeCount, newBarCount;


        try {
            newOfficeCount = Double.parseDouble(newValue.toString());
            newBarCount = Double.parseDouble(newValue.toString());
            if (newOfficeCount < 0 || newBarCount < 0) {
                throw new NumberFormatException("Count must be a positive number");
            }
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(null, "Try entering a positive number of 0 or above");
            return;
        }

        try {
            resultSet.absolute(rowIndex + 1);
            resultSet.updateDouble(InventoryDatabase.OFFICE_COUNT_COLUMN, newOfficeCount);
            resultSet.updateDouble(InventoryDatabase.BAR_COUNT_COLUMN, newBarCount);
            resultSet.updateRow();
            fireTableDataChanged();
        } catch (SQLException e) {
            System.out.println("Error changing inventory count " + e);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
        if (colIndex == 4 || colIndex == 5) {
            return true;
        }
        return false;
    }

    public boolean deleteRow(int rowIndex) {
        try {
            resultSet.absolute(rowIndex + 1);
            resultSet.deleteRow();
            fireTableDataChanged();
            return true;
        } catch (SQLException se) {
            System.out.println("Delete row error " + se);
            return false;
        }
    }

    public boolean insertRow(String product, String brand, String type, double amount_office, double amount_bar, double totalAmount, String order, String distributor) {
        try {
            resultSet.moveToInsertRow();
            resultSet.updateString(InventoryDatabase.PRODUCT_COLUMN, product);
            resultSet.updateString(InventoryDatabase.BRAND_COLUMN, brand);
            resultSet.updateString(InventoryDatabase.TYPE_COLUMN, type);
            resultSet.updateDouble(InventoryDatabase.OFFICE_COUNT_COLUMN, amount_office);
            resultSet.updateDouble(InventoryDatabase.BAR_COUNT_COLUMN, amount_bar);
            resultSet.updateDouble(InventoryDatabase.TOTAL_COLUMN, totalAmount);
            resultSet.updateString(InventoryDatabase.ORDER_COLUMN, order);
            resultSet.updateString(InventoryDatabase.DISTRIBUTOR_COLUMN, distributor);
            resultSet.insertRow();
            resultSet.moveToCurrentRow();
            fireTableDataChanged();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding row");
            System.out.println(e);
            return false;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        try {
            return resultSet.getMetaData().getColumnName(columnIndex + 1);
        } catch (SQLException se) {
            System.out.println("Error fetching column names" + se);
            return "?";
        }
    }
}
