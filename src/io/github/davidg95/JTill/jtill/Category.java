/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Class which models a category.
 *
 * @author David
 */
public class Category implements Serializable {

    private int ID;
    private Department department;
    private String name;
    private Time startSell;
    private Time endSell;
    private boolean timeRestrict;
    private int minAge;

    private BigDecimal sales = BigDecimal.ZERO;

    /**
     * Constructor which takes in all values.
     *
     * @param ID the id.
     * @param name the name.
     * @param startSell the time which items in this category may be sold.
     * @param endSell the time which items in this category will not be allowed
     * to be sold.
     * @param timeRestrict if the time restrictions should apply.
     * @param minAge the minimum age for items in the category.
     * @param dep the department the category belongs to.
     */
    public Category(int ID, String name, Time startSell, Time endSell, boolean timeRestrict, int minAge, Department dep) {
        this(name, startSell, endSell, timeRestrict, minAge, dep);
        this.ID = ID;
    }

    /**
     * Constructor which takes in all values except id.
     *
     * @param name the name.
     * @param startSell the time which items in this category may be sold.
     * @param endSell the time which items in this category will not be allowed
     * to be sold.
     * @param timeRestrict if the time restrictions should apply.
     * @param minAge the minimum age for items in the category.
     * @param dep the department the category belongs to.
     */
    public Category(String name, Time startSell, Time endSell, boolean timeRestrict, int minAge, Department dep) {
        this.name = name;
        this.timeRestrict = timeRestrict;
        this.startSell = startSell;
        this.endSell = endSell;
        if (!timeRestrict) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                this.startSell = new Time(sdf.parse("00:00:00").getTime());
                this.endSell = new Time(sdf.parse("00:00:00").getTime());
            } catch (ParseException ex) {
            }
        }
        this.minAge = minAge;
        this.department = dep;
    }

    /**
     * Returns true or false indicating whether the time passed in is within the
     * selling time of the category.
     *
     * @param t the time to compare.
     * @return true or false.
     */
    public boolean isSellTime(Time t) {
        return t.after(startSell) && t.before(endSell);
    }

    public int getId() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getStartSell() {
        return startSell;
    }

    public void setStartSell(Time startSell) {
        this.startSell = startSell;
    }

    public Time getEndSell() {
        return endSell;
    }

    public void setEndSell(Time endSell) {
        this.endSell = endSell;
    }

    public boolean isTimeRestrict() {
        return timeRestrict;
    }

    public void setTimeRestrict(boolean timeRestrict) {
        this.timeRestrict = timeRestrict;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public void addToSales(BigDecimal toAdd) {
        sales = sales.add(toAdd);
    }

    public BigDecimal getSales() {
        return sales;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * Gets all products in the category.
     *
     * @return a List of all products in the category.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     * @throws JTillException if the category was not found.
     */
    public List<Product> getProductsInCategory() throws IOException, SQLException, JTillException {
        return DataConnect.dataconnect.getProductsInCategory(ID);
    }

    /**
     * Save the category to the database.
     *
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public void save() throws IOException, SQLException {
        try {
            DataConnect.dataconnect.updateCategory(this);
        } catch (JTillException ex) {
            DataConnect.dataconnect.addCategory(this);
        }
    }

    /**
     * Method to get all the categories.
     *
     * @return a List of all the categories.
     * @throws IOException if there is a network error.
     * @throws SQLException if there is a database error.
     */
    public static List<Category> getAll() throws IOException, SQLException {
        return DataConnect.dataconnect.getAllCategorys();
    }

    public String getSQLInsertString() {
        return "'" + this.name
                + "','" + this.startSell.toString()
                + "','" + this.endSell.toString()
                + "','" + this.timeRestrict
                + "'," + this.minAge
                + "," + this.department.getId();
    }

    public String getSQLUpdateString() {
        if (this.isTimeRestrict()) {
            return "UPDATE CATEGORYS"
                    + " SET NAME='" + this.getName()
                    + "', SELL_START='" + this.getStartSell().toString()
                    + "', SELL_END='" + this.getEndSell().toString()
                    + "', TIME_RESTRICT=" + this.isTimeRestrict()
                    + ", MINIMUM_AGE=" + this.getMinAge()
                    + ", DEPARTMENT=" + this.getDepartment().getId()
                    + " WHERE CATEGORYS.ID=" + this.getId();
        } else {
            return "UPDATE CATEGORYS"
                    + " SET NAME='" + this.getName()
                    + "', TIME_RESTRICT=" + this.isTimeRestrict()
                    + ", MINIMUM_AGE=" + this.getMinAge()
                    + ", DEPARTMENT=" + this.getDepartment().getId()
                    + " WHERE CATEGORYS.ID=" + this.getId();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.ID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Category other = (Category) obj;
        return this.ID == other.ID;
    }

    @Override
    public String toString() {
        return this.ID + " - " + this.name;
    }
}
