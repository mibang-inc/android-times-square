// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import android.app.Activity;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.intellij.lang.annotations.MagicConstant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.MULTIPLE;
import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;
import static com.squareup.timessquare.CalendarPickerView.SelectionMode.SINGLE;
import static com.squareup.timessquare.MonthCellDescriptor.RangeState.FIRST;
import static com.squareup.timessquare.MonthCellDescriptor.RangeState.LAST;
import static com.squareup.timessquare.MonthCellDescriptor.RangeState.MIDDLE;
import static com.squareup.timessquare.MonthCellDescriptor.RangeState.NONE;
import static java.util.Calendar.APRIL;
import static java.util.Calendar.AUGUST;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.FEBRUARY;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static java.util.Calendar.JUNE;
import static java.util.Calendar.MARCH;
import static java.util.Calendar.MAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.NOVEMBER;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.SEPTEMBER;
import static java.util.Calendar.YEAR;
import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

@RunWith(RobolectricTestRunner.class)
public class CalendarPickerViewTest {
    private CalendarPickerView view;
    private Calendar today;
    private Date maxDate;
    private Date minDate;


    private Date d1 = getDate(1);
    private Date d2 = getDate(2);
    private Date d20 = getDate(20);
    private Date d3 = getDate(3);
    private Date d4 = getDate(4);
    private Date d5 = getDate(5);
    private Date d6 = getDate(6);
    private Date d8 = getDate(8);

    @Before
    public void setUp() throws Exception {
        view = new CalendarPickerView(new Activity(), null);
        today = Calendar.getInstance();
        today.set(2012, NOVEMBER, 16, 0, 0);
        minDate = today.getTime();
        today.set(2013, NOVEMBER, 16, 0, 0);
        maxDate = today.getTime();
        today.set(2012, NOVEMBER, 16, 0, 0);
        Date startDate = today.getTime();
        view.today.setTime(startDate);
        view.init(minDate, maxDate) //
            .inMode(SINGLE) //
            .withSelectedDate(startDate);
    }

    @Test
    public void testRuleFilterByDates() throws Exception {
        Collection<Integer> rules = CloseRule.init(d1, d6)
            .withDisableDates(Arrays.asList(d3, d4))
            .build();

        assertThat(CloseRule.inRules(d4, rules)).isTrue();
        assertThat(CloseRule.inRules(d5, rules)).isFalse();

        view.init(minDate, maxDate, rules) //
            .inMode(RANGE) //
            .withSelectedDates(Arrays.asList(d1, d6));
        assertThat(view.getSelectedDates()).hasSize(4);
    }

    @Test
    public void testHashSet() throws Exception {
        Set<Integer> set = new HashSet<Integer>();
        set.add(new Integer(1));
        set.add(new Integer(1));
        assertThat(set.size()).isEqualTo(1);
    }

    //@Test
    //public void testRuleFilterByWeekday() throws Exception {
        //Date d1 = getDate(1);
        //Calendar cal = Calendar.getInstance();
        //cal.setTime(d1);
        //assertThat(cal.get(Calendar.DAY_OF_WEEK)).isEqualTo(Calendar.THURSDAY);
        //Collection<Integer> rules = CloseRule.init(d1, d20)
            //.withOpenWeekday(Calendar.THURSDAY)
            //.build();

        //assertThat(CloseRule.inRules(d1, rules)).isFalse();
        //assertThat(CloseRule.inRules(d2, rules)).isTrue();
        //assertThat(CloseRule.inRules(d8, rules)).isFalse();
    //}

    //@Test
    //public void testRuleFilterByMonth() throws Exception {
        //Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.MONTH, -2);
        //Collection<Integer> rules = CloseRule.init(cal.getTime(), d20)
            //.withOpenMonth(Calendar.AUGUST)
            //.build();
        //assertThat(CloseRule.inRules(d1, rules)).isFalse();

        //cal = Calendar.getInstance();
        //cal.add(Calendar.MONTH, -1);
        //assertThat(cal.get(Calendar.MONTH)).isEqualTo(Calendar.JULY);
        //assertThat(CloseRule.inRules(cal.getTime(), rules)).isTrue();

