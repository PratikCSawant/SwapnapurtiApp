package com.example.swapnapurtiapp;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIInterface {
    @Headers("Content-Type: application/json")
    @POST("/add-personal-form")
    Call<String> AddPersonalInfo(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/add-oralexam-form")
    Call<String> AddOralExam(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/add-breast-exam")
    Call<String> AddBreastExam(@Body String body);
}
