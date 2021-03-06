/*
 * Copyright 2017 The GDL Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.s1ck.gdl.model.comparables.time;

import org.s1ck.gdl.model.comparables.ComparableExpression;
import org.s1ck.gdl.model.predicates.Predicate;
import org.s1ck.gdl.model.predicates.expressions.Comparison;
import org.s1ck.gdl.utils.Comparator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a constant Timestamp. Wraps a java.time.LocalDateTime
 */
public class TimeLiteral extends TimeAtom {

    /**
     * The wrapped LocalDateTime
     */
    private final LocalDateTime time;

    /**
     * Construct a Literal from UNIX epoch milliseconds
     *
     * @param millis milliseconds since 1970-01-01T00:00
     */
    public TimeLiteral(long millis){
        this.time = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("Z"));
    }

    /**
     * Constructs a literal from a given string of the form YYYY-MM-DDTHH:MM(:SS) or YYYY-MM-DD
     * or "now"
     *
     * @param date the string to construct the DateTime from
     */
    public TimeLiteral(String date){
        try {
            if(date.equalsIgnoreCase("now")){
                this.time = LocalDateTime.now();
            }
            else{
                date = preprocessDateString(date);
                this.time = LocalDateTime.parse(date);
            }
        } catch(DateTimeParseException e) {
            throw new IllegalArgumentException("Date string has the wrong format.");
        }
    }

    /**
     * Constructs a literal from the current time
     */
    public TimeLiteral(){
        this.time = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Z"));
    }

    @Override
    public String toString(){
        return time.toString();
    }

    /**
     * UNIX epoch milliseconds of the wrapped DateTime
     *
     * @return UNIX epoch milliseconds
     */
    public long getMilliseconds(){
        return time.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
    }

    /**
     * The year of the wrapped datetime
     *
     * @return year as int
     */
    public int getYear(){
        return time.getYear();
    }

    /**
     * The month of the wrapped datetime
     *
     * @return month as int
     */
    public int getMonth(){
        return time.getMonthValue();
    }

    /**
     * The day within the month of the wrapped datetime
     *
     * @return month as int
     */
    public int getDay(){
        return time.getDayOfMonth();
    }

    /**
     * The hour of the wrapped datetime
     *
     * @return hour as int
     */
    public int getHour(){
        return time.getHour();
    }

    /**
     * The minute within the hour of the wrapped datetime
     *
     * @return minute as int
     */
    public int getMinute(){
        return time.getMinute();
    }

    /**
     * The second within the minute of the wrapped datetime
     *
     * @return second as int
     */
    public int getSecond(){
        return time.getSecond();
    }


    @Override
    public Set<String> getVariables(){
        return new HashSet<>();
    }

    @Override
    public String getVariable() {
        return null;
    }

    @Override
    public Optional<Long> evaluate(){
        return Optional.of(getMilliseconds());
    }

    @Override
    public boolean containsSelectorType(TimeSelector.TimeField type){
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeLiteral that = (TimeLiteral) o;

        return getMilliseconds()==that.getMilliseconds();
    }

    @Override
    public int hashCode(){
        return (int) getMilliseconds();
    }


    /**
     * Utility method to handle input strings like 1970-01-01. They are not recognized by LocalDateTime.
     * Thus they must be augmented to e.g. 1970-01-01T00:00
     * Error handling is not done here, but when the LocalDateTime is constructed
     *
     * @param date the string input
     * @return the (possibly) augmented string input
     */
    private String preprocessDateString(String date){
        if (date.length()==10){
            date+="T00:00";
        }
        return date;
    }

    @Override
    public Predicate unfoldGlobal(Comparator comp, ComparableExpression rhs, List<String> variables) {
        return new Comparison(this, comp, rhs);
    }

    @Override
    public boolean isGlobal(){
        return false;
    }

    @Override
    public ComparableExpression replaceGlobalByLocal(List<String> variables) {
        return this;
    }
}