        ////
        //cal = Calendar.getInstance();
        //cal.add(Calendar.MONTH, -1);
        //Date prevMonth = cal.getTime(); 
        //cal = Calendar.getInstance();
        //cal.add(Calendar.MONTH, 1);
        //Date nextMonth = cal.getTime(); 
        //rules = CloseRule.init(prevMonth, nextMonth)
            //.withOpenMonth(Calendar.AUGUST)
            //.build();
        //view.init(minDate, maxDate, rules) //
            //.inMode(RANGE) //
            //.withSelectedDates(Arrays.asList(prevMonth, nextMonth));

        //cal.set(Calendar.MONTH,  Calendar.SEPTEMBER);
        //cal.set(Calendar.DAY_OF_MONTH,  23);
        //assertThat(CloseRule.inRules(cal.getTime(), rules)).isFalse();
    //}

    @Test
    public void testRuleFilterByPreOrder() throws Exception {
        int delta = 0;
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 6);
        Date mornning = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        Date night = cal.getTime();

        Date today = new Date();

        Collection<Integer> rules = CloseRule.init(cal.getTime(), d20)
            .withPreorder(0, mornning)
            .build();
        assertThat(CloseRule.inRules(today, rules)).isFalse();

        rules = CloseRule.init(cal.getTime(), d20)
            .withPreorder(0, night)
            .build();
        assertThat(CloseRule.inRules(today, rules)).isTrue();
    }

    private static Date getDate(int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, date);
        return cal.getTime();
    }

    @Test
    public void testRuleIntegers() throws Exception {
        List<Integer> rules = CloseRule.getRules();
        assertThat(CloseRule.inRules(new Date(), rules)).isTrue();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        assertThat(CloseRule.inRules(cal.getTime(), rules)).isFalse();
    }

    
    public void testInitDecember() throws Exception {
        Calendar dec2012 = buildCal(2012, DECEMBER, 1);
        Calendar dec2013 = buildCal(2013, DECEMBER, 1);
        view.init(dec2012.getTime(), dec2013.getTime()) //
            .inMode(SINGLE) //
            .withSelectedDate(dec2012.getTime());
        assertThat(view.months).hasSize(12);
    }

    
    public void testInitJanuary() throws Exception {
        Calendar jan2012 = buildCal(2012, JANUARY, 1);
        Calendar jan2013 = buildCal(2013, JANUARY, 1);
        view.init(jan2012.getTime(), jan2013.getTime()) //
            .inMode(SINGLE) //
            .withSelectedDate(jan2012.getTime());
        assertThat(view.months).hasSize(12);
    }

    
    public void testInitMidyear() throws Exception {
        Calendar may2012 = buildCal(2012, MAY, 1);
        Calendar may2013 = buildCal(2013, MAY, 1);
        view.init(may2012.getTime(), may2013.getTime()) //
            .inMode(SINGLE) //
            .withSelectedDate(may2012.getTime());
        assertThat(view.months).hasSize(12);
    }

    
    public void testOnlyShowingFourWeeks() throws Exception {
        List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(FEBRUARY, 2015, today);
        assertThat(cells).hasSize(4);

        // Last cell should be 1.
        assertCell(cells, 0, 0, 1, true, false, false, false, NONE);

        // Last cell should be 28.
        assertCell(cells, 3, 6, 28, true, false, false, false, NONE);
    }

    
    public void testOnlyShowingFiveWeeks() throws Exception {
        List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(FEBRUARY, 2013, today);
        assertThat(cells).hasSize(5);

        // First cell is the 27th of January.
        assertCell(cells, 0, 0, 27, false, false, false, false, NONE);

        // First day of Feb falls on the 5th cell.
        assertCell(cells, 0, 5, 1, true, false, false, true, NONE);

        // Last day of Feb falls on the 5th row, 5th column.
        assertCell(cells, 4, 4, 28, true, false, false, true, NONE);

        // Last cell should be March 2nd.
        assertCell(cells, 4, 6, 2, false, false, false, false, NONE);
    }

    
    public void testWeirdOverlappingYear() throws Exception {
        List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(JANUARY, 2013, today);
        assertThat(cells).hasSize(5);
    }

    
    public void testShowingSixWeeks() throws Exception {
        List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(DECEMBER, 2012, today);
        assertThat(cells).hasSize(6);

        // First cell is the 27th of November.
        assertCell(cells, 0, 0, 25, false, false, false, false, NONE);

        // First day of December falls on the 6th cell.
        assertCell(cells, 0, 6, 1, true, false, false, true, NONE);

        // Last day of December falls on the 6th row, 2nd column.
        assertCell(cells, 5, 1, 31, true, false, false, true, NONE);

        // Last cell should be January 5th.
        assertCell(cells, 5, 6, 5, false, false, false, false, NONE);
    }

    
    public void testIsSelected() throws Exception {
        Calendar nov29 = buildCal(2012, NOVEMBER, 29);

        List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(NOVEMBER, 2012, nov29);
        assertThat(cells).hasSize(5);
        // Make sure the cell is selected when it's in November.
        assertCell(cells, 4, 4, 29, true, true, false, true, NONE);

        cells = selectDateAndGetCells(DECEMBER, 2012, nov29);
        assertThat(cells).hasSize(6);
        // Make sure the cell is not selected when it's in December.
        assertCell(cells, 0, 4, 29, false, false, false, false, NONE);
    }

    
    public void testTodayIsToday() throws Exception {
        List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(NOVEMBER, 2012, today);
        assertCell(cells, 2, 5, 16, true, true, true, true, NONE);
    }

    
    public void testSelectabilityInFirstMonth() throws Exception {
        List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(NOVEMBER, 2012, today);
        // 10/29 is not selectable because it's in the previous month.
        assertCell(cells, 0, 0, 28, false, false, false, false, NONE);
        // 11/1 is not selectable because it's < minDate (11/16/12).
        assertCell(cells, 0, 4, 1, true, false, false, false, NONE);
        // 11/16 is selectable because it's == minDate (11/16/12).
        assertCell(cells, 2, 5, 16, true, true, true, true, NONE);
        // 11/20 is selectable because it's > minDate (11/16/12).
        assertCell(cells, 3, 2, 20, true, false, false, true, NONE);
        // 12/1 is not selectable because it's in the next month.
        assertCell(cells, 4, 6, 1, false, false, false, false, NONE);
    }

    
    public void testSelectabilityInLastMonth() throws Exception {
        List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(NOVEMBER, 2013, today);
        // 10/29 is not selectable because it's in the previous month.
        assertCell(cells, 0, 0, 27, false, false, false, false, NONE);
        // 11/1 is selectable because it's < maxDate (11/16/13).
        assertCell(cells, 0, 5, 1, true, false, false, true, NONE);
        // 11/15 is selectable because it's one less than maxDate (11/16/13).
        assertCell(cells, 2, 5, 15, true, false, false, true, NONE);
        // 11/16 is not selectable because it's > maxDate (11/16/13).
        assertCell(cells, 2, 6, 16, true, false, false, false, NONE);
    }

    
    public void testInitSingleWithMultipleSelections() throws Exception {
        List<Date> selectedDates = new ArrayList<Date>();
        selectedDates.add(minDate);
        // This one should work.
        view.init(minDate, maxDate) //
            .inMode(SINGLE) //
            .withSelectedDates(selectedDates);

        // Now add another date and try init'ing again in SINGLE mode.
        Calendar secondSelection = buildCal(2012, NOVEMBER, 17);
        selectedDates.add(secondSelection.getTime());
        try {
            view.init(minDate, maxDate) //
                .inMode(SINGLE) //
                .withSelectedDates(selectedDates);
            fail("Should not have been able to init() with SINGLE mode && multiple selected dates");
        } catch (IllegalArgumentException expected) {
        }
    }

    
    public void testNullDates() throws Exception {
        final Date validDate = today.getTime();
        try {
            view.init(validDate, validDate) //
                .inMode(SINGLE) //
                .withSelectedDate(null);
            fail("Should not have been able to pass in a null startDate");
        } catch (IllegalArgumentException expected) {
        }
        try {
            view.init(null, validDate) //
                .inMode(SINGLE) //
                .withSelectedDate(validDate);
            fail("Should not have been able to pass in a null minDate");
        } catch (IllegalArgumentException expected) {
        }
        try {
            view.init(validDate, null) //
                .inMode(SINGLE) //
                .withSelectedDate(validDate);
            fail("Should not have been able to pass in a null maxDate");
        } catch (IllegalArgumentException expected) {
        }
    }

    
    public void testZeroDates() throws Exception {
        final Date validDate = today.getTime();
        final Date zeroDate = new Date(0L);
        try {
            view.init(validDate, validDate) //
                .inMode(SINGLE) //
                .withSelectedDate(zeroDate);
            fail("Should not have been able to pass in a zero startDate");
        } catch (IllegalArgumentException expected) {
        }
        try {
            view.init(zeroDate, validDate) //
                .inMode(SINGLE) //
                .withSelectedDate(validDate);
            fail("Should not have been able to pass in a zero minDate");
        } catch (IllegalArgumentException expected) {
        }
        try {
            view.init(validDate, zeroDate) //
                .inMode(SINGLE) //
                .withSelectedDate(validDate);
            fail("Should not have been able to pass in a zero maxDate");
        } catch (IllegalArgumentException expected) {
        }
    }

    
    public void testMinAndMaxMixup() throws Exception {
        final Date minDate = today.getTime();
        today.add(YEAR, -1);
        final Date maxDate = today.getTime();
        try {
            view.init(minDate, maxDate) //
                .inMode(SINGLE) //
                .withSelectedDate(minDate);
            fail("Should not have been able to pass in a maxDate < minDate");
        } catch (IllegalArgumentException expected) {
        }
    }

    
    public void testSelectedNotInRange() throws Exception {
        final Date minDate = today.getTime();
        today.add(YEAR, 1);
        final Date maxDate = today.getTime();
        today.add(YEAR, 1);
        Date selectedDate = today.getTime();
        try {
            view.init(minDate, maxDate) //
                .inMode(SINGLE) //
                .withSelectedDate(selectedDate);
            fail("Should not have been able to pass in a selectedDate > maxDate");
        } catch (IllegalArgumentException expected) {
        }
        today.add(YEAR, -5);
        selectedDate = today.getTime();
        try {
            view.init(minDate, maxDate) //
                .inMode(SINGLE) //
                .withSelectedDate(selectedDate);
            fail("Should not have been able to pass in a selectedDate < minDate");
        } catch (IllegalArgumentException expected) {
        }
    }

    
    public void testNotCallingInit() throws Exception {
        view = new CalendarPickerView(new Activity(), null);
        try {
            view.onMeasure(0, 0);
            fail("Should have thrown an IllegalStateException!");
        } catch (IllegalStateException expected) {
        }
    }

    
    public void testShowingOnlyOneMonth() throws Exception {
        Calendar feb1 = buildCal(2013, FEBRUARY, 1);
        Calendar mar1 = buildCal(2013, MARCH, 1);
        view.init(feb1.getTime(), mar1.getTime()) //
            .inMode(SINGLE) //
            .withSelectedDate(feb1.getTime());
        assertThat(view.months).hasSize(1);
    }

    
    public void selectDateThrowsExceptionForDatesOutOfRange() {
        view.init(minDate, maxDate) //
            .inMode(SINGLE) //
            .withSelectedDate(today.getTime());
        Calendar outOfRange = buildCal(2015, FEBRUARY, 1);
        try {
            view.selectDate(outOfRange.getTime());
            fail("selectDate should've blown up with an out of range date");
        } catch (IllegalArgumentException expected) {
        }
    }

    
    public void selectDateReturnsTrueForDateInRange() {
        view.init(minDate, maxDate) //
            .inMode(SINGLE) //
            .withSelectedDate(today.getTime());
        Calendar inRange = buildCal(2013, FEBRUARY, 1);
        boolean wasAbleToSetDate = view.selectDate(inRange.getTime());
        assertThat(wasAbleToSetDate).isTrue();
    }

    
    public void selectDateDoesntSelectDisabledCell() {
        view.init(minDate, maxDate) //
            .inMode(SINGLE) //
            .withSelectedDate(today.getTime());
        Calendar jumpToCal = buildCal(2013, FEBRUARY, 1);
        boolean wasAbleToSetDate = view.selectDate(jumpToCal.getTime());
        assertThat(wasAbleToSetDate).isTrue();
        assertThat(view.selectedCells.get(0).isSelectable()).isTrue();
    }

    
    public void testMultiselectWithNoInitialSelections() throws Exception {
        view.init(minDate, maxDate) //
            .inMode(MULTIPLE);
        assertThat(view.selectionMode).isEqualTo(MULTIPLE);
        assertThat(view.getSelectedDates()).isEmpty();

        view.selectDate(minDate);
        assertThat(view.getSelectedDates()).hasSize(1);

        Calendar secondSelection = buildCal(2012, NOVEMBER, 17);
        view.selectDate(secondSelection.getTime());
        assertThat(view.getSelectedDates()).hasSize(2);
        assertThat(view.getSelectedDates().get(1)).hasTime(secondSelection.getTimeInMillis());
    }

    
    public void testOnDateConfiguredListener() {
        final Calendar testCal = Calendar.getInstance();
        view.setDateSelectableFilter(new CalendarPickerView.DateSelectableFilter() {
            @Override public boolean isDateSelectable(Date date) {
            testCal.setTime(date);
            int dayOfWeek = testCal.get(DAY_OF_WEEK);
            return dayOfWeek > 1 && dayOfWeek < 7;
            }
        });
        view.init(minDate, maxDate) //
            .inMode(SINGLE) //
            .withSelectedDate(today.getTime());
        Calendar jumpToCal = Calendar.getInstance();
        jumpToCal.add(MONTH, 2);
        jumpToCal.set(DAY_OF_WEEK, 1);
        boolean wasAbleToSetDate = view.selectDate(jumpToCal.getTime());
        assertThat(wasAbleToSetDate).isFalse();

        jumpToCal.set(DAY_OF_WEEK, 2);
        wasAbleToSetDate = view.selectDate(jumpToCal.getTime());
        assertThat(wasAbleToSetDate).isTrue();
    }

    
    public void testRangeSelectionWithNoInitialSelection() throws Exception {
        view.init(minDate, maxDate)
            .inMode(RANGE);
        assertThat(view.selectedCals).hasSize(0);
        assertThat(view.selectedCells).hasSize(0);

        Calendar nov18 = buildCal(2012, NOVEMBER, 18);
        view.selectDate(nov18.getTime());
        assertOneDateSelected();

        Calendar nov24 = buildCal(2012, NOVEMBER, 24);
        view.selectDate(nov24.getTime());
        assertRangeSelected();

        assertRangeSelectionBehavior();
    }

    private void assertRangeSelectionBehavior() {
        // Start a new range in the middle of the current (Nov 18 - Nov 24) one.
        Calendar nov20 = buildCal(2012, NOVEMBER, 20);
        view.selectDate(nov20.getTime());
        assertOneDateSelected();

        // Finish that range.
        Calendar nov26 = buildCal(2012, NOVEMBER, 26);
        view.selectDate(nov26.getTime());
        assertRangeSelected();

        // Start a new range in the middle of the current (Nov 20 - Nov 26) one.
        Calendar nov24 = buildCal(2012, NOVEMBER, 24);
        view.selectDate(nov24.getTime());
        assertOneDateSelected();

        // Only Nov 24 is selected: going backward should start a new range.
        view.selectDate(nov20.getTime());
        assertOneDateSelected();
    }

    
    //@Test
    public void testRangeWithTwoInitialSelections() throws Exception {
        Calendar nov18 = buildCal(2012, NOVEMBER, 18);
        Calendar nov24 = buildCal(2012, NOVEMBER, 24);
        List<Date> selectedDates = Arrays.asList(nov18.getTime(), nov24.getTime());
        view.init(minDate, maxDate)
            .inMode(RANGE)
            .withSelectedDates(selectedDates);
        assertRangeSelected();

        assertRangeSelectionBehavior();
    }

    
    public void testRangeWithOneInitialSelection() throws Exception {
        Calendar nov18 = buildCal(2012, NOVEMBER, 18);
        Calendar nov24 = buildCal(2012, NOVEMBER, 24);
        List<Date> selectedDates = Arrays.asList(nov18.getTime());
        view.init(minDate, maxDate)
            .inMode(RANGE)
            .withSelectedDates(selectedDates);
        assertOneDateSelected();

        view.selectDate(nov24.getTime());
        assertRangeSelected();

        assertRangeSelectionBehavior();
    }

    private void assertRangeSelected() {
        //assertThat(view.selectedCals).hasSize(2);
        assertThat(view.selectedCells).hasSize(5);
        assertThat(view.getSelectedDates()).hasSize(5);
    }

    private void assertOneDateSelected() {
        assertThat(view.selectedCals).hasSize(1);
        assertThat(view.selectedCells).hasSize(1);
        assertThat(view.getSelectedDates()).hasSize(1);
    }

    
    public void testRangeStateOnDateSelections() {
        Calendar startCal = buildCal(2012, NOVEMBER, 17);
        Calendar endCal = buildCal(2012, NOVEMBER, 24);

        view.init(minDate, maxDate) //
            .inMode(RANGE);

        boolean wasAbleToSetDate = view.selectDate(startCal.getTime());
        assertThat(wasAbleToSetDate).isTrue();

        wasAbleToSetDate = view.selectDate(endCal.getTime());
        assertThat(wasAbleToSetDate).isTrue();

        List<List<MonthCellDescriptor>> cells = getCells(NOVEMBER, 2012);
        assertCell(cells, 2, 6, 17, true, true, false, true, FIRST);
        assertCell(cells, 3, 0, 18, true, false, false, true, MIDDLE);
        assertCell(cells, 3, 1, 19, true, false, false, true, MIDDLE);
        assertCell(cells, 3, 2, 20, true, false, false, true, MIDDLE);
        assertCell(cells, 3, 3, 21, true, false, false, true, MIDDLE);
        assertCell(cells, 3, 4, 22, true, false, false, true, MIDDLE);
        assertCell(cells, 3, 5, 23, true, false, false, true, MIDDLE);
        assertCell(cells, 3, 6, 24, true, true, false, true, LAST);
    }

    
    public void testLocaleSetting() throws Exception {
        view.init(minDate, maxDate) //
            .withLocale(Locale.GERMAN);
        MonthView monthView = (MonthView) view.getAdapter().getView(1, null, null);
        CalendarRowView header = (CalendarRowView) monthView.grid.getChildAt(0);
        TextView firstDay = (TextView) header.getChildAt(0);
        assertThat(firstDay).hasTextString("So"); // Sonntag = Sunday
        assertThat(monthView.title).hasTextString("Dezember 2012");
    }

    
    public void testFirstDayOfWeekIsMonday() throws Exception {
        Locale originalLocale = Locale.getDefault();
        Locale greatBritain = new Locale("en", "GB");
        // Verify that firstDayOfWeek is actually Monday.
        Calendar cal = Calendar.getInstance(greatBritain);
        assertThat(cal.getFirstDayOfWeek()).isEqualTo(Calendar.MONDAY);

        // Set the locale and run "setUp" again.
        Locale.setDefault(greatBritain);
        setUp();

        try {
            view.init(minDate, maxDate);
            MonthView monthView = (MonthView) view.getAdapter().getView(1, null, null);
            CalendarRowView header = (CalendarRowView) monthView.grid.getChildAt(0);
            TextView firstDay = (TextView) header.getChildAt(0);
            assertThat(firstDay).hasTextString("Mon"); // Monday!

            // Now verify that the generated cells are right.
            List<List<MonthCellDescriptor>> cells = getCells(SEPTEMBER, 2013);
            assertThat(cells).hasSize(6);
            assertCell(cells, 0, 0, 26, false, false, false, false, NONE);
            assertCell(cells, 1, 0, 2, true, false, false, true, NONE);
            assertCell(cells, 5, 0, 30, true, false, false, true, NONE);
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    private static void assertCell(List<List<MonthCellDescriptor>> cells, int row, int col,
            int expectedVal, boolean expectedCurrentMonth, boolean expectedSelected,
            boolean expectedToday, boolean expectedSelectable,
            MonthCellDescriptor.RangeState expectedRangeState) {
        final MonthCellDescriptor cell = cells.get(row).get(col);
        assertThat(cell.getValue()).isEqualTo(expectedVal);
        assertThat(cell.isCurrentMonth()).isEqualTo(expectedCurrentMonth);
        assertThat(cell.isSelected()).isEqualTo(expectedSelected);
        assertThat(cell.isToday()).isEqualTo(expectedToday);
        assertThat(cell.isSelectable()).isEqualTo(expectedSelectable);
        assertThat(cell.getRangeState()).isEqualTo(expectedRangeState);
    }

    private List<List<MonthCellDescriptor>> selectDateAndGetCells(int month, int year,
            Calendar selectedDate) {
        view.selectDate(selectedDate.getTime());
        return getCells(month, year);
    }

    private List<List<MonthCellDescriptor>> getCells(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(DAY_OF_MONTH, 1);
        cal.set(YEAR, year);
        cal.set(MONTH, month);
        return view.getMonthCells(new MonthDescriptor(month, year, cal.getTime(), "January 2012"), cal);
    }

    private Calendar buildCal(int year, @MagicConstant(intValues = {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER,
    DECEMBER
    }) int month, int day) {
        Calendar jumpToCal = Calendar.getInstance();
        jumpToCal.set(year, month, day);
        CalendarPickerView.setMidnight(jumpToCal);
        return jumpToCal;
    }
}
