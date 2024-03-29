package com.example.loginregister.datasets;

import androidx.annotation.NonNull;

public class PlanInfo {
    public String startdate;
    public String enddate;
    public double plan_weight;
    public double plan_weightnow;
    public double final_weight;
    public String nowstartdate;
    public double nowplan_weight;
    public double nowplan_weightnow;
    public String toString(){
        return "[" + startdate + "," + enddate + "," + plan_weight + "," + plan_weightnow + "," +
                final_weight + "]";
    }
    public String toStringG(){
        return "[" + nowstartdate + "," + nowplan_weight + "," + nowplan_weightnow  + "]";
    }
}
