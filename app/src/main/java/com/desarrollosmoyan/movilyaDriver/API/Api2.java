package com.desarrollosmoyan.movilyaDriver.API;
import com.desarrollosmoyan.movilyaDriver.Model.BankAccountResponse;
import com.desarrollosmoyan.movilyaDriver.Model.CityResponse;
import com.desarrollosmoyan.movilyaDriver.Models.ChatResponse;
import com.desarrollosmoyan.movilyaDriver.Models.DocumentResponse;
import com.desarrollosmoyan.movilyaDriver.Models.Errorresponse;
import com.desarrollosmoyan.movilyaDriver.Models.ListcheclResponse;
import com.desarrollosmoyan.movilyaDriver.Models.Servicelistresponse;
import com.desarrollosmoyan.movilyaDriver.Models.UpdateserviceResponse;
import com.desarrollosmoyan.movilyaDriver.Models.UserResponse;
import com.desarrollosmoyan.movilyaDriver.Models.docResposne;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Api2 {

    @FormUrlEncoded
    @POST("driverloginbyphone")
    Call<UserResponse> userlogin(
            @Field("phone") String phone
    );


    @FormUrlEncoded
    @POST("startorder")
    Call<Errorresponse> startorder(
            @Field("id") String id
    );

    @GET("getallservices")
    Call<Servicelistresponse> getallservices();

    @FormUrlEncoded
    @POST("updateservice")
    Call<UpdateserviceResponse> updateservice(
            @Field("service_id") String service_id,
            @Field("provider_id") String provider_id
    );

    @FormUrlEncoded
    @POST("deleteservice")
    Call<UpdateserviceResponse> deleteservice(
            @Field("service_id") String service_id,
            @Field("provider_id") String provider_id
    );

    @GET("checkservicelist/{service_id}/{provider_id}")
    Call<ListcheclResponse> checklistservices(
            @Path("service_id") String service_id,
            @Path("provider_id") String provider_id

    );
    @GET("getalldoc/{ids}")
    Call<DocumentResponse> getalldocs(
            @Path("ids") String getPID
    );

    @GET("getbankaccount/{id}")
    Call<BankAccountResponse> getbankaccount(
            @Path("id") String id
    );
    @FormUrlEncoded
    @POST("addwithdraw")
    Call<Errorresponse> addwithdraw(
            @Field("id") String id,
            @Field("bid") String bid,
            @Field("amount") String amount);

    @FormUrlEncoded
    @POST("addbankaccount")
    Call<Errorresponse> addbankaccount(
            @Field("name") String name,
            @Field("type") String type,
            @Field("bankname") String bankname,
            @Field("accountnumber") String accountnumber,
            @Field("ifsc") String ifsc,
            @Field("micr") String micr,
            @Field("id") String id,
            @Field("country") String country,
            @Field("currency") String currency
    );


    @GET("getcity")
    Call<CityResponse> getcity();

    @Multipart
    @POST("uploaddoc")
    Call<docResposne> uploaddoc(
            @Part MultipartBody.Part image,
            @Part("pid") RequestBody pid,
            @Part("did") RequestBody did
    );
    @FormUrlEncoded
    @POST("addchat")
    Call<Errorresponse> addchat(
            @Field("booking_id") String booking_id,
            @Field("uid") String uid,
            @Field("pid") String pid,
            @Field("message") String message,
            @Field("type") String type
    );

    @GET("getchat/{booking_id}")
    Call<ChatResponse> getchat(
            @Path("booking_id") String booking_id
    );





}
