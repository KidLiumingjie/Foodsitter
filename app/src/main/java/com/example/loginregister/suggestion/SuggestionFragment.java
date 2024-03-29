package com.example.loginregister.suggestion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginregister.R;
import com.example.loginregister.datasets.DietStatus;
import com.example.loginregister.datasets.FoodInfo;
import com.example.loginregister.datasets.GoalActiveLevelNu;
import com.example.loginregister.datasets.NowPlanInfo;
import com.example.loginregister.datasets.NuInfo;
import com.example.loginregister.datasets.UserInfo;
import com.example.loginregister.home.SharedViewModel;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SuggestionFragment extends Fragment {
    public SuggestionFragment() {
        super(R.layout.fragment_suggest_food);
    }

    Context mContext;
    SharedViewModel viewModel;
    View mView;
    UserInfo userData;
    NowPlanInfo nowplandata;
    CustomList customList;
    ProgressBar progressBar;
    TextView caloriesLimit, caloriesHad, proteinHad, carbsHad, fatHad, noResult;
    ProgressBar caloriesProgress, proteinProgress, carbsProgress, fatProgress;
    ListView lvShow;
    int goal, activeLevel;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        final Observer<DietStatus> statusObserver = new Observer<DietStatus>() {
            @Override
            public void onChanged(DietStatus dietStatus) {
                // Update the UI.
                update();
                Log.i("dietStatus", viewModel.getDietStatus().getValue().toString());
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getDietStatus().observe(getViewLifecycleOwner(), statusObserver);

        final Observer<GoalActiveLevelNu> goalActiveLevelNuObserver = new Observer<GoalActiveLevelNu>() {
            @Override
            public void onChanged(GoalActiveLevelNu goalActiveLevel) {
                // Update the UI.
                viewModel.setDietStatus(getInitialDietStatus(userData));
                caloriesLimit.setText(String.format("%s", viewModel.getDietStatus().getValue().CaloriesPerMeal));
                viewModel.emptyCart();
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
//        viewModel.getGoalActiveLevel().observe(getViewLifecycleOwner(), goalActiveLevelObserver);


        mView = view;

        caloriesLimit = view.findViewById(R.id.calories_limit);
        caloriesHad = view.findViewById(R.id.calories_had);
        caloriesProgress = view.findViewById(R.id.caloriesProgress);
        proteinHad = view.findViewById(R.id.protein_had);
        proteinProgress = view.findViewById(R.id.proteinProgress);
        carbsHad = view.findViewById(R.id.carbs_had);
        carbsProgress = view.findViewById(R.id.carbsProgress);
        fatHad = view.findViewById(R.id.fat_had);
        fatProgress = view.findViewById(R.id.fatProgress);
        lvShow = view.findViewById(R.id.lv_show);
        progressBar = view.findViewById(R.id.progressBar);
        noResult = view.findViewById(R.id.tv_no_result);

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String uname = pref.getString("username", null);
            Log.i("username", uname);
            viewModel.getCon().run();

            nowplandata = viewModel.getCon().getnowplanData(uname);
            Log.i("nowplanData", nowplandata.toString());
            double  weight= nowplandata.nowplan_weight;
            double weightnow=nowplandata.nowplan_weightnow;
            String  exercise=nowplandata.exercise;

            if (weight-weightnow>0){
                goal=2;
            }
            else if (weight-weightnow==0){
                goal=1;
            }
            else   {goal=0;}
            if (Objects.equals(exercise, "高度")){
                activeLevel=2;
            }
            else if (Objects.equals(exercise, "中度")){
                activeLevel=1;
            }
            else   {activeLevel=0;}

            view.post(() -> {
                viewModel.setGoalActiveLevel(goal, activeLevel);
            });

            NuInfo nudata = viewModel.getCon().getNuData(uname);
            Log.i("NUData", nudata.toString());//營養素在這
            view.post(() -> {
               viewModel.setNu(nudata);
            });

            userData = viewModel.getCon().getUserData(uname);
            Log.v("OK", "使用者資料已回傳");
            Log.i("userData", userData.toString());
            viewModel.setUserId(userData.user_id);

            viewModel.getSuggestion();

            view.post(() -> {
                viewModel.getGoalActiveLevelNu().observe(getViewLifecycleOwner(), goalActiveLevelNuObserver);
            });

        }).start();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DietStatus getInitialDietStatus(UserInfo userInfo) {
        DietStatus dietStatus = new DietStatus();
        NuInfo nudata = viewModel.getGoalActiveLevelNu().getValue().NuInfo;

        if (nudata.calorie == 0 && nudata.protein == 0 && nudata.carbohy == 0 && nudata.fat == 0) {
            int age = 0;
            double BMR = 0, TDEE, proteinRatio, carbsRatio, fatRatio;

            Calendar bday = Calendar.getInstance();
            bday.setTime(userInfo.birthday);

            age = Period.between(
                            LocalDate.of(bday.get(Calendar.YEAR), bday.get(Calendar.MONTH), bday.get(Calendar.DAY_OF_MONTH)),
                            LocalDate.now())
                    .getYears();

            Log.i("age", Integer.toString(age));

            if (Objects.equals(userInfo.gender, "男")) {
                BMR = 66 + (13.7 * userInfo.weight + 5 * userInfo.height - 6.8 * age);
            } else if (Objects.equals(userInfo.gender, "女")){
                BMR = 655 + (9.6 * userInfo.weight + 1.8 * userInfo.height - 4.7 * age);
            }

            Log.i("BMR", String.valueOf(BMR));
            Log.i("ActiveLevel", String.valueOf(viewModel.getGoalActiveLevelNu().getValue().ActiveLevel));
            Log.i("Goal", String.valueOf(viewModel.getGoalActiveLevelNu().getValue().Goal));

            switch(viewModel.getGoalActiveLevelNu().getValue().ActiveLevel) {
                case 0:  //低度
                    TDEE = BMR * 1.2;
                    break;
                case 1:  //中度
                    TDEE = BMR * 1.4;
                    break;
                case 2:  //高度
                    TDEE = BMR * 1.6;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + activeLevel);
            }

            switch(viewModel.getGoalActiveLevelNu().getValue().Goal) {
                case 0:  //減脂
                    proteinRatio = 0.35;
                    carbsRatio = 0.4;
                    fatRatio = 0.25;
                    TDEE *= 0.9;
                    break;
                case 1:  //維持
                    proteinRatio = 0.2;
                    carbsRatio = 0.5;
                    fatRatio = 0.3;
                    break;
                case 2:  //增肌
                    proteinRatio = 0.25;
                    carbsRatio = 0.55;
                    fatRatio = 0.2;
                    TDEE *= 1.1;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + goal);
            }

            Log.i("TDEE", String.valueOf((int)TDEE));


            dietStatus.CaloriesPerDay = (int)TDEE;
            dietStatus.ProteinPerDay = viewModel.oneDecimal(dietStatus.CaloriesPerDay * proteinRatio / 4);
            dietStatus.CarbsPerDay = viewModel.oneDecimal(dietStatus.CaloriesPerDay * carbsRatio / 4);
            dietStatus.FatPerDay = viewModel.oneDecimal(dietStatus.CaloriesPerDay * fatRatio / 9);
        }
        else {
            dietStatus.CaloriesPerDay = nudata.calorie;
            dietStatus.ProteinPerDay = nudata.protein;
            dietStatus.CarbsPerDay = nudata.carbohy;
            dietStatus.FatPerDay = nudata.fat;
        }

        double ratio;

        Calendar currentTime = Calendar.getInstance();
        Calendar noon = Calendar.getInstance();
        noon.set(Calendar.HOUR_OF_DAY, 12);
        noon.set(Calendar.MINUTE, 0);
        if (currentTime.before(noon)) {
            ratio = 0.2;
        } else {
            ratio = 0.4;
        }

        dietStatus.CaloriesPerMeal = (int)(dietStatus.CaloriesPerDay * ratio);  //早:午:晚 = 2:4:4
        dietStatus.ProteinPerMeal = viewModel.oneDecimal(dietStatus.ProteinPerDay * ratio);
        dietStatus.CarbsPerMeal = viewModel.oneDecimal(dietStatus.CarbsPerDay * ratio);
        dietStatus.FatPerMeal = viewModel.oneDecimal(dietStatus.FatPerDay * ratio);

        return dietStatus;
    }

    public void updateBoard(DietStatus dietStatus) {
        caloriesHad.setText(String.format("%s", dietStatus.CaloriesAchieved));
        caloriesProgress.setProgress((int)((double)dietStatus.CaloriesAchieved / dietStatus.CaloriesPerMeal * 360));
        updateRingColor();
        proteinHad.setText(String.format("%s / %s g", String.format("%s", dietStatus.ProteinAchieved), String.format("%s", dietStatus.ProteinPerMeal)));
        proteinProgress.setProgress((int)(dietStatus.ProteinAchieved / dietStatus.ProteinPerMeal * 100));
        updateBarColor(dietStatus.ProteinAchieved, dietStatus.ProteinPerMeal, proteinProgress, proteinHad);
        carbsHad.setText(String.format("%s / %s g", String.format("%s", dietStatus.CarbsAchieved), String.format("%s", dietStatus.CarbsPerMeal)));
        carbsProgress.setProgress((int)(dietStatus.CarbsAchieved / dietStatus.CarbsPerMeal * 100));
        updateBarColor(dietStatus.CarbsAchieved, dietStatus.CarbsPerMeal, carbsProgress, carbsHad);
        fatHad.setText(String.format("%s / %s g", String.format("%s", dietStatus.FatAchieved), String.format("%s", dietStatus.FatPerMeal)));
        fatProgress.setProgress((int)(dietStatus.FatAchieved / dietStatus.FatPerMeal * 100));
        updateBarColor(dietStatus.FatAchieved, dietStatus.FatPerMeal, fatProgress, fatHad);
    }

    public void updateRingColor() {
        double fillRate = (double)viewModel.getDietStatus().getValue().CaloriesAchieved / viewModel.getDietStatus().getValue().CaloriesPerMeal;
        if (fillRate < 0.75) {
            caloriesProgress.setProgressDrawable(getResources().getDrawable(R.drawable.progressring_green));
            caloriesHad.setTextColor(getResources().getColor(R.color.green_7ec972));
        } else if (fillRate < 1) {
            caloriesProgress.setProgressDrawable(getResources().getDrawable(R.drawable.progressring_yellow));
            caloriesHad.setTextColor(getResources().getColor(R.color.yellow_f7b400));
        } else {
            caloriesProgress.setProgressDrawable(getResources().getDrawable(R.drawable.progressring_red));
            caloriesHad.setTextColor(getResources().getColor(R.color.red_fa7c6b));
        }
    }

    public void updateBarColor(double achieved, double goal, ProgressBar progress, TextView text) {
        double fillRate = achieved / goal;
        if (fillRate < 0.75) {
            progress.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_green));
            text.setTextColor(getResources().getColor(R.color.green_7ec972));
        } else if (fillRate < 1) {
            progress.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_yellow));
            text.setTextColor(getResources().getColor(R.color.yellow_f7b400));
        } else {
            progress.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_red));
            text.setTextColor(getResources().getColor(R.color.red_fa7c6b));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void update() {
        DietStatus dietStatus = viewModel.getDietStatus().getValue();
        updateBoard(dietStatus);
        refreshList(dietStatus.CaloriesPerMeal, dietStatus.CaloriesAchieved);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void refreshList(int caloriesPerMeal, int caloriesAchieved) {
        int caloriesDifference = caloriesPerMeal - caloriesAchieved;
        int threshold = Math.max(caloriesDifference, 0);
        noResult.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

//        foodData = viewModel.getCon().getFoodData(threshold);
        List<FoodInfo> newFoodData = CaloriesFilter(viewModel.getFoodData(), threshold);
        Log.v("OK", "食物資料已回傳");
        Log.i("foodData", newFoodData.toString());

        mView.post(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            customList = new CustomList(requireContext(), this, newFoodData);
            lvShow.setAdapter(customList);
            if (newFoodData.isEmpty()) {
                noResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<FoodInfo> CaloriesFilter(List<FoodInfo> list, int threshold) {
        List<FoodInfo> newList = new ArrayList<>();
        list.forEach(item -> {
            if (item.calories <= threshold) {
                newList.add(item);
            }
        });
        return newList;
    }
}
